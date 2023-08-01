package com.example.sunnyweather
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
