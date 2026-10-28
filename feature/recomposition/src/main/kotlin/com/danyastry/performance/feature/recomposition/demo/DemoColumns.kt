package com.danyastry.performance.feature.recomposition.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DemoColumns(problem: @Composable () -> Unit, fixed: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Problem", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
            problem()
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Fixed", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            fixed()
        }
    }
}
