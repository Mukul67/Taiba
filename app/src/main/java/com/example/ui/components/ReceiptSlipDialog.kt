package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ColdStorageEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptSlipDialog(
    entry: ColdStorageEntry,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val entryDateStr = remember(entry.entryDate) {
        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(entry.entryDate))
    }

    val totalRent = entry.bags * entry.rentPerBag
    val dueAmount = (totalRent - entry.advancePaid).coerceAtLeast(0.0)

    val slipText = """
        তামান্না স্পেশালাইজড কোল্ড স্টোরেজ
        তালন্দ, তানোর, রাজশাহী
        মোবাইল: ০১৭xxxxxxxx
        ----------------------------------
        বুকিং নং: ${entry.bookingNo}
        এসআর (SR) নং: ${entry.srNo}
        তারিখ: $entryDateStr
        ----------------------------------
        কৃষক/ব্যবসায়ী: ${entry.farmerName}
        মোবাইল: ${entry.phone}
        গ্রাম/ঠিকানা: ${if (entry.village.isNotBlank()) entry.village else "তানোর, রাজশাহী"}
        ----------------------------------
        আলুর জাত: ${entry.potatoType}
        মোট বস্তা: ${entry.bags} বস্তা
        চেম্বার/তলা: ${if (entry.chamber.isNotBlank()) entry.chamber else "-"}
        স্ট্যাটাস: ${entry.status}
        খালাসকৃত: ${entry.releasedBags} বস্তা
        অবশিষ্ট মজুত: ${entry.remainingBags} বস্তা
        ${if (entry.doNo.isNotBlank()) "ডিও (DO) নং: ${entry.doNo}" else ""}
        ----------------------------------
        ভাড়া হার: ৳${entry.rentPerBag.toInt()} / বস্তা
        মোট হিসাব: ৳${totalRent.toInt()}
        অগ্রিম জমা: ৳${entry.advancePaid.toInt()}
        বকেয়া প্রদেয়: ৳${dueAmount.toInt()}
        ----------------------------------
        ধন্যবাদ! কোল্ড স্টোরেজে আলু নিরাপদে রাখুন।
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("receipt_slip_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "বুকিং রশিদ ও চালান",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Official Cold Storage Voucher Card
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFAFAFA),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(8.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Header
                        Text(
                            text = "তামান্না স্পেশালাইজড কোল্ড স্টোরেজ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "তালন্দ, তানোর, রাজশাহী",
                            fontSize = 12.sp,
                            color = Color(0xFF616161),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "আলু সংরক্ষণ ও ডেলিভারি গেটপাস",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE0E0E0))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Voucher meta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("বুকিং নং: ${entry.bookingNo}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("এসআর নং: ${entry.srNo}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("এন্ট্রি তারিখ: $entryDateStr", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Farmer info
                        Text("কৃষক/ব্যবসায়ী: ${entry.farmerName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("মোবাইল: ${entry.phone}", fontSize = 12.sp)
                        if (entry.village.isNotBlank()) {
                            Text("গ্রাম/ঠিকানা: ${entry.village}", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Stock specs
                        SlipRow(label = "আলুর জাত:", value = entry.potatoType)
                        SlipRow(label = "মোট বস্তা:", value = "${entry.bags} বস্তা")
                        if (entry.chamber.isNotBlank()) {
                            SlipRow(label = "চেম্বার / তলা:", value = entry.chamber)
                        }
                        SlipRow(label = "বর্তমান স্ট্যাটাস:", value = entry.status)
                        if (entry.releasedBags > 0) {
                            SlipRow(label = "খালাসকৃত বস্তা:", value = "${entry.releasedBags} বস্তা")
                            SlipRow(label = "মজুত অবশিষ্ট:", value = "${entry.remainingBags} বস্তা")
                        }
                        if (entry.doNo.isNotBlank()) {
                            SlipRow(label = "ডিও (DO) নং:", value = entry.doNo)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Financials
                        SlipRow(label = "বস্তা প্রতি ভাড়া:", value = "৳ ${entry.rentPerBag.toInt()}")
                        SlipRow(label = "মোট ভাড়া প্রাক্কলন:", value = "৳ ${totalRent.toInt()}")
                        if (entry.advancePaid > 0) {
                            SlipRow(label = "অগ্রিম পরিশোধ:", value = "৳ ${entry.advancePaid.toInt()}")
                            SlipRow(label = "অবশিষ্ট বকেয়া:", value = "৳ ${dueAmount.toInt()}")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Signature line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(1.dp)
                                        .background(Color.Gray)
                                )
                                Text("গ্রাহকের স্বাক্ষর", fontSize = 10.sp, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(1.dp)
                                        .background(Color.Gray)
                                )
                                Text("ম্যানেজারের স্বাক্ষর", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, slipText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "বুকিং রশিদ শেয়ার করুন")
                    context.startActivity(shareIntent)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("share_receipt_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("রশিদ শেয়ার করুন")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("বন্ধ করুন")
            }
        }
    )
}

@Composable
fun SlipRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF616161))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
    }
}
