package com.louis.myworkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.work.*
import com.louis.myworkmanager.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workManager = WorkManager.getInstance(this)
        binding.apply {
            btnOneTimeTask.setOnClickListener(this@MainActivity)
            btnPeriodicTask.setOnClickListener(this@MainActivity)
            btnCancelTask.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnOneTimeTask -> startOneTimeTask()
            R.id.btnPeriodicTask -> startPeriodicTask()
            R.id.btnCancelTask -> cancelPeriodicTask()
        }
    }

    private fun startPeriodicTask() {
        binding.textStatus.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, binding.editCity.text.toString())
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java,15, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.apply {
            enqueue(periodicWorkRequest)
            getWorkInfoByIdLiveData(periodicWorkRequest.id)
                .observe(this@MainActivity) { workInfo ->
                    val status = workInfo.state.name
                    binding.apply {
                        textStatus.append("\n" + status)
                        btnCancelTask.isEnabled = false
                        if (workInfo.state == WorkInfo.State.ENQUEUED) {
                            btnCancelTask.isEnabled = true
                        }
                    }
                }
        }
    }

    private fun cancelPeriodicTask() {
        workManager.cancelWorkById(periodicWorkRequest.id)
    }

    private fun startOneTimeTask() {
        binding.textStatus.text = getString(R.string.status)

        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, binding.editCity.text.toString())
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.apply {
            enqueue(oneTimeWorkRequest)
            getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
                .observe(this@MainActivity) {workInfo ->
                    val status = workInfo.state.name
                    binding.textStatus.append("\n" + status)
                }
        }
    }
}