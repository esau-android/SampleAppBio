package com.biocatch.dialogasfragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.biocatch.client.android.sdk.BioCatchClient
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        SDKManager.changeContext("MainActivity")
        findViewById<AppCompatButton>(R.id.clickMeButton).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                findViewById<ConstraintLayout>(R.id.mainFormLayout).visibility = View.GONE
                changeFragment(MainFragment())
                CoroutineScope(Dispatchers.Main + SDKManager.handler).launch {
                    withTimeout(50L) {
                        Log.e("status","--> ${BioCatchClient.state}")
                    }
                    }
            }
        })
    }

    private fun changeFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.placeHolder, fragment)
        ft.commit()
    }
}