package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ColdStorageEntry

@Composable
fun FilterAndSearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    selectedVariety: String,
    onVarietySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusOptions = listOf(
        "সব",
        ColdStorageEntry.STATUS_STORED,
        ColdStorageEntry.STATUS_DELIVERED,
        ColdStorageEntry.STATUS_PARTIAL
    )

    val varietyOptions = listOf("সব জাত") + ColdStorageEntry.POTATO_VARIETIES

    Column(modifier = modifier.fillMaxWidth()) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_field"),
            placeholder = { Text("খুঁজুন (নাম, মোবাইল, বুকিং নং, এসআর, ডিও...)") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFCFD8DC)
            )
        )

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            statusOptions.forEach { status ->
                val isSelected = selectedStatus == status
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusSelected(status) },
                    label = {
                        Text(
                            text = status,
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_status_$status")
                )
            }
        }

        // Variety Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            varietyOptions.forEach { variety ->
                val isSelected = selectedVariety == variety
                FilterChip(
                    selected = isSelected,
                    onClick = { onVarietySelected(variety) },
                    label = {
                        Text(
                            text = variety,
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_variety_$variety")
                )
            }
        }
    }
}
