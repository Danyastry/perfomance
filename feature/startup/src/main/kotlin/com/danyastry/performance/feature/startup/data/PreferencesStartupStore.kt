package com.danyastry.performance.feature.startup.data

import android.content.Context
import androidx.core.content.edit
import com.danyastry.performance.feature.startup.model.InitMode
import com.danyastry.performance.feature.startup.model.StartupSample

class PreferencesStartupStore(context: Context) : StartupStore {

    private val preferences = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    override var mode: InitMode
        get() = preferences.getString(KEY_MODE, null)?.let { name -> InitMode.entries.firstOrNull { it.name == name } } ?: InitMode.EAGER
        set(value) = preferences.edit(commit = true) { putString(KEY_MODE, value.name) }

    override fun samples(): List<StartupSample> =
        preferences.getString(KEY_SAMPLES, "").orEmpty().lines().mapNotNull(SampleCodec::decode)

    override fun add(sample: StartupSample) {
        val lines = (samples() + sample).takeLast(MAX_SAMPLES).joinToString("\n", transform = SampleCodec::encode)
        preferences.edit(commit = true) { putString(KEY_SAMPLES, lines) }
    }

    override fun clear() = preferences.edit(commit = true) { remove(KEY_SAMPLES) }

    internal companion object {
        const val FILE = "startup_lab"
        const val KEY_MODE = "mode"
        const val KEY_SAMPLES = "samples"
        const val MAX_SAMPLES = 60
    }
}
