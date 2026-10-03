package ir.bordermanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.bordermanager.data.*
import ir.bordermanager.util.Formatters
import ir.bordermanager.util.Jalali

@Composable
fun StatusScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState()
    val all = remember(rev, vm.selectedBorderId) { vm.repo.db.listStatusTrucks(vm.selectedBorderId) }
    val pendingQueue = remember(rev, vm.selectedBorderId) { vm.repo.db.listPendingQueueTrucks(vm.selectedBorderId) }
    val latestCall = remember(rev, vm.selectedBorderId) { vm.repo.db.latestCallBatch(vm.selectedBorderId) }

    var filter by remember { mutableStateOf<CargoStatus?>(null) }
    var threshold by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }

    val q = threshold.toIntOrNull()
    val preview = remember(rev, vm.selectedBorderId, q) {
        if (q == null || q <= 0) 0 else vm.repo.db.previewCallCount(vm.selectedBorderId, q)
    }
    val normalizedSearch = search.trim()
    val source = if (normalizedSearch.isBlank()) all else (all + pendingQueue).distinctBy { it.id }
    val shown = source.filter { truck ->
        val statusOk = filter == null || truck.status == filter
        val searchOk = normalizedSearch.isBlank() ||
            truck.plate.contains(normalizedSearch, ignoreCase = true) ||
            truck.ownerName.contains(normalizedSearch, ignoreCase = true) ||
            truck.phone.contains(normalizedSearch, ignoreCase = true) ||
            truck.driverName.contains(normalizedSearch, ignoreCase = true) ||
            truck.driverPaymentAccount.contains(normalizedSearch, ignoreCase = true) ||
            truck.parkingQueue.toString().contains(normalizedSearch) ||
            truck.cottageNumber.contains(normalizedSearch, ignoreCase = true)
        statusOk && searchOk
    }

    Column(modifier.fillMaxSize()) {
        ScreenTitle("وضعیت کالا", onBack={vm.back()})
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(Modifier.padding(horizontal=16.dp), verticalAlignment=Alignment.CenterVertically) {
                    BorderSelector(vm)
                }
            }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("جستجوی ماشین") },
                    placeholder = { Text("پلاک، نوبت، صاحب کالا، تماس یا کوتاژ") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (search.isNotBlank()) IconButton(onClick={search=""}) { Icon(Icons.Default.Close,null) }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp)
                )
            }

            if (pendingQueue.isNotEmpty()) {
                item {
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal=16.dp),
                        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant),
                        shape=RoundedCornerShape(15.dp)
                    ) {
                        Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping,null,tint=Purple)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("${pendingQueue.size} ماشین هنوز شماره نوبت پارکینگ ندارند",fontWeight=FontWeight.Bold,fontSize=13.sp)
                                Text("این ماشین‌ها ثبت شده‌اند ولی تا ثبت نوبت وارد فراخوان نمی‌شوند. در جستجو قابل پیدا کردن هستند.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.62f))
                            }
                        }
                    }
                }
            }

            latestCall?.let { batch ->
                item {
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal=16.dp),
                        colors=CardDefaults.cardColors(containerColor=WaitingGreenBg),
                        shape=RoundedCornerShape(15.dp)
                    ) {
                        Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                            Icon(Icons.Default.History,null,tint=WaitingGreen)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text("آخرین فراخوان: تا نوبت ${Formatters.number(batch.threshold)}",fontWeight=FontWeight.Bold)
                                Text("${Jalali.fromEpoch(batch.createdAt).date.format()} • ${batch.entryCount} پلاک ورود خورده",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f))
                            }
                            TextButton(onClick={vm.navigate(Screen.CallHistory)}) { Text("تاریخچه") }
                        }
                    }
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth().padding(horizontal=16.dp),
                    colors=CardDefaults.cardColors(containerColor=PurpleSoft),
                    shape=RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(15.dp), verticalArrangement=Arrangement.spacedBy(9.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign,null,tint=Purple)
                            Spacer(Modifier.width(8.dp))
                            Text("فراخوان پارکینگ",fontWeight=FontWeight.Bold,fontSize=18.sp)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick={vm.navigate(Screen.CallHistory)}) { Text("سوابق") }
                        }
                        Text(
                            "عدد نهایی فراخوان را وارد کن. ماشین‌های مشمول از «در پارکینگ» به «در انتظار برای تخلیه» می‌روند و سابقه تاریخ، نوبت و پلاک‌ها برای همیشه ثبت می‌شود.",
                            fontSize=12.sp,
                            color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f)
                        )
                        OutlinedTextField(
                            threshold,
                            {threshold=it.filter(Char::isDigit)},
                            label={Text("فراخوان تا شماره نوبت")},
                            singleLine=true,
                            modifier=Modifier.fillMaxWidth()
                        )
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Text("$preview ماشین مشمول فراخوان",modifier=Modifier.weight(1f),fontWeight=FontWeight.Bold,color=Purple)
                            Button(
                                onClick={if(q!=null&&q>0)showConfirm=true},
                                enabled=q!=null&&q>0
                            ){Text("ثبت فراخوان")}
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        FilterChip(filter==null,{filter=null},{Text("همه")},modifier=Modifier.weight(1f))
                        FilterChip(filter==CargoStatus.PARKING,{filter=CargoStatus.PARKING},{Text("در پارکینگ")},modifier=Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        FilterChip(filter==CargoStatus.WAITING_UNLOAD,{filter=CargoStatus.WAITING_UNLOAD},{Text("انتظار تخلیه")},modifier=Modifier.weight(1f))
                        FilterChip(filter==CargoStatus.UNLOADED,{filter=CargoStatus.UNLOADED},{Text("تخلیه شد")},modifier=Modifier.weight(1f))
                    }
                }
            }

            item {
                Text(
                    if (search.isBlank()) "لیست بر اساس شماره نوبت پارکینگ مرتب شده" else "${shown.size} نتیجه برای «$search»",
                    fontSize=11.sp,
                    color=MaterialTheme.colorScheme.onSurface.copy(alpha=.55f),
                    modifier=Modifier.padding(horizontal=18.dp,vertical=2.dp)
                )
            }

            if (shown.isEmpty()) {
                item { EmptyState(if(search.isBlank()) "موردی در این لیست وجود ندارد" else "ماشینی با این مشخصات پیدا نشد") }
            } else {
                val grouped = shown.groupBy { it.ownerId }
                val soft = listOf(Color(0xFFF7F0FF),Color(0xFFEEF4FF),Color(0xFFECFBF3),Color(0xFFFFF1F4),Color(0xFFFFF6E9),Color(0xFFEDF9FF))
                grouped.entries
                    .sortedBy { entry -> entry.value.minOfOrNull { t -> if(t.parkingQueue>0)t.parkingQueue else Int.MAX_VALUE } ?: Int.MAX_VALUE }
                    .forEachIndexed { idx, (ownerId, list) ->
                        item(key="status-owner-$ownerId") {
                            Surface(color=soft[idx%soft.size],shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp)) {
                                Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                                    Text(list.first().ownerName,fontWeight=FontWeight.Bold,fontSize=16.sp,modifier=Modifier.weight(1f))
                                    Text("${list.size} ماشین",fontSize=12.sp,color=Purple)
                                }
                            }
                        }
                        items(
                            list.sortedWith(compareBy<Truck>{if(it.parkingQueue>0)0 else 1}.thenBy{it.parkingQueue}),
                            key={it.id}
                        ) { t ->
                            Box(Modifier.padding(horizontal=16.dp)) {
                                TruckCard(
                                    t,
                                    showOwner=false,
                                    onEdit={vm.navigate(Screen.TruckForm(t.cargoTypeId,t.id))},
                                    onStatus={vm.setStatus(t.id,it)}
                                )
                            }
                        }
                    }
            }
        }
    }

    if(showConfirm && q!=null) {
        AlertDialog(
            onDismissRequest={showConfirm=false},
            title={Text("تأیید فراخوان تا نوبت $q")},
            text={
                Text(
                    if(preview>0)
                        "$preview ماشین وارد وضعیت «در انتظار برای تخلیه» می‌شوند. تاریخ امروز، شماره فراخوان و تمام پلاک‌های ورودخورده در سابقه فراخوان ذخیره می‌شود."
                    else
                        "در حال حاضر ماشینی مشمول این شماره نیست، اما خود فراخوان امروز تا نوبت $q در تاریخچه ثبت می‌شود."
                )
            },
            confirmButton={
                Button(onClick={
                    vm.repo.write{callUpTo(vm.selectedBorderId,q)}
                    threshold=""
                    showConfirm=false
                }){Text("تأیید و ثبت")}
            },
            dismissButton={TextButton(onClick={showConfirm=false}){Text("انصراف")}}
        )
    }
}

@Composable
fun CallHistoryScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState()
    val batches = remember(rev,vm.selectedBorderId){vm.repo.db.listCallBatches(vm.selectedBorderId)}
    var expandedBatch by remember { mutableStateOf<Long?>(batches.firstOrNull()?.id) }

    Column(modifier.fillMaxSize()) {
        ScreenTitle("تاریخچه فراخوان‌ها",onBack={vm.back()})
        Row(Modifier.padding(horizontal=16.dp)){BorderSelector(vm)}
        if(batches.isEmpty()) {
            EmptyState("هنوز فراخوانی ثبت نشده")
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding=PaddingValues(16.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp)
            ) {
                items(batches,key={it.id}) { batch ->
                    val expanded = expandedBatch==batch.id
                    val entries = if(expanded) remember(rev,batch.id){vm.repo.db.listCallEntries(batch.id)} else emptyList()
                    Card(
                        modifier=Modifier.fillMaxWidth(),
                        shape=RoundedCornerShape(17.dp),
                        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Row(
                                Modifier.fillMaxWidth().clickable{expandedBatch=if(expanded)null else batch.id}.padding(14.dp),
                                verticalAlignment=Alignment.CenterVertically
                            ) {
                                Surface(color=PurpleSoft,shape=RoundedCornerShape(12.dp)) {
                                    Icon(Icons.Default.Campaign,null,tint=Purple,modifier=Modifier.padding(10.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("${Jalali.fromEpoch(batch.createdAt).date.format()} — تا نوبت ${Formatters.number(batch.threshold)}",fontWeight=FontWeight.Bold)
                                    Text("${batch.entryCount} پلاک در این فراخوان ورود خورده",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f))
                                }
                                Icon(if(expanded)Icons.Default.ExpandLess else Icons.Default.ExpandMore,null,tint=Purple)
                            }
                            if(expanded) {
                                if(entries.isEmpty()) {
                                    Text("در این فراخوان پلاک جدیدی مشمول نبود.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f),modifier=Modifier.padding(start=14.dp,end=14.dp,bottom=14.dp))
                                } else {
                                    entries.forEach { entry ->
                                        HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
                                        Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                                            Surface(color=if(entry.missed)ParkingRedBg else WaitingGreenBg,shape=RoundedCornerShape(10.dp)) {
                                                Text("نوبت ${Formatters.number(entry.parkingQueue)}",fontSize=11.sp,fontWeight=FontWeight.Bold,color=if(entry.missed)ParkingRed else WaitingGreen,modifier=Modifier.padding(horizontal=8.dp,vertical=6.dp))
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Column(Modifier.weight(1f)) {
                                                Text(entry.plate,fontWeight=FontWeight.Bold)
                                                Text(entry.ownerName,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f))
                                                Text(
                                                    if(entry.missed) "جا مانده از فراخوان" else "وضعیت فعلی: ${entry.currentStatus?.titleFa ?: "ماشین حذف شده"}",
                                                    fontSize=11.sp,
                                                    color=if(entry.missed)ParkingRed else Purple
                                                )
                                            }
                                            if(entry.truckId!=null && entry.currentStatus!=CargoStatus.UNLOADED) {
                                                if(entry.missed) {
                                                    TextButton(onClick={vm.repo.write{markCallEntryEntered(entry.id)}}){Text("ثبت ورود")}
                                                } else {
                                                    TextButton(onClick={vm.repo.write{markCallEntryMissed(entry.id)}}){Text("جا مانده",color=ParkingRed)}
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusListScreen(vm: AppViewModel, parking: Boolean, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState(); val status=if(parking)CargoStatus.PARKING else CargoStatus.WAITING_UNLOAD
    val trucks=remember(rev,vm.selectedBorderId,parking){vm.repo.db.listActiveTrucks(vm.selectedBorderId,status)}
    Column(modifier.fillMaxSize()) {
        ScreenTitle(if(parking)"بارهای موجود در پارکینگ" else "بارهای درحال انتظار برای تخلیه",onBack={vm.back()})
        Row(Modifier.padding(horizontal=16.dp)){BorderSelector(vm)}
        Card(Modifier.fillMaxWidth().padding(16.dp),colors=CardDefaults.cardColors(containerColor=(if(parking)ParkingRedBg else WaitingGreenBg)),shape=RoundedCornerShape(16.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(if(parking)Icons.Default.LocalParking else Icons.Default.MoveToInbox,null,tint=if(parking)ParkingRed else WaitingGreen);Spacer(Modifier.width(8.dp));Column{Text(if(parking)"${trucks.size} ماشین در پارکینگ" else "${trucks.size} ماشین در انتظار تخلیه",fontWeight=FontWeight.Bold);Text("تفکیک بر اساس صاحب کالا • مرتب بر اساس نوبت",fontSize=11.sp)}}}
        GroupedTruckList(vm,trucks,modifier=Modifier.weight(1f))
    }
}

@Composable
private fun GroupedTruckList(vm: AppViewModel, trucks: List<Truck>, modifier: Modifier = Modifier) {
    val grouped = trucks.groupBy { it.ownerId }
    val soft = listOf(Color(0xFFF7F0FF),Color(0xFFEEF4FF),Color(0xFFECFBF3),Color(0xFFFFF1F4),Color(0xFFFFF6E9),Color(0xFFEDF9FF))
    if(trucks.isEmpty()){Box(modifier.fillMaxSize()){EmptyState("موردی در این لیست وجود ندارد")};return}
    LazyColumn(modifier,contentPadding=PaddingValues(horizontal=16.dp,vertical=6.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        grouped.entries.sortedBy{it.value.minOfOrNull{t->t.parkingQueue}?:Int.MAX_VALUE}.forEachIndexed { idx,(ownerId,list)->
            item(key="h$ownerId") { Surface(color=soft[idx%soft.size],shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth()){Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text(list.first().ownerName,fontWeight=FontWeight.Bold,fontSize=16.sp,modifier=Modifier.weight(1f));Text("${list.size} ماشین",fontSize=12.sp,color=Purple)}} }
            items(list.sortedBy{it.parkingQueue},key={it.id}) { t-> TruckCard(t,showOwner=false,onEdit={vm.navigate(Screen.TruckForm(t.cargoTypeId,t.id))},onStatus={vm.setStatus(t.id,it)}) }
        }
        item{Spacer(Modifier.height(16.dp))}
    }
}

@Composable
fun CalendarScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState()
    val y=vm.calendarYear; val m=vm.calendarMonth; val days=Jalali.daysInMonth(y,m); val first=Jalali.dayOfWeekIndex(y,m,1)
    val eventCounts = remember(rev,vm.selectedBorderId,y,m) { (1..days).associateWith { d -> vm.repo.db.trucksOnJalaliDay(vm.selectedBorderId,y,m,d).size } }
    val selected = vm.selectedCalendarDay.coerceIn(1,days)
    val selectedTrucks = remember(rev,vm.selectedBorderId,y,m,selected){vm.repo.db.trucksOnJalaliDay(vm.selectedBorderId,y,m,selected)}
    Column(modifier.fillMaxSize()) {
        ScreenTitle("تقویم شمسی",onBack={vm.back()})
        Row(Modifier.padding(horizontal=16.dp)){BorderSelector(vm)}
        Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){IconButton(onClick=vm::nextCalendarMonth){Icon(Icons.Default.ChevronRight,null,tint=Purple)};Text(Jalali.Date(y,m,1).monthTitle(),fontWeight=FontWeight.Bold,fontSize=20.sp);IconButton(onClick=vm::previousCalendarMonth){Icon(Icons.Default.ChevronLeft,null,tint=Purple)}}
        val labels=listOf("شنبه","یکشنبه","دوشنبه","سه‌شنبه","چهارشنبه","پنجشنبه","جمعه")
        Row(Modifier.fillMaxWidth().padding(horizontal=14.dp)){labels.forEach{Text(it,modifier=Modifier.weight(1f),textAlign=TextAlign.Center,fontSize=10.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.58f))}}
        val cells=(List(first){0}+(1..days).toList())
        LazyVerticalGrid(columns=GridCells.Fixed(7),modifier=Modifier.fillMaxWidth().height(280.dp).padding(horizontal=12.dp),userScrollEnabled=false){items(cells){d->if(d==0)Spacer(Modifier.aspectRatio(1f))else{val has=(eventCounts[d]?:0)>0;val sel=d==selected;Box(Modifier.aspectRatio(1f).padding(3.dp).background(if(sel)PurpleSoft else Color.Transparent,RoundedCornerShape(12.dp)).clickable{vm.selectedCalendarDay=d},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text(d.toString(),fontWeight=if(sel)FontWeight.Bold else FontWeight.Normal,color=if(sel)Purple else MaterialTheme.colorScheme.onSurface);if(has)Box(Modifier.size(6.dp).background(if(sel)Purple else WaitingGreen,RoundedCornerShape(50)))} }}}}
        Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically){Text("ثبت‌های ${Jalali.Date(y,m,selected).format()}",fontWeight=FontWeight.Bold,fontSize=16.sp,modifier=Modifier.weight(1f));Surface(color=PurpleSoft,shape=RoundedCornerShape(10.dp)){Text("${selectedTrucks.size} مورد",color=Purple,modifier=Modifier.padding(horizontal=10.dp,vertical=5.dp))}}
        if(selectedTrucks.isEmpty())EmptyState("در این روز ثبت یا ورود فعالی وجود ندارد")else LazyColumn(Modifier.weight(1f),contentPadding=PaddingValues(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(selectedTrucks,key={it.id}){t->TruckCard(t,onEdit={vm.navigate(Screen.TruckForm(t.cargoTypeId,t.id))},onStatus={vm.setStatus(t.id,it)})};item{Spacer(Modifier.height(12.dp))}}
    }
}
