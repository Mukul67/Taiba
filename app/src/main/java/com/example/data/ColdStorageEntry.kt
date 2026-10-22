package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cold_storage_entries")
data class ColdStorageEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingNo: String,
    val srNo: String,
    val farmerName: String,
    val phone: String,
    val village: String = "",
    val potatoType: String,
    val bags: Int,
    val chamber: String = "",
    val doNo: String = "",
    val status: String = STATUS_STORED,
    val releasedBags: Int = 0,
    val entryDate: Long = System.currentTimeMillis(),
    val releaseDate: Long? = null,
    val rentPerBag: Double = 280.0,
    val advancePaid: Double = 0.0,
    val remarks: String = ""
) {
    val remainingBags: Int
        get() = (bags - releasedBags).coerceAtLeast(0)

    val isDelivered: Boolean
        get() = status == STATUS_DELIVERED || (releasedBags >= bags && bags > 0)

    companion object {
        const val STATUS_STORED = "স্টোরে জমা আছে"
        const val STATUS_DELIVERED = "খালাস হয়েছে (DO)"
        const val STATUS_PARTIAL = "আংশিক খালাস"

        val POTATO_VARIETIES = listOf(
            "কার্ডিনাল",
            "গ্রানুলোবা",
            "ডায়মন্ড",
            "স্টিক / অন্যান্য"
        )
    }
}
