package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    // Užtikrinama, kad paketo vardas atitinka tikrąjį applicationId ("com.aistudio.textmetrics.kxmpzq")
    val expectedApplicationId = BuildConfig.APPLICATION_ID
    assertEquals(expectedApplicationId, appContext.packageName)
    assertEquals("com.aistudio.textmetrics.kxmpzq", appContext.packageName)
  }
}

