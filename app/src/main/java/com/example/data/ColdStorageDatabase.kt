package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ColdStorageEntry::class], version = 1, exportSchema = false)
abstract class ColdStorageDatabase : RoomDatabase() {

    abstract fun coldStorageDao(): ColdStorageDao

    companion object {
        @Volatile
        private var INSTANCE: ColdStorageDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): ColdStorageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColdStorageDatabase::class.java,
                    "tamanna_cold_storage.db"
                )
                    .addCallback(ColdStorageDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class ColdStorageDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(database.coldStorageDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: ColdStorageDao) {
                val currentTime = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L

                val sampleEntries = listOf(
                    ColdStorageEntry(
                        bookingNo = "BK-2026-001",
                        srNo = "SR-1001",
                        farmerName = "মোঃ রফিকুল ইসলাম",
                        phone = "01712345678",
                        village = "তালন্দ, তানোর",
                        potatoType = "কার্ডিনাল",
                        bags = 150,
                        chamber = "চেম্বার ১ (২য় তলা)",
                        status = ColdStorageEntry.STATUS_STORED,
                        releasedBags = 0,
                        rentPerBag = 280.0,
                        advancePaid = 10000.0,
                        entryDate = currentTime - (12 * oneDay),
                        remarks = "উন্নত মানের বীজ আলু"
                    ),
                    ColdStorageEntry(
                        bookingNo = "BK-2026-002",
                        srNo = "SR-1002",
                        farmerName = "মোঃ আনিসুর রহমান",
                        phone = "01823456789",
                        village = "কলমা, তানোর",
                        potatoType = "গ্রানুলোবা",
                        bags = 220,
                        chamber = "চেম্বার ২ (১ম তলা)",
                        status = ColdStorageEntry.STATUS_STORED,
                        releasedBags = 0,
                        rentPerBag = 280.0,
                        advancePaid = 15000.0,
                        entryDate = currentTime - (8 * oneDay),
                        remarks = "খাবার আলু, গ্রেড-এ"
                    ),
                    ColdStorageEntry(
                        bookingNo = "BK-2026-003",
                        srNo = "SR-1003",
                        farmerName = "শ্রী বিমল চন্দ্র সরকার",
                        phone = "01734567890",
                        village = "কামারগাঁ, তানোর",
                        potatoType = "ডায়মন্ড",
                        bags = 100,
                        chamber = "চেম্বার ১ (৩য় তলা)",
                        doNo = "DO-5021",
                        status = ColdStorageEntry.STATUS_DELIVERED,
                        releasedBags = 100,
                        rentPerBag = 280.0,
                        advancePaid = 28000.0,
                        entryDate = currentTime - (25 * oneDay),
                        releaseDate = currentTime - (2 * oneDay),
                        remarks = "সম্পূর্ণ খালাস সম্পন্ন হয়েছে"
                    ),
                    ColdStorageEntry(
                        bookingNo = "BK-2026-004",
                        srNo = "SR-1004",
                        farmerName = "হাজী মোঃ মোশাররফ হোসেন",
                        phone = "01945678901",
                        village = "মুণ্ডুমালা, তানোর",
                        potatoType = "স্টিক / অন্যান্য",
                        bags = 180,
                        chamber = "চেম্বার ৩ (২য় তলা)",
                        status = ColdStorageEntry.STATUS_STORED,
                        releasedBags = 0,
                        rentPerBag = 280.0,
                        advancePaid = 12000.0,
                        entryDate = currentTime - (4 * oneDay),
                        remarks = "বীজ আলু লট-বি"
                    ),
                    ColdStorageEntry(
                        bookingNo = "BK-2026-005",
                        srNo = "SR-1005",
                        farmerName = "মোঃ জহুরুল ইসলাম",
                        phone = "01756789012",
                        village = "তালন্দ বাজার, তানোর",
                        potatoType = "কার্ডিনাল",
                        bags = 300,
                        chamber = "চেম্বার ২ (৩য় তলা)",
                        doNo = "DO-5022",
                        status = ColdStorageEntry.STATUS_PARTIAL,
                        releasedBags = 120,
                        rentPerBag = 280.0,
                        advancePaid = 35000.0,
                        entryDate = currentTime - (18 * oneDay),
                        releaseDate = currentTime - (1 * oneDay),
                        remarks = "১২০ বস্তা ডেলিভারি হয়েছে, বাকি ১৮০ বস্তা স্টোরে আছে"
                    )
                )
                dao.insertAll(sampleEntries)
            }
        }
    }
}
