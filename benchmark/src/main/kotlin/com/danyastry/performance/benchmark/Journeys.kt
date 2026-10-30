package com.danyastry.performance.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.danyastry.performance.core.contract.LabIds
import com.danyastry.performance.core.contract.LabTags

const val TARGET_PACKAGE = "com.danyastry.performance"
private const val WAIT_MILLIS = 5_000L
private const val LOAD_WAIT_MILLIS = 15_000L
private const val FLINGS = 4

fun MacrobenchmarkScope.waitForCatalog() {
    device.require(By.res(LabTags.CATALOG_LIST), "the catalog list")
}

fun MacrobenchmarkScope.openLab(labId: String) {
    val list = device.require(By.res(LabTags.CATALOG_LIST), "the catalog list")
    val row = By.res(LabTags.catalogRow(labId))
    if (!device.hasObject(row)) list.scrollUntil(Direction.DOWN, Until.findObject(row))
    device.require(row, "the catalog row for $labId").click()
}

fun MacrobenchmarkScope.openColdStartScreen() {
    openLab(LabIds.COLD_START)
    device.require(By.res(LabTags.COLD_START_RESTART), "the restart button")
}

fun MacrobenchmarkScope.openScrollingScreen(modeTag: String) {
    openLab(LabIds.LIST_SCROLLING)
    device.require(By.res(modeTag), "the scrolling mode $modeTag").click()
    device.require(By.res(LabTags.FEED_LIST), "the feed")
}

fun MacrobenchmarkScope.flingFeed() {
    val feed = device.require(By.res(LabTags.FEED_LIST), "the feed")
    feed.setGestureMargin(device.displayWidth / 5)
    repeat(FLINGS) {
        feed.fling(Direction.DOWN)
        device.waitForIdle()
    }
}

fun MacrobenchmarkScope.openMainThreadScreen(modeTag: String, parserTag: String) {
    openLab(LabIds.MAIN_THREAD)
    device.require(By.res(modeTag), "the load mode $modeTag").click()
    device.require(By.res(parserTag), "the parser $parserTag").click()
    device.require(By.res(LabTags.MAIN_THREAD_LOAD), "the load button")
}

fun MacrobenchmarkScope.loadReceipt() {
    check(!device.hasObject(By.res(LabTags.MAIN_THREAD_REPORT))) { "A load report is already on screen before loading" }
    device.require(By.res(LabTags.MAIN_THREAD_LOAD), "the load button").click()
    device.require(By.res(LabTags.MAIN_THREAD_REPORT), "the load report", LOAD_WAIT_MILLIS)
}

private fun UiDevice.require(selector: BySelector, what: String, timeoutMillis: Long = WAIT_MILLIS): UiObject2 =
    wait(Until.findObject(selector), timeoutMillis) ?: throw AssertionError("Could not find $what within $timeoutMillis ms")
