package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ColdStorageEntry

@Composable
fun DeliveryOrderDialog(
    entry: ColdStorageEntry,
    releaseDoNo: String,
    onReleaseDoNoChanged: (String) -> Unit,
    releaseBags: String,
    onReleaseBagsChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val releaseBagsInt = releaseBags.toIntOrNull() ?: 0
    val totalRent = releaseBagsInt * entry.rentPerBag
    val netDue = (totalRent - entry.advancePaid).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("delivery_order_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "আলু খালাস ও ডিও (DO) প্রদান",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Farmer Summary Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F8E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "বুকিং: ${entry.bookingNo}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "এসআর: ${entry.srNo}",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF37474F),
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "কৃষক: ${entry.farmerName} (${entry.phone})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "আলুর জাত: ${entry.potatoType} | বর্তমান মজুত: ${entry.remainingBags} বস্তা",
                            fontSize = 13.sp,
                            color = Color(0xFF558B2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // DO Number Field
                OutlinedTextField(
                    value = releaseDoNo,
                    onValueChange = onReleaseDoNoChanged,
                    label = { Text("ডিও (DO) নং *") },
                    placeholder = { Text("যেমন: DO-5023") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_release_do_no"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bags to Release Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = releaseBags,
                        onValueChange = onReleaseBagsChanged,
                        label = { Text("খালাসকৃত বস্তা সংখ্যা *") },
                        placeholder = { Text("সর্বোচ্চ ${entry.remainingBags}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_release_bags"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onReleaseBagsChanged(entry.remainingBags.toString()) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("সব", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Estimated Bill preview
                if (releaseBagsInt > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF8E1),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ভাড়া (${releaseBagsInt} × ৳${entry.rentPerBag.toInt()}):", fontSize = 12.sp)
                                Text("৳ ${totalRent.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            if (entry.advancePaid > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("জমা/অগ্রিম কর্তন:", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                    Text("- ৳ ${entry.advancePaid.toInt()}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("মোট প্রদেয় বিল:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("৳ ${netDue.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_release_button")
            ) {
                Text("খালাস নিশ্চিত করুন")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("বাতিল")
            }
        }
    )
}
