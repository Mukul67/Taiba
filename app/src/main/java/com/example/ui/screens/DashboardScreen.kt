package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ColdStorageEntry
import com.example.ui.ColdStorageViewModel
import com.example.ui.components.ColdStorageHeader
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.DeliveryOrderDialog
import com.example.ui.components.FilterAndSearchBar
import com.example.ui.components.NewEntryFormCard
import com.example.ui.components.ReceiptSlipDialog
import com.example.ui.components.StockEntryCard
import com.example.ui.theme.CardinalColor
import com.example.ui.theme.DiamantColor
import com.example.ui.theme.GranolaColor
import kotlinx.coroutines.flow.collectLatest

enum class DashboardTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    STOCK_LIST("স্টক ও এন্ট্রি", Icons.Default.Inventory2),
    NEW_ENTRY("নতুন এন্ট্রি", Icons.Default.EditNote),
    ANALYTICS("হিসাব-নিকাশ", Icons.Default.BarChart)
}

@Composable
fun DashboardScreen(
    viewModel: ColdStorageViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(DashboardTab.STOCK_LIST.ordinal) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val selectedVariety by viewModel.selectedVarietyFilter.collectAsStateWithLifecycle()

    val selectedReleaseEntry by viewModel.selectedEntryForRelease.collectAsStateWithLifecycle()
    val selectedReceiptEntry by viewModel.selectedEntryForReceipt.collectAsStateWithLifecycle()
    val entryToDelete by viewModel.entryToDelete.collectAsStateWithLifecycle()
    val editingEntry by viewModel.selectedEntryForEdit.collectAsStateWithLifecycle()

    val releaseDoNo by viewModel.releaseDoNo.collectAsStateWithLifecycle()
    val releaseBags by viewModel.releaseBags.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // If an item is selected for editing, switch tab to NEW_ENTRY
    LaunchedEffect(editingEntry) {
        if (editingEntry != null) {
            selectedTab = DashboardTab.NEW_ENTRY.ordinal
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                DashboardTab.values().forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == DashboardTab.STOCK_LIST.ordinal) {
                FloatingActionButton(
                    onClick = {
                        viewModel.cancelEdit()
                        selectedTab = DashboardTab.NEW_ENTRY.ordinal
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_new_entry")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "নতুন এন্ট্রি")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("নতুন এন্ট্রি", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                DashboardTab.STOCK_LIST.ordinal -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("stock_list_lazy_column"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Branding Header & Quick Stats
                        item {
                            ColdStorageHeader(stats = stats)
                        }

                        // Search and Filter controls
                        item {
                            FilterAndSearchBar(
                                searchQuery = searchQuery,
                                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                selectedStatus = selectedStatus,
                                onStatusSelected = viewModel::onStatusFilterChanged,
                                selectedVariety = selectedVariety,
                                onVarietySelected = viewModel::onVarietyFilterChanged
                            )
                        }

                        // Section Title: সাম্প্রতিক স্টক ও এন্ট্রি তালিকা
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সাম্প্রতিক স্টক ও এন্ট্রি তালিকা",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF263238)
                                    )
                                )
                                Text(
                                    text = "${filteredEntries.size} টি রেকর্ড",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF78909C),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // Empty State if no records
                        if (filteredEntries.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "কোনো এন্ট্রি পাওয়া যায়নি!",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "অনুগ্রহ করে অনুসন্ধানের শব্দ পরিবর্তন করুন বা নতুন এন্ট্রি যুক্ত করুন।",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // List of Entries
                        items(
                            items = filteredEntries,
                            key = { it.id }
                        ) { entry ->
                            StockEntryCard(
                                entry = entry,
                                onOpenReleaseDialog = viewModel::openReleaseDialog,
                                onOpenReceipt = viewModel::openReceiptDialog,
                                onEdit = viewModel::startEditEntry,
                                onDelete = viewModel::promptDelete
                            )
                        }

                        // Bottom space so FAB doesn't obscure content
                        item {
                            Spacer(modifier = Modifier.height(56.dp))
                        }
                    }
                }

                DashboardTab.NEW_ENTRY.ordinal -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("new_entry_screen_column"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            ColdStorageHeader(stats = stats)
                        }

                        item {
                            NewEntryFormCard(
                                formState = formState,
                                isEditing = editingEntry != null,
                                onBookingNoChange = viewModel::onBookingNoChanged,
                                onSrNoChange = viewModel::onSrNoChanged,
                                onFarmerNameChange = viewModel::onFarmerNameChanged,
                                onPhoneChange = viewModel::onPhoneChanged,
                                onVillageChange = viewModel::onVillageChanged,
                                onPotatoTypeChange = viewModel::onPotatoTypeChanged,
                                onBagsChange = viewModel::onBagsChanged,
                                onChamberChange = viewModel::onChamberChanged,
                                onDoNoChange = viewModel::onDoNoChanged,
                                onStatusChange = viewModel::onStatusChanged,
                                onAdvanceChange = viewModel::onAdvancePaidChanged,
                                onRemarksChange = viewModel::onRemarksChanged,
                                onSubmit = {
                                    viewModel.saveEntry()
                                    selectedTab = DashboardTab.STOCK_LIST.ordinal
                                },
                                onCancelEdit = {
                                    viewModel.cancelEdit()
                                    selectedTab = DashboardTab.STOCK_LIST.ordinal
                                }
                            )
                        }
                    }
                }

                DashboardTab.ANALYTICS.ordinal -> {
                    AnalyticsView(
                        stats = stats,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    // Delivery Order Dialog
    selectedReleaseEntry?.let { entry ->
        DeliveryOrderDialog(
            entry = entry,
            releaseDoNo = releaseDoNo,
            onReleaseDoNoChanged = viewModel::onReleaseDoNoChanged,
            releaseBags = releaseBags,
            onReleaseBagsChanged = viewModel::onReleaseBagsChanged,
            onDismiss = viewModel::closeReleaseDialog,
            onConfirm = viewModel::confirmDoRelease
        )
    }

    // Receipt Slip Dialog
    selectedReceiptEntry?.let { entry ->
        ReceiptSlipDialog(
            entry = entry,
            onDismiss = viewModel::closeReceiptDialog
        )
    }

    // Delete Confirmation Dialog
    entryToDelete?.let { entry ->
        DeleteConfirmDialog(
            entry = entry,
            onDismiss = viewModel::dismissDelete,
            onConfirm = viewModel::confirmDelete
        )
    }
}

@Composable
fun AnalyticsView(
    stats: com.example.ui.StorageStats,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ColdStorageHeader(stats = stats)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "আলুর জাতভিত্তিক বর্তমান মজুত হিসাব",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "কোল্ড স্টোরেজে সংরক্ষিত মোট আলুর জাতভিত্তিক বস্তার হিসাব ও অনুপাত:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ColdStorageEntry.POTATO_VARIETIES.forEach { variety ->
                        val count = stats.varietyCounts.getOrDefault(variety, 0)
                        val ratio = if (stats.totalStoredBags > 0) count.toFloat() / stats.totalStoredBags else 0f
                        val varietyColor = when {
                            variety.contains("কার্ডিনাল") -> CardinalColor
                            variety.contains("গ্রানুলোবা") -> GranolaColor
                            variety.contains("ডায়মন্ড") -> DiamantColor
                            else -> Color(0xFF6A1B9A)
                        }

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(varietyColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = variety,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = "$count বস্তা (${(ratio * 100).toInt()}%)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF37474F)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = varietyColor,
                                trackColor = Color(0xFFEEEEEE),
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "স্টোরেজ ও খালাস সারসংক্ষেপ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val releaseRatio = if (stats.totalAllBags > 0) {
                        stats.totalDeliveredBags.toFloat() / stats.totalAllBags
                    } else 0f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("সর্বমোট রিসিভড বস্তা:", fontSize = 13.sp)
                        Text("${stats.totalAllBags} বস্তা", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("বর্তমানে কোল্ড চেম্বারে মজুত:", fontSize = 13.sp, color = Color(0xFF2E7D32))
                        Text("${stats.totalStoredBags} বস্তা", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ডিও (DO) খালাস সম্পন্ন:", fontSize = 13.sp, color = Color(0xFFE65100))
                        Text("${stats.totalDeliveredBags} বস্তা", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { releaseRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Color(0xFFE65100),
                        trackColor = Color(0xFFC8E6C9),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "মোট আলুর ${(releaseRatio * 100).toInt()}% খালাস হয়েছে, বাকি ${(100 - (releaseRatio * 100).toInt())}% স্টোরে সংরক্ষিত রয়েছে।",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
