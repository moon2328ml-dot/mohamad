package ir.bordermanager.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.MarkUnreadChatAlt
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import ir.bordermanager.data.CargoStatus
import ir.bordermanager.data.Truck

private enum class MessageFilter(val title: String) {
    ACTIVE("تخلیه‌نشده"),
    UNLOADED("تخلیه‌شده"),
    ALL("هر دو")
}

private data class PhoneContact(val phone: String, val trucks: List<Truck>) {
    val hasActive: Boolean get() = trucks.any { it.status != CargoStatus.UNLOADED }
    val hasUnloaded: Boolean get() = trucks.any { it.status == CargoStatus.UNLOADED }
    val representative: Truck get() = trucks.first()
}

@Composable
fun MessagingScreen(vm: AppViewModel, cargoId: Long, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rev by vm.repo.revision.collectAsState()
    val cargo = remember(rev, cargoId) { vm.repo.db.getCargoType(cargoId) }
    val trucks = remember(rev, cargoId) { vm.repo.db.listTrucksForCargo(cargoId) }
    val contacts = remember(trucks) {
        trucks.asSequence()
            .filter { it.phone.isNotBlank() }
            .groupBy { normalizePhone(it.phone) }
            .filterKeys { it.isNotBlank() }
            .map { (phone, grouped) -> PhoneContact(phone, grouped) }
            .sortedBy { it.representative.plate }
    }
    var filter by remember(cargoId) { mutableStateOf(MessageFilter.ACTIVE) }
    var selectedPhones by remember(cargoId) { mutableStateOf<Set<String>>(emptySet()) }
    var selectionInitialized by remember(cargoId) { mutableStateOf(false) }
    var message by remember(cargoId) { mutableStateOf("") }
    var showConfirm by remember(cargoId) { mutableStateOf(false) }
    var permissionMessage by remember(cargoId) { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result[Manifest.permission.SEND_SMS] == true ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        ) {
            showConfirm = true
            permissionMessage = ""
        } else {
            permissionMessage = "مجوز ارسال پیامک داده نشد. از تنظیمات گوشی اجازه ارسال پیامک را فعال کن."
        }
    }

    LaunchedEffect(contacts) {
        if (!selectionInitialized) {
            selectedPhones = contacts.filter { it.hasActive }.map { it.phone }.toSet()
            selectionInitialized = true
        } else {
            selectedPhones = selectedPhones.intersect(contacts.map { it.phone }.toSet())
        }
    }

    val visibleContacts = contacts.filter {
        when (filter) {
            MessageFilter.ACTIVE -> it.hasActive
            MessageFilter.UNLOADED -> it.hasUnloaded
            MessageFilter.ALL -> true
        }
    }

    fun applyFilter(next: MessageFilter) {
        filter = next
        selectedPhones = visibleContactsFor(contacts, next).map { it.phone }.toSet()
    }

    if (cargo == null) {
        EmptyState("نوع بار پیدا نشد")
        return
    }

    Column(modifier.fillMaxSize()) {
        ScreenTitle("پیامک گروهی", onBack = { vm.back() })
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PurpleSoft)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(cargo.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                        Text("شماره‌ها از تماس‌های ثبت‌شده‌ی ماشین‌های همین پارت جمع می‌شوند.", fontSize = 12.sp)
                        Text("تخلیه‌شده‌ها در حالت پیش‌فرض انتخاب نمی‌شوند.", fontSize = 12.sp, color = Purple)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
                    MessageFilter.values().forEach { option ->
                        FilterChip(
                            selected = filter == option,
                            onClick = { applyFilter(option) },
                            label = { Text(option.title, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("${visibleContacts.size} شماره • ${selectedPhones.size} انتخاب‌شده", fontSize = 12.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = { selectedPhones = visibleContacts.map { it.phone }.toSet() }) {
                        Icon(Icons.Default.Checklist, null, Modifier.size(17.dp)); Spacer(Modifier.size(3.dp)); Text("انتخاب همه")
                    }
                    TextButton(onClick = { selectedPhones = emptySet() }) {
                        Icon(Icons.Default.ClearAll, null, Modifier.size(17.dp)); Spacer(Modifier.size(3.dp)); Text("پاک‌کردن")
                    }
                }
            }
            if (visibleContacts.isEmpty()) {
                item { EmptyState("برای این فیلتر شماره‌ای ثبت نشده است") }
            } else {
                items(visibleContacts, key = { it.phone }) { contact ->
                    val checked = contact.phone in selectedPhones
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { enabled ->
                                    selectedPhones = if (enabled) selectedPhones + contact.phone else selectedPhones - contact.phone
                                }
                            )
                            Column(Modifier.weight(1f)) {
                                Text(contact.phone, fontWeight = FontWeight.Bold)
                                Text(
                                    contact.representative.driverName.ifBlank { "پلاک ${contact.representative.plate}" },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                                )
                            }
                            Text(
                                when {
                                    contact.hasActive && contact.hasUnloaded -> "فعال و تخلیه‌شده"
                                    contact.hasUnloaded -> "تخلیه‌شده"
                                    else -> "تخلیه‌نشده"
                                },
                                fontSize = 10.sp,
                                color = if (contact.hasUnloaded && !contact.hasActive) ParkingRed else WaitingGreen
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("متن پیام مشترک") },
                    placeholder = { Text("خبر یا توضیح موردنظر را بنویس...") },
                    minLines = 4
                )
            }
            item {
                Button(
                    onClick = {
                        when {
                            selectedPhones.isEmpty() -> permissionMessage = "حداقل یک شماره را انتخاب کن."
                            message.isBlank() -> permissionMessage = "متن پیام را وارد کن."
                            ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ->
                                permissionLauncher.launch(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS))
                            else -> showConfirm = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.MarkUnreadChatAlt, null)
                    Spacer(Modifier.size(7.dp))
                    Text("ارسال پیام به ${selectedPhones.size} شماره")
                }
            }
            if (permissionMessage.isNotBlank()) {
                item { Text(permissionMessage, color = ParkingRed, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
            item { Spacer(Modifier.height(10.dp)) }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("تأیید ارسال پیام") },
            text = { Text("این پیام برای ${selectedPhones.size} شماره ارسال می‌شود. ادامه می‌دهی؟") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    sendSms(context, selectedPhones.toList(), message.trim())
                }) { Text("ارسال") }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("انصراف") } }
        )
    }
}

private fun visibleContactsFor(contacts: List<PhoneContact>, filter: MessageFilter): List<PhoneContact> = contacts.filter {
    when (filter) {
        MessageFilter.ACTIVE -> it.hasActive
        MessageFilter.UNLOADED -> it.hasUnloaded
        MessageFilter.ALL -> true
    }
}

private fun normalizePhone(raw: String): String {
    val fa = "۰۱۲۳۴۵۶۷۸۹"
    val ar = "٠١٢٣٤٥٦٧٨٩"
    return raw.map { ch ->
        val faIndex = fa.indexOf(ch)
        val arIndex = ar.indexOf(ch)
        when {
            faIndex >= 0 -> ('0'.code + faIndex).toChar()
            arIndex >= 0 -> ('0'.code + arIndex).toChar()
            else -> ch
        }
    }.filter { it.isDigit() || it == '+' }.joinToString("")
}

private fun sendSms(context: Context, recipients: List<String>, body: String) {
    val sms = SmsManager.getDefault()
    var sent = 0
    recipients.distinct().forEach { phone ->
        try {
            val parts = sms.divideMessage(body)
            if (parts.size == 1) sms.sendTextMessage(phone, null, body, null, null)
            else sms.sendMultipartTextMessage(phone, null, parts, null, null)
            sent++
        } catch (_: Throwable) {
            // Keep sending to the remaining numbers if one number is invalid.
        }
    }
    Toast.makeText(context, "پیام برای $sent شماره ارسال شد", Toast.LENGTH_LONG).show()
}
