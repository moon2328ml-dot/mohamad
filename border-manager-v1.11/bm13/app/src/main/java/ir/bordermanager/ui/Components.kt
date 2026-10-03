package ir.bordermanager.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.bordermanager.data.Border
import ir.bordermanager.data.CargoStatus
import ir.bordermanager.data.DeclarationType
import ir.bordermanager.data.Truck
import ir.bordermanager.util.Formatters
import ir.bordermanager.util.Jalali

@Composable
fun ScreenTitle(title: String, onBack: (() -> Unit)? = null, action: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowForward, null, tint = Purple) }
        Text(title, modifier = Modifier.weight(1f), fontSize = 23.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        action?.invoke()
    }
}

@Composable
fun BorderSelector(vm: AppViewModel) {
    val rev by vm.repo.revision.collectAsState()
    val borders = remember(rev) { vm.repo.db.listBorders() }
    var expanded by remember { mutableStateOf(false) }
    val selected = borders.firstOrNull { it.id == vm.selectedBorderId }?.name ?: "همه مرزها"
    Box {
        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.LocationOn, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("مرز: $selected"); Spacer(Modifier.width(4.dp)); Icon(Icons.Default.KeyboardArrowDown, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("همه مرزها") }, onClick = { vm.selectedBorderId = null; expanded = false })
            borders.forEach { b -> DropdownMenuItem(text = { Text(b.name) }, onClick = { vm.selectedBorderId = b.id; expanded = false }) }
            HorizontalDivider()
            DropdownMenuItem(text = { Text("مدیریت مرزها") }, leadingIcon = { Icon(Icons.Default.Settings, null) }, onClick = { expanded = false; vm.navigate(Screen.Borders) })
        }
    }
}

@Composable
fun SectionCard(title: String, subtitle: String, icon: ImageVector, tint: Color = Purple, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(tint.copy(alpha = .12f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp); Text(subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .62f), fontSize = 12.sp) }
            Icon(Icons.Default.ChevronLeft, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .45f))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, tint: Color) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = .08f)), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = tint); Spacer(Modifier.width(9.dp)); Column { Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.65f)); Text(value, fontWeight = FontWeight.Bold, fontSize = 17.sp) }
        }
    }
}

@Composable
fun StatusChip(status: CargoStatus) {
    val (fg, bg) = when (status) {
        CargoStatus.PARKING -> ParkingRed to ParkingRedBg
        CargoStatus.WAITING_UNLOAD -> WaitingGreen to WaitingGreenBg
        CargoStatus.UNLOADED -> Purple to PurpleSoft
    }
    Surface(color = bg, shape = RoundedCornerShape(10.dp)) { Text(status.titleFa, color = fg, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)) }
}

@Composable
fun TruckCard(
    truck: Truck,
    showOwner: Boolean = true,
    onEdit: (() -> Unit)? = null,
    onStatus: ((CargoStatus) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var menu by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val hasQueue = truck.parkingQueue > 0
                Surface(color = if (hasQueue) PurpleSoft else MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("نوبت", fontSize = 10.sp, color = if (hasQueue) Purple else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (hasQueue) Formatters.number(truck.parkingQueue) else "ثبت نشده", fontWeight = FontWeight.ExtraBold, fontSize = if (hasQueue) 19.sp else 12.sp, color = if (hasQueue) Purple else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(truck.plate, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (showOwner) Text(truck.ownerName, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.7f), fontSize = 12.sp)
                    Text("${truck.cargoTitle} • ${Formatters.weightKg(truck.weightKg)} • ${truck.bundleCount} بندل", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.65f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        if (truck.declarationType == DeclarationType.COMPANY) "اظهار: اظهار شرکت" else "کوتاژ: ${truck.cottageNumber.ifBlank{"ثبت نشده"}}",
                        fontSize = 11.sp,
                        color = Purple,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (truck.driverName.isNotBlank()) {
                        Text("راننده: ${truck.driverName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .58f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (hasQueue) StatusChip(truck.status) else Surface(color=MaterialTheme.colorScheme.surfaceVariant,shape=RoundedCornerShape(10.dp)){Text("هنوز وارد پارکینگ نشده",fontSize=10.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(horizontal=8.dp,vertical=6.dp))}
                Box { IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, null) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        onEdit?.let { DropdownMenuItem(text={Text(if(truck.parkingQueue>0)"ویرایش" else "ویرایش / ثبت نوبت پارکینگ")}, leadingIcon={Icon(Icons.Default.Edit,null)}, onClick={ menu=false; it() }) }
                        if (onStatus != null && truck.parkingQueue > 0) {
                            DropdownMenuItem(text={Text("در پارکینگ")}, onClick={ menu=false; onStatus(CargoStatus.PARKING) })
                            DropdownMenuItem(text={Text("در انتظار برای تخلیه")}, onClick={ menu=false; onStatus(CargoStatus.WAITING_UNLOAD) })
                            DropdownMenuItem(text={Text("تخلیه شد")}, leadingIcon={Icon(Icons.Default.CheckCircle,null)}, onClick={ menu=false; onStatus(CargoStatus.UNLOADED) })
                        }
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (truck.phone.isNotBlank()) TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${truck.phone}"))) }) { Icon(Icons.Default.Phone, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text(truck.phone) }
                Spacer(Modifier.weight(1f))
                truck.calledAt?.let { Text("ورود تخلیه: ${Jalali.fromEpoch(it).date.format()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.55f)) }
                if (truck.completedAt != null) { Spacer(Modifier.width(8.dp)); Text("تخلیه: ${Jalali.fromEpoch(truck.completedAt).date.format()}", fontSize = 10.sp, color = Purple) }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(title: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("حذف $title") }, text = { Text("این مورد و اطلاعات زیرمجموعه آن حذف می‌شود. مطمئنی؟") }, confirmButton = { TextButton(onClick = onConfirm) { Text("حذف", color = ParkingRed) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } })
}

@Composable
fun EmptyState(text: String) {
    Column(Modifier.fillMaxWidth().padding(36.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Inbox, null, Modifier.size(42.dp), tint = Purple.copy(alpha=.55f)); Spacer(Modifier.height(10.dp)); Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.6f)) }
}
