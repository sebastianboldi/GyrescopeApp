package com.tribeone.gyrescopeapp.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tribeone.gyrescopeapp.R


class IntroActivity: AppCompatActivity() {

    private lateinit var etUrl: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        etUrl = findViewById(R.id.et_stream_url)
        button = findViewById(R.id.button)

        button.setOnClickListener {
            if(isNetworkConnected()){
                val url = etUrl.text.trim().toString()
                if(url.isNotEmpty()) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("url", url )
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Please add stream url", Toast.LENGTH_LONG ).show()
                }
            }else{
                Toast.makeText(this, "Please connect device with the internet", Toast.LENGTH_LONG ).show()
            }
        }

    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

}