package com.example.pworkmanager

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pworkmanager.ui.theme.PWorkManagerTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var workRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PWorkManagerTheme {
                Home()
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun Home() {

        var startWorker by remember {
            mutableStateOf(false)
        }
        val workManagerState = remember {
            mutableStateOf("")
        }

        var context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {

                Text(
                    text = "Workmanager Demo",
                    fontSize = 20.sp
                )

                Text(
                    text = "In this demo we have shown how to start workmanager and stop it based on unique tag details",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround) {
                    Button(onClick = { startWorker = true  }) {
                        Text(text = "Start")
                    }

                    Button(onClick = { stopWorker(context) }) {
                        Text(text = "Stop")
                    }
                }

                Text(
                    text = workManagerState.value,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if(startWorker)
            startWorker(context, workManagerState, lifecycleOwner)
    }

    private fun stopWorker(context: Context) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("Periodic Worker Call")

        Toast.makeText(context, "Stopped worker", Toast.LENGTH_SHORT).show()
    }

    private fun startWorker(
        context: Context,
        workManagerState: MutableState<String>,
        lifecycleOwner: LifecycleOwner
    ) {
        val workManager = WorkManager.getInstance(context)
            .enqueue(createWorkRequest())

        workManager.state.observe(
            lifecycleOwner, Observer { state ->
                workManagerState.value = state.toString()
            }
        )

        Toast.makeText(context, "Started worker", Toast.LENGTH_SHORT).show()

    }

    private fun createWorkRequest() : PeriodicWorkRequest {
        workRequest = PeriodicWorkRequestBuilder<MyWorker>(6, TimeUnit.HOURS)
            .setConstraints(createWorkConstraints())
            .setInitialDelay(3, TimeUnit.SECONDS)
            .addTag("Periodic Worker Call")
            .build()
        return workRequest
    }

    private fun createWorkConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
    }



}


