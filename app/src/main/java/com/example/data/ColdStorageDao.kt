package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ColdStorageDao {

    @Query("SELECT * FROM cold_storage_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<ColdStorageEntry>>

    @Query("""
        SELECT * FROM cold_storage_entries 
        WHERE farmerName LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%' 
           OR bookingNo LIKE '%' || :query || '%' 
           OR srNo LIKE '%' || :query || '%' 
           OR doNo LIKE '%' || :query || '%' 
           OR village LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchEntries(query: String): Flow<List<ColdStorageEntry>>

    @Query("SELECT * FROM cold_storage_entries WHERE id = :id LIMIT 1")
    fun getEntryById(id: Long): Flow<ColdStorageEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ColdStorageEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<ColdStorageEntry>)

    @Update
    suspend fun updateEntry(entry: ColdStorageEntry)

    @Delete
    suspend fun deleteEntry(entry: ColdStorageEntry)

    @Query("DELETE FROM cold_storage_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM cold_storage_entries")
    fun getCount(): Flow<Int>

    @Query("SELECT SUM(bags) FROM cold_storage_entries")
    fun getTotalBags(): Flow<Int?>

    @Query("SELECT bookingNo FROM cold_storage_entries ORDER BY id DESC LIMIT 1")
    suspend fun getLatestBookingNo(): String?

    @Query("SELECT srNo FROM cold_storage_entries ORDER BY id DESC LIMIT 1")
    suspend fun getLatestSrNo(): String?
}
