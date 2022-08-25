package com.biocatch.dialogasfragment

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.annotation.MainThread
import com.biocatch.client.android.sdk.BioCatchClient
import com.biocatch.client.android.sdk.contract.ExtendedOptions
import com.biocatch.client.android.sdk.contract.LogLevel
import com.biocatch.client.android.sdk.contract.State
import com.biocatch.client.android.sdk.contract.events.INewSessionStartedEventListener
import com.biocatch.client.android.sdk.contract.events.IStateChangedEventListener
import com.biocatch.client.android.sdk.contract.events.NewSessionStartedEvent
import com.biocatch.client.android.sdk.contract.events.StateChangedEvent
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

object SDKManager : Observable() {

    const val SDK_TAG = "BioCatch SDKManager"
    const val ACTION = "android.intent.action.COCREA"

    private var csid: String? = null
    private var uuid: String? = null
    private var cid: String? = null

     val handler = CoroutineExceptionHandler {_, exception ->
        Log.e("HandlerC","CoroutineExceptionHandler got $exception")
    }

    private fun invokeCoroutine(action : () -> Unit) {
        CoroutineScope(Dispatchers.Main + handler).launch {
            withTimeout(50L){
                try {
                    action.invoke()
                }
                catch (e: java.lang.Exception){
                    Log.e("invokeCoroutine","CoroutineExceptionHandler got $e")
                }
            }
        }
    }

    init {
        invokeCoroutine {
            // Sets the SDK's log level. Verbose mode will be a good choice for debugging.
            BioCatchClient.setLogLevel(LogLevel.VERBOSE)

            // Register this manager to listen to callbacks from the SDK.
            // IStateChangedEventListener listens for state change events, indicating Start/Stop etc...
            BioCatchClient.addEventListener(object : IStateChangedEventListener {
                override fun onStateChanged(state: StateChangedEvent?) {
                    setChanged()
                    notifyObservers(state)
                }
            })
            // INewSessionStartedEventListener listens for session states and updates us if a new
            // session has started.
            BioCatchClient.addEventListener(object : INewSessionStartedEventListener {
                override fun onNewSessionStarted(session: NewSessionStartedEvent?) {
                    setChanged()
                    notifyObservers(session)
                }
            })
        }
    }

    @MainThread
    fun getVersion() = BioCatchClient.version

    fun getCSId() = csid

    fun getCid() = cid

    fun getUUId(): String {
        if (uuid.isNullOrEmpty()) {
            uuid = UUID.randomUUID().toString()
        }
        return uuid!!
    }

    /**
     * Starts the SDK and data collection.
     * This method should only be called from the main thread.
     *
     * @param serverUrl - The WUP server url
     * @param customerID - The unique company id
     * @param application - the app's application instance
     * @param extendedOptions - additional configuration options
     *
     * the SDK throws exception if any error occur during initialization so we handle them gracefully and log
     * the error to console.
     */
    fun start(
        serverUrl: String,
        customerID: String,
        application: Application,
        csid: String? = null,
        extendedOptions: ExtendedOptions = ExtendedOptions(),
        activity: Activity? = null
    ) {
        Log.e(SDK_TAG, "Start csid: $csid")
                try {
                    BioCatchClient.start(
                        serverUrl,
                        customerID,
                        application,
                        csid,
                        extendedOptions,
                        activity
                    )
                    SDKManager.csid = csid
                    cid = customerID
                    Log.d(SDK_TAG, "Start csid: $csid")
                } catch (e: Exception) {
                    Log.e(SDK_TAG, "Error starting BioCatch client ${e.message}")
                }
    }

    /**
     * Tells the SDK to use a new session id.
     *
     * This function must be called each time the user is starting a sensitive-area
     * application flow, for example - when the user is logged out of his account by the
     * system (perhaps a time-out?) or if he logged out by himself, we should call this function
     * to update the SDK that from now on - it's consider a new session.
     *
     * @param customerSessionId - the new customer session id to use.
     */
    @MainThread
    fun updateCsid(customerSessionId: String, contextName: String) {
        Log.d(SDK_TAG, "csid: $csid  context: $contextName")
        // Check state to prevent errors
        invokeCoroutine {

        if (BioCatchClient.state == State.STARTED) {
            try {
                BioCatchClient.updateCustomerSessionID(customerSessionId)
                changeContext(contextName)
                csid = customerSessionId
            } catch (e: Exception) {
                Log.e(SDK_TAG, "Error updating customer session id: ${e.message}")
            }
        } else {
            Log.e(SDK_TAG, "Error updating customer session id: the sdk is the STOPPED state.")
        }
        }
    }

    /**
     * Updates the SDK about the what part of the app is currently going through.
     * This helps to build a better journey for the user.
     *
     * @param contextName - a meaningful identifier of the user's current location in the
     * app. Don't use runtime class name as they can be Obfuscated and thus meaningless - use
     * plain strings like "Main Page Login" or "Transactions Page".
     */
    @MainThread
    fun changeContext(contextName: String) {
        // Check state to prevent errors
        invokeCoroutine {
            if (BioCatchClient.state == State.STARTED || BioCatchClient.state == State.STARTING) {
                try {
                    BioCatchClient.changeContext(contextName)
                } catch (e: Exception) {
                    Log.e(SDK_TAG, "Error changing context: ${e.message}")
                }
            } else {
                Log.e(SDK_TAG, "Error changing context: the sdk is the STOPPED state.")
            }
        }
    }


}