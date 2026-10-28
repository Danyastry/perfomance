package com.danyastry.performance.core.ui

import androidx.annotation.StringRes

@get:StringRes
val FeatureCategory.titleRes: Int
    get() = when (this) {
        FeatureCategory.STARTUP -> R.string.ui_category_startup
        FeatureCategory.COMPOSE -> R.string.ui_category_compose
        FeatureCategory.SCROLLING -> R.string.ui_category_scrolling
        FeatureCategory.MEMORY -> R.string.ui_category_memory
        FeatureCategory.UNDER_THE_HOOD -> R.string.ui_category_under_the_hood
    }

@get:StringRes
val FeatureCategory.descriptionRes: Int
    get() = when (this) {
        FeatureCategory.STARTUP -> R.string.ui_category_startup_description
        FeatureCategory.COMPOSE -> R.string.ui_category_compose_description
        FeatureCategory.SCROLLING -> R.string.ui_category_scrolling_description
        FeatureCategory.MEMORY -> R.string.ui_category_memory_description
        FeatureCategory.UNDER_THE_HOOD -> R.string.ui_category_under_the_hood_description
    }
