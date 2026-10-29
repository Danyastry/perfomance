package com.danyastry.performance.feature.scrolling.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danyastry.performance.feature.scrolling.data.FeedSource
import com.danyastry.performance.feature.scrolling.data.SampleImage
import com.danyastry.performance.feature.scrolling.jank.JankCounter
import com.danyastry.performance.feature.scrolling.logic.RowWork
import com.danyastry.performance.feature.scrolling.model.FeedItem
import com.danyastry.performance.feature.scrolling.model.ListMode
import com.danyastry.performance.feature.scrolling.model.ProductRowModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScrollingViewModel(
    worker: CoroutineDispatcher = Dispatchers.Default,
    private val loadImage: () -> ByteArray? = SampleImage::jpeg,
) : ViewModel() {

    val items: List<FeedItem> = FeedSource.items()
    val counter = JankCounter()

    private val _mode = MutableStateFlow(ListMode.HEAVY)
    val mode: StateFlow<ListMode> = _mode.asStateFlow()

    private val _models = MutableStateFlow<Map<Int, ProductRowModel>>(emptyMap())
    val models: StateFlow<Map<Int, ProductRowModel>> = _models.asStateFlow()

    private val _image = MutableStateFlow(SampleImage.jpegOrNull())
    val image: StateFlow<ByteArray?> = _image.asStateFlow()

    init {
        viewModelScope.launch {
            if (_image.value == null) _image.value = withContext(worker) { loadImage() }
        }
        viewModelScope.launch {
            _models.value = withContext(worker) {
                items.filterIsInstance<FeedItem.Product>().associate { it.id to RowWork.model(it) }
            }
        }
    }

    fun onModeChange(mode: ListMode) {
        _mode.value = mode
        counter.reset()
    }

    fun onResetJank() = counter.reset()
}
