package com.danyastry.performance.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
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
