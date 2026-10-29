package com.danyastry.performance.feature.scrolling.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.danyastry.performance.feature.scrolling.data.ThumbnailCache
import com.danyastry.performance.feature.scrolling.logic.ImageDecoding
import com.danyastry.performance.feature.scrolling.logic.RowWork
import com.danyastry.performance.feature.scrolling.model.FeedItem
import com.danyastry.performance.feature.scrolling.model.ProductRowModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HeaderRow(header: FeedItem.Header) {
    Text(
        header.title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
fun HeavyProductRow(product: FeedItem.Product, jpeg: ByteArray?) {
    val model = RowWork.model(product)
    val thumbnail = jpeg?.let(ImageDecoding::fullSizeThenScale)
    ProductRow(model, thumbnail)
}

@Composable
fun OptimizedProductRow(model: ProductRowModel, jpeg: ByteArray?) {
    val thumbnail by produceState(initialValue = ThumbnailCache.cached(model.id), model.id, jpeg) {
        if (value == null && jpeg != null) value = withContext(Dispatchers.Default) { ThumbnailCache.load(model.id, jpeg) }
    }
    ProductRow(model, thumbnail)
}

@Composable
private fun ProductRow(model: ProductRowModel, thumbnail: ImageBitmap?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (thumbnail != null) {
            Image(bitmap = thumbnail, contentDescription = null, modifier = Modifier.size(THUMBNAIL_DP.dp))
        } else {
            Box(Modifier.size(THUMBNAIL_DP.dp).background(MaterialTheme.colorScheme.surfaceVariant))
        }
        Column {
            Text(model.title, style = MaterialTheme.typography.bodyLarge)
            Text(model.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private const val THUMBNAIL_DP = 56
