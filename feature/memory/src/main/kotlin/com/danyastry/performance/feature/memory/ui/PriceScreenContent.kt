package com.danyastry.performance.feature.memory.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.danyastry.performance.feature.memory.R

@Composable
fun PriceScreenContent(title: String, explanation: String, cents: Long, onPublish: () -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.safeDrawingPadding().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.memory_price, cents / 100.0), style = MaterialTheme.typography.displaySmall)
            Button(onClick = onPublish) { Text(stringResource(R.string.memory_publish)) }
            Text(explanation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
