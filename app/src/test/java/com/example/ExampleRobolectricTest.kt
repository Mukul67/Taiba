package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ColdStorageEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tamanna Cold Storage", appName)
  }

  @Test
  fun `verify cold storage entry calculations`() {
    val entry = ColdStorageEntry(
      bookingNo = "BK-2026-001",
      srNo = "SR-1001",
      farmerName = "মোঃ রফিকুল ইসলাম",
      phone = "01712345678",
      potatoType = "কার্ডিনাল",
      bags = 150,
      releasedBags = 50,
      status = ColdStorageEntry.STATUS_PARTIAL
    )

    assertEquals(100, entry.remainingBags)
    assertFalse(entry.isDelivered)

    val fullyDelivered = entry.copy(
      releasedBags = 150,
      status = ColdStorageEntry.STATUS_DELIVERED
    )
    assertEquals(0, fullyDelivered.remainingBags)
    assertTrue(fullyDelivered.isDelivered)
  }
}
