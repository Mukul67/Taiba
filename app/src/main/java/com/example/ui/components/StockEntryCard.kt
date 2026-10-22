package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ColdStorageEntry
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.CardinalColor
import com.example.ui.theme.DiamantColor
import com.example.ui.theme.GranolaColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockEntryCard(
    entry: ColdStorageEntry,
    onOpenReleaseDialog: (ColdStorageEntry) -> Unit,
    onOpenReceipt: (ColdStorageEntry) -> Unit,
    onEdit: (ColdStorageEntry) -> Unit,
    onDelete: (ColdStorageEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    val statusColor = when (entry.status) {
        ColdStorageEntry.STATUS_DELIVERED -> Color(0xFF2E7D32)
        ColdStorageEntry.STATUS_PARTIAL -> Color(0xFFEF6C00)
        else -> Color(0xFF1565C0)
    }

    val statusBg = when (entry.status) {
        ColdStorageEntry.STATUS_DELIVERED -> Color(0xFFE8F5E9)
        ColdStorageEntry.STATUS_PARTIAL -> Color(0xFFFFF3E0)
        else -> Color(0xFFE3F2FD)
    }

    val varietyColor = when {
        entry.potatoType.contains("কার্ডিনাল") -> CardinalColor
        entry.potatoType.contains("গ্রানুলোবা") -> GranolaColor
        entry.potatoType.contains("ডায়মন্ড") -> DiamantColor
        else -> AmberTertiary
    }

    val dateFormatted = remember(entry.entryDate) {
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        sdf.format(Date(entry.entryDate))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stock_entry_card_${entry.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Booking No, SR No & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = entry.bookingNo,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFECEFF1)
                    ) {
                        Text(
                            text = entry.srNo,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF37474F)
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusBg
                    ) {
                        Text(
                            text = entry.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "মেনু",
                                tint = Color.Gray
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("রশিদ / চালান দেখুন") },
                                onClick = {
                                    menuExpanded = false
                                    onOpenReceipt(entry)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Receipt, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("এন্ট্রি সম্পাদনা করুন") },
                                onClick = {
                                    menuExpanded = false
                                    onEdit(entry)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("মুছে ফেলুন", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete(entry)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Farmer & Phone Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.farmerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF212121)
                        )
                    )
                    if (entry.village.isNotBlank()) {
                        Text(
                            text = "গ্রাম/ঠিকানা: ${entry.village}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF616161)
                            )
                        )
                    }
                }

                // Call Button
                if (entry.phone.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${entry.phone}")
                                }
                                context.startActivity(intent)
                            }
                            .testTag("call_farmer_${entry.id}"),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "কল করুন",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = entry.phone,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Grid: Potato Type, Bag Count, Chamber, DO No
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8F9FA)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Potato Type
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(varietyColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "জাত: ",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                            Text(
                                text = entry.potatoType,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF263238)
                                )
                            )
                        }

                        // Bag counts
                        Text(
                            text = "মোট: ${entry.bags} বস্তা",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF263238)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Progress if partial or released
                    if (entry.releasedBags > 0) {
                        val progress = if (entry.bags > 0) entry.releasedBags.toFloat() / entry.bags else 0f
                        Column {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color(0xFFE0E0E0),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "খালাস: ${entry.releasedBags} বস্তা",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "অবশিষ্ট: ${entry.remainingBags} বস্তা",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "স্টোরে মজুত: ${entry.remainingBags} বস্তা",
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                            if (entry.chamber.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warehouse,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = entry.chamber,
                                        fontSize = 11.sp,
                                        color = Color(0xFF546E7A)
                                    )
                                }
                            }
                        }
                    }

                    if (entry.doNo.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ডিও (DO) নং: ${entry.doNo}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: DO Delivery & Receipt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DO Release button if remaining bags > 0
                if (entry.remainingBags > 0) {
                    FilledTonalButton(
                        onClick = { onOpenReleaseDialog(entry) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_do_release_${entry.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "খালাস (DO)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "সম্পূর্ণ খালাসকৃত",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                // Slip / Receipt Button
                OutlinedButton(
                    onClick = { onOpenReceipt(entry) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_receipt_${entry.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "রশিদ / চালান",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
