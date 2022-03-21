package com.louis.myservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.louis.myservice.databinding.ActivityMainBinding
import com.louis.myservice.MyBoundService.MyBinder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mServiceBound = false
    private lateinit var mBoundService: MyBoundService

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as MyBinder
            mBoundService = myBinder.getService
            mServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnStartService.setOnClickListener {
                Intent(this@MainActivity, MyService::class.java).also {
                    startService(it)
                }
            }

            btnStartJobIntentService.setOnClickListener {
                Intent(this@MainActivity, MyJobIntentService::class.java).apply {
                    putExtra(MyJobIntentService.EXTRA_DURATION, 5000L)
                }.also {
                    MyJobIntentService.enqueueWork(this@MainActivity, it)
                }
            }

            btnStartBoundService.setOnClickListener {
                Intent(this@MainActivity, MyBoundService::class.java).also {
                    bindService(it, mServiceConnection, BIND_AUTO_CREATE)
                }
            }

            btnStopBoundService.setOnClickListener {
                unbindService(mServiceConnection)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mServiceBound) {
            unbindService(mServiceConnection)
        }
    }
}