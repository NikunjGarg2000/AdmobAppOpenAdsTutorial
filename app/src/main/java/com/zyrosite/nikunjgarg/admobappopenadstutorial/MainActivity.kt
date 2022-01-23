package com.zyrosite.nikunjgarg.admobappopenadstutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val application = application
            // Show the app open ad.
            (application as MyApplication)
                .showAdIfAvailable(
                    this,
                    object : OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            Log.e(
                                "TAG",
                                "onAdShowedFullScreenContent."
                            )
                        }
                    })
        } catch (e: Exception) {
            Log.i("TAG", e.message.toString())
        }

    }
}