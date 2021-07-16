package com.example.kotlin_to_firebase

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val home = findViewById<TextView>(R.id.home_title)
        home.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD)
        Thread {
            try {
                Thread.sleep(1000)
                startActivity(Intent(this, Login::class.java))
                this.finish()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}