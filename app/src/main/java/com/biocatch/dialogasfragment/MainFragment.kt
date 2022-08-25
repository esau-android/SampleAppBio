package com.biocatch.dialogasfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        SDKManager.changeContext("MainFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatButton>(R.id.clickMeButton).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                view.findViewById<ConstraintLayout>(R.id.mainFormLayout).visibility = View.GONE
                val dialog = SecondFragment()
                dialog.show(childFragmentManager, "Dialog")
            }
        })
    }
}