package com.danyastry.performance.catalog

import com.danyastry.performance.core.ui.FeatureCategory
import com.danyastry.performance.core.ui.FeatureEntry

data class CatalogSection(
    val category: FeatureCategory,
    val entries: List<FeatureEntry>,
)
