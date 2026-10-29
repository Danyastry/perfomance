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
