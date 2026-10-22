package com.example.data

import kotlinx.coroutines.flow.Flow

class ColdStorageRepository(private val dao: ColdStorageDao) {

    val allEntries: Flow<List<ColdStorageEntry>> = dao.getAllEntries()

    fun searchEntries(query: String): Flow<List<ColdStorageEntry>> {
        return if (query.isBlank()) {
            dao.getAllEntries()
        } else {
            dao.searchEntries(query.trim())
        }
    }

    fun getEntryById(id: Long): Flow<ColdStorageEntry?> = dao.getEntryById(id)

    suspend fun insert(entry: ColdStorageEntry): Long = dao.insertEntry(entry)

    suspend fun update(entry: ColdStorageEntry) = dao.updateEntry(entry)

    suspend fun delete(entry: ColdStorageEntry) = dao.deleteEntry(entry)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun getNextSuggestedBookingNo(): String {
        val latest = dao.getLatestBookingNo()
        if (latest.isNullOrBlank()) return "BK-2026-001"
        return try {
            val parts = latest.split("-")
            val num = parts.last().toIntOrNull() ?: 0
            val nextNum = num + 1
            "BK-2026-%03d".format(nextNum)
        } catch (e: Exception) {
            "BK-2026-001"
        }
    }

    suspend fun getNextSuggestedSrNo(): String {
        val latest = dao.getLatestSrNo()
        if (latest.isNullOrBlank()) return "SR-1001"
        return try {
            val numPart = latest.replace(Regex("[^0-9]"), "")
            val num = numPart.toIntOrNull() ?: 1000
            val nextNum = num + 1
            "SR-$nextNum"
        } catch (e: Exception) {
            "SR-1001"
        }
    }
}
