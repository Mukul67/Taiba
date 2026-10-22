package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ColdStorageEntry
import com.example.ui.FormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryFormCard(
    formState: FormState,
    isEditing: Boolean,
    onBookingNoChange: (String) -> Unit,
    onSrNoChange: (String) -> Unit,
    onFarmerNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onVillageChange: (String) -> Unit,
    onPotatoTypeChange: (String) -> Unit,
    onBagsChange: (String) -> Unit,
    onChamberChange: (String) -> Unit,
    onDoNoChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onAdvanceChange: (String) -> Unit,
    onRemarksChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var potatoTypeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statusChoices = listOf(
        ColdStorageEntry.STATUS_STORED,
        ColdStorageEntry.STATUS_DELIVERED
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("new_entry_form_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Form Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (isEditing) "এন্ট্রি সংশোধন করুন" else "নতুন স্টক ও বুকিং এন্ট্রি",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (isEditing) {
                    OutlinedButton(
                        onClick = onCancelEdit,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("বাতিল")
                    }
                }
            }

            // Error Message Banner
            if (formState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formState.error,
                        color = Color(0xFFC62828),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: বুকিং নং & এসআর নং
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = formState.bookingNo,
                    onValueChange = onBookingNoChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_booking_no"),
                    label = { Text("বুকিং নং *") },
                    placeholder = { Text("BK-2026-001") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = formState.srNo,
                    onValueChange = onSrNoChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_sr_no"),
                    label = { Text("এসআর (SR) নং *") },
                    placeholder = { Text("SR-1001") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: কৃষক/ব্যবসায়ীর নাম
            OutlinedTextField(
                value = formState.farmerName,
                onValueChange = onFarmerNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_farmer_name"),
                label = { Text("কৃষক / ব্যবসায়ীর নাম *") },
                placeholder = { Text("নাম লিখুন") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: মোবাইল নম্বর & গ্রাম/ঠিকানা
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = formState.phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_phone"),
                    label = { Text("মোবাইল নম্বর *") },
                    placeholder = { Text("০১৭xxxxxxxx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = formState.village,
                    onValueChange = onVillageChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_village"),
                    label = { Text("গ্রাম / এলাকা") },
                    placeholder = { Text("তালন্দ, তানোর") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 4: আলুর জাত Dropdown & বস্তা সংখ্যা
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = potatoTypeExpanded,
                    onExpandedChange = { potatoTypeExpanded = !potatoTypeExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = formState.potatoType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("আলুর জাত *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = potatoTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .testTag("dropdown_potato_type"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = potatoTypeExpanded,
                        onDismissRequest = { potatoTypeExpanded = false }
                    ) {
                        ColdStorageEntry.POTATO_VARIETIES.forEach { variety ->
                            DropdownMenuItem(
                                text = { Text(variety) },
                                onClick = {
                                    onPotatoTypeChange(variety)
                                    potatoTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = formState.bags,
                    onValueChange = onBagsChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_bags"),
                    label = { Text("বস্তা সংখ্যা *") },
                    placeholder = { Text("যেমন: ১৫০") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 5: চেম্বার/তলা & অগ্রিম টাকা
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = formState.chamber,
                    onValueChange = onChamberChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_chamber"),
                    label = { Text("চেম্বার / তলা") },
                    placeholder = { Text("চেম্বার ১, ২য় তলা") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = formState.advancePaid,
                    onValueChange = onAdvanceChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_advance"),
                    label = { Text("অগ্রিম জমা (টাকা)") },
                    placeholder = { Text("যেমন: ৫০০০") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 6: ডিও নং (খালাসের সময়) & স্ট্যাটাস
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = formState.doNo,
                    onValueChange = onDoNoChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_do_no"),
                    label = { Text("ডিও (DO) নং (খালাসে)") },
                    placeholder = { Text("যেমন: DO-5001") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = formState.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("স্ট্যাটাস *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .testTag("dropdown_status"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusChoices.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption) },
                                onClick = {
                                    onStatusChange(statusOption)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Remarks field
            OutlinedTextField(
                value = formState.remarks,
                onValueChange = onRemarksChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_remarks"),
                label = { Text("মন্তব্য / নোট (ঐচ্ছিক)") },
                placeholder = { Text("যেমন: বীজ আলু, এ-গ্রেড") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_entry_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isEditing) "আপডেট সংরক্ষণ করুন" else "ডেটা সেইভ করুন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
