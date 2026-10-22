package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ColdStorageEntry
import com.example.data.ColdStorageRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormState(
    val bookingNo: String = "",
    val srNo: String = "",
    val farmerName: String = "",
    val phone: String = "",
    val village: String = "",
    val potatoType: String = ColdStorageEntry.POTATO_VARIETIES.first(),
    val bags: String = "",
    val chamber: String = "",
    val doNo: String = "",
    val status: String = ColdStorageEntry.STATUS_STORED,
    val rentPerBag: String = "280",
    val advancePaid: String = "0",
    val remarks: String = "",
    val error: String? = null
)

data class StorageStats(
    val totalEntriesCount: Int = 0,
    val totalStoredBags: Int = 0,
    val totalDeliveredBags: Int = 0,
    val totalAllBags: Int = 0,
    val varietyCounts: Map<String, Int> = emptyMap()
)

class ColdStorageViewModel(
    private val repository: ColdStorageRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("সব")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _selectedVarietyFilter = MutableStateFlow("সব জাত")
    val selectedVarietyFilter: StateFlow<String> = _selectedVarietyFilter.asStateFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    // Dialog & Action States
    private val _selectedEntryForRelease = MutableStateFlow<ColdStorageEntry?>(null)
    val selectedEntryForRelease: StateFlow<ColdStorageEntry?> = _selectedEntryForRelease.asStateFlow()

    private val _selectedEntryForReceipt = MutableStateFlow<ColdStorageEntry?>(null)
    val selectedEntryForReceipt: StateFlow<ColdStorageEntry?> = _selectedEntryForReceipt.asStateFlow()

    private val _selectedEntryForEdit = MutableStateFlow<ColdStorageEntry?>(null)
    val selectedEntryForEdit: StateFlow<ColdStorageEntry?> = _selectedEntryForEdit.asStateFlow()

    private val _entryToDelete = MutableStateFlow<ColdStorageEntry?>(null)
    val entryToDelete: StateFlow<ColdStorageEntry?> = _entryToDelete.asStateFlow()

    // DO Release Form Fields
    private val _releaseDoNo = MutableStateFlow("")
    val releaseDoNo: StateFlow<String> = _releaseDoNo.asStateFlow()

    private val _releaseBags = MutableStateFlow("")
    val releaseBags: StateFlow<String> = _releaseBags.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    val rawEntries: StateFlow<List<ColdStorageEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredEntries: StateFlow<List<ColdStorageEntry>> = combine(
        rawEntries,
        _searchQuery,
        _selectedStatusFilter,
        _selectedVarietyFilter
    ) { list, query, status, variety ->
        list.filter { entry ->
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                entry.farmerName.lowercase().contains(q) ||
                        entry.phone.contains(q) ||
                        entry.bookingNo.lowercase().contains(q) ||
                        entry.srNo.lowercase().contains(q) ||
                        entry.doNo.lowercase().contains(q) ||
                        entry.village.lowercase().contains(q)
            }
            val matchesStatus = if (status == "সব") true else entry.status == status
            val matchesVariety = if (variety == "সব জাত") true else entry.potatoType == variety
            matchesQuery && matchesStatus && matchesVariety
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<StorageStats> = rawEntries.combine(_searchQuery) { list, _ ->
        var stored = 0
        var delivered = 0
        var total = 0
        val varietyMap = mutableMapOf<String, Int>()

        for (item in list) {
            total += item.bags
            val rem = item.remainingBags
            stored += rem
            delivered += item.releasedBags

            val curr = varietyMap.getOrDefault(item.potatoType, 0)
            varietyMap[item.potatoType] = curr + rem
        }

        StorageStats(
            totalEntriesCount = list.size,
            totalStoredBags = stored,
            totalDeliveredBags = delivered,
            totalAllBags = total,
            varietyCounts = varietyMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StorageStats()
    )

    init {
        loadSuggestedNumbers()
    }

    fun loadSuggestedNumbers() {
        viewModelScope.launch {
            val nextBooking = repository.getNextSuggestedBookingNo()
            val nextSr = repository.getNextSuggestedSrNo()
            _formState.value = _formState.value.copy(
                bookingNo = nextBooking,
                srNo = nextSr
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(status: String) {
        _selectedStatusFilter.value = status
    }

    fun onVarietyFilterChanged(variety: String) {
        _selectedVarietyFilter.value = variety
    }

    // Form inputs
    fun onBookingNoChanged(value: String) {
        _formState.value = _formState.value.copy(bookingNo = value, error = null)
    }

    fun onSrNoChanged(value: String) {
        _formState.value = _formState.value.copy(srNo = value, error = null)
    }

    fun onFarmerNameChanged(value: String) {
        _formState.value = _formState.value.copy(farmerName = value, error = null)
    }

    fun onPhoneChanged(value: String) {
        _formState.value = _formState.value.copy(phone = value, error = null)
    }

    fun onVillageChanged(value: String) {
        _formState.value = _formState.value.copy(village = value)
    }

    fun onPotatoTypeChanged(value: String) {
        _formState.value = _formState.value.copy(potatoType = value)
    }

    fun onBagsChanged(value: String) {
        _formState.value = _formState.value.copy(bags = value, error = null)
    }

    fun onChamberChanged(value: String) {
        _formState.value = _formState.value.copy(chamber = value)
    }

    fun onDoNoChanged(value: String) {
        _formState.value = _formState.value.copy(doNo = value)
    }

    fun onStatusChanged(value: String) {
        _formState.value = _formState.value.copy(status = value)
    }

    fun onRentPerBagChanged(value: String) {
        _formState.value = _formState.value.copy(rentPerBag = value)
    }

    fun onAdvancePaidChanged(value: String) {
        _formState.value = _formState.value.copy(advancePaid = value)
    }

    fun onRemarksChanged(value: String) {
        _formState.value = _formState.value.copy(remarks = value)
    }

    fun saveEntry() {
        val current = _formState.value
        if (current.bookingNo.isBlank()) {
            _formState.value = current.copy(error = "বুকিং নম্বর আবশ্যক!")
            return
        }
        if (current.srNo.isBlank()) {
            _formState.value = current.copy(error = "এসআর (SR) নম্বর আবশ্যক!")
            return
        }
        if (current.farmerName.isBlank()) {
            _formState.value = current.copy(error = "কৃষক/ব্যবসায়ীর নাম লিখুন!")
            return
        }
        if (current.phone.isBlank()) {
            _formState.value = current.copy(error = "মোবাইল নম্বর লিখুন!")
            return
        }
        val bagsInt = current.bags.toIntOrNull()
        if (bagsInt == null || bagsInt <= 0) {
            _formState.value = current.copy(error = "সঠিক বস্তা সংখ্যা দিন!")
            return
        }

        viewModelScope.launch {
            val isEditing = _selectedEntryForEdit.value != null
            val rentDouble = current.rentPerBag.toDoubleOrNull() ?: 280.0
            val advanceDouble = current.advancePaid.toDoubleOrNull() ?: 0.0

            if (isEditing) {
                val old = _selectedEntryForEdit.value!!
                val updated = old.copy(
                    bookingNo = current.bookingNo.trim(),
                    srNo = current.srNo.trim(),
                    farmerName = current.farmerName.trim(),
                    phone = current.phone.trim(),
                    village = current.village.trim(),
                    potatoType = current.potatoType,
                    bags = bagsInt,
                    chamber = current.chamber.trim(),
                    doNo = current.doNo.trim(),
                    status = current.status,
                    rentPerBag = rentDouble,
                    advancePaid = advanceDouble,
                    remarks = current.remarks.trim()
                )
                repository.update(updated)
                _selectedEntryForEdit.value = null
                _toastEvent.emit("এন্ট্রি সফলভাবে আপডেট করা হয়েছে!")
            } else {
                val initialReleased = if (current.status == ColdStorageEntry.STATUS_DELIVERED) bagsInt else 0
                val newEntry = ColdStorageEntry(
                    bookingNo = current.bookingNo.trim(),
                    srNo = current.srNo.trim(),
                    farmerName = current.farmerName.trim(),
                    phone = current.phone.trim(),
                    village = current.village.trim(),
                    potatoType = current.potatoType,
                    bags = bagsInt,
                    chamber = current.chamber.trim(),
                    doNo = current.doNo.trim(),
                    status = current.status,
                    releasedBags = initialReleased,
                    rentPerBag = rentDouble,
                    advancePaid = advanceDouble,
                    remarks = current.remarks.trim()
                )
                val newId = repository.insert(newEntry)
                _toastEvent.emit("সফলভাবে এন্ট্রি সম্পন্ন হয়েছে! (বুকিং: ${current.bookingNo})")
            }

            // Reset form with new auto numbers
            val nextBooking = repository.getNextSuggestedBookingNo()
            val nextSr = repository.getNextSuggestedSrNo()
            _formState.value = FormState(
                bookingNo = nextBooking,
                srNo = nextSr
            )
        }
    }

    fun startEditEntry(entry: ColdStorageEntry) {
        _selectedEntryForEdit.value = entry
        _formState.value = FormState(
            bookingNo = entry.bookingNo,
            srNo = entry.srNo,
            farmerName = entry.farmerName,
            phone = entry.phone,
            village = entry.village,
            potatoType = entry.potatoType,
            bags = entry.bags.toString(),
            chamber = entry.chamber,
            doNo = entry.doNo,
            status = entry.status,
            rentPerBag = entry.rentPerBag.toInt().toString(),
            advancePaid = entry.advancePaid.toInt().toString(),
            remarks = entry.remarks
        )
    }

    fun cancelEdit() {
        _selectedEntryForEdit.value = null
        loadSuggestedNumbers()
    }

    // Delivery Order (DO) Release flow
    fun openReleaseDialog(entry: ColdStorageEntry) {
        _selectedEntryForRelease.value = entry
        _releaseDoNo.value = if (entry.doNo.isNotBlank()) entry.doNo else "DO-${(1000..9999).random()}"
        _releaseBags.value = entry.remainingBags.toString()
    }

    fun closeReleaseDialog() {
        _selectedEntryForRelease.value = null
    }

    fun onReleaseDoNoChanged(value: String) {
        _releaseDoNo.value = value
    }

    fun onReleaseBagsChanged(value: String) {
        _releaseBags.value = value
    }

    fun confirmDoRelease() {
        val entry = _selectedEntryForRelease.value ?: return
        val doNoVal = _releaseDoNo.value.trim()
        val releaseBagsInt = _releaseBags.value.toIntOrNull()

        if (doNoVal.isBlank()) {
            viewModelScope.launch { _toastEvent.emit("ডিও (DO) নং দিন!") }
            return
        }
        if (releaseBagsInt == null || releaseBagsInt <= 0 || releaseBagsInt > entry.remainingBags) {
            viewModelScope.launch { _toastEvent.emit("সঠিক বস্তা সংখ্যা দিন (সর্বোচ্চ ${entry.remainingBags} বস্তা)!") }
            return
        }

        viewModelScope.launch {
            val totalReleasedNow = entry.releasedBags + releaseBagsInt
            val newStatus = if (totalReleasedNow >= entry.bags) {
                ColdStorageEntry.STATUS_DELIVERED
            } else {
                ColdStorageEntry.STATUS_PARTIAL
            }

            val updated = entry.copy(
                doNo = doNoVal,
                releasedBags = totalReleasedNow,
                status = newStatus,
                releaseDate = System.currentTimeMillis()
            )
            repository.update(updated)
            _selectedEntryForRelease.value = null
            _toastEvent.emit("ডিও খালাস সফল হয়েছে! $releaseBagsInt বস্তা খালাস করা হলো।")
        }
    }

    // Receipt Dialog flow
    fun openReceiptDialog(entry: ColdStorageEntry) {
        _selectedEntryForReceipt.value = entry
    }

    fun closeReceiptDialog() {
        _selectedEntryForReceipt.value = null
    }

    // Delete flow
    fun promptDelete(entry: ColdStorageEntry) {
        _entryToDelete.value = entry
    }

    fun dismissDelete() {
        _entryToDelete.value = null
    }

    fun confirmDelete() {
        val entry = _entryToDelete.value ?: return
        viewModelScope.launch {
            repository.delete(entry)
            _entryToDelete.value = null
            _toastEvent.emit("রেকর্ডটি মুছে ফেলা হয়েছে!")
        }
    }
}

class ColdStorageViewModelFactory(
    private val repository: ColdStorageRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColdStorageViewModel::class.java)) {
            return ColdStorageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
