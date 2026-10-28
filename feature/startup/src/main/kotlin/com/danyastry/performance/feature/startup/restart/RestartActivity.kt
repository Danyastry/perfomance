package com.danyastry.performance.feature.startup.restart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process

class RestartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainPid = intent.getIntExtra(EXTRA_PID, -1)
        if (mainPid > 0) Process.killProcess(mainPid)
        packageManager.getLaunchIntentForPackage(packageName)?.let { launch ->
            startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        finish()
        Runtime.getRuntime().exit(0)
    }

    companion object {
        private const val EXTRA_PID = "pid"

        fun coldRestart(context: Context) {
            val intent = Intent(context, RestartActivity::class.java)
                .putExtra(EXTRA_PID, Process.myPid())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
