package com.danyastry.performance.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

const val TARGET_PACKAGE = "com.danyastry.performance"
private const val WAIT_MILLIS = 5_000L

fun MacrobenchmarkScope.waitForCatalog() {
    device.wait(Until.hasObject(By.text("Performance Lab")), WAIT_MILLIS)
}

fun MacrobenchmarkScope.openColdStartScreen() {
    device.wait(Until.hasObject(By.text("Cold start")), WAIT_MILLIS)
    device.findObject(By.text("Cold start"))?.click()
    device.wait(Until.hasObject(By.text("Restart and measure")), WAIT_MILLIS)
}

fun MacrobenchmarkScope.openScrollingScreen(mode: String) {
    device.wait(Until.hasObject(By.text("List scrolling")), WAIT_MILLIS)
    device.findObject(By.text("List scrolling"))?.click()
    device.wait(Until.hasObject(By.text(mode)), WAIT_MILLIS)
    device.findObject(By.text(mode))?.click()
    device.wait(Until.hasObject(By.res(FEED_LIST)), WAIT_MILLIS)
}

fun MacrobenchmarkScope.openMainThreadScreen(background: Boolean, parser: String? = null) {
    device.wait(Until.hasObject(By.text("Performance Lab")), WAIT_MILLIS)
    device.findObject(By.scrollable(true))?.scrollUntil(Direction.DOWN, Until.findObject(By.text(MAIN_THREAD_ENTRY)))
    device.findObject(By.text(MAIN_THREAD_ENTRY))?.click()
    device.wait(Until.hasObject(By.text(LOAD_RECEIPT)), WAIT_MILLIS)
    if (background) device.findObject(By.text("Background"))?.click()
    if (parser != null) device.findObject(By.text(parser))?.click()
}

fun MacrobenchmarkScope.loadReceipt() {
    device.findObject(By.text(LOAD_RECEIPT))?.click()
    device.wait(Until.hasObject(By.text(WORK_TIME)), LOAD_WAIT_MILLIS)
}

fun MacrobenchmarkScope.flingFeed() {
    val feed = device.findObject(By.res(FEED_LIST)) ?: return
    feed.setGestureMargin(device.displayWidth / 5)
    repeat(FLINGS) {
        feed.fling(Direction.DOWN)
        device.waitForIdle()
    }
}

private const val FEED_LIST = "feed_list"
private const val FLINGS = 4
private const val MAIN_THREAD_ENTRY = "The same receipt load on and off the main thread, with StrictMode, frame gaps and trace sections"
private const val LOAD_RECEIPT = "Load receipt"
private const val WORK_TIME = "Work time"
private const val LOAD_WAIT_MILLIS = 15_000L
