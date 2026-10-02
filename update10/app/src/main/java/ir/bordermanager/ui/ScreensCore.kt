package ir.bordermanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.bordermanager.data.*
import ir.bordermanager.util.Formatters
import ir.bordermanager.util.Jalali

@Composable
fun HomeScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState()
    val stats = remember(rev, vm.selectedBorderId) { vm.repo.db.dashboardStats(vm.selectedBorderId) }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("مدیریت مرز", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink); Text("سامانه مدیریت بار، پارکینگ و حسابداری", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.58f)) }
                Icon(Icons.Default.Notifications, null, tint = Purple)
            }
            Spacer(Modifier.height(14.dp)); BorderSelector(vm)
            Spacer(Modifier.height(10.dp)); Text("امروز ${Jalali.now().date.format()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=.6f))
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(Modifier.weight(1f)) { StatCard("در پارکینگ", Formatters.number(stats.parking), Icons.Default.LocalShipping, ParkingRed) }
                Box(Modifier.weight(1f)) { StatCard("انتظار تخلیه", Formatters.number(stats.waiting), Icons.Default.CheckCircle, WaitingGreen) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(Modifier.weight(1f)) { StatCard("کل فعال", Formatters.number(stats.totalActive), Icons.Default.Inventory, Purple) }
                Box(Modifier.weight(1f)) { StatCard("صاحبان کالا", Formatters.number(stats.owners), Icons.Default.Groups, Color(0xFF2563EB)) }
            }
        }
        item { Text("بخش‌های اصلی", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.padding(top=4.dp)) }
        item { SectionCard("صاحبان کالا", "نوع بارها، ماشین‌ها و اطلاعات صاحبان", Icons.Default.Groups) { vm.navigate(Screen.Owners) } }
        item { SectionCard("حسابرسی ماهانه", "آمار ماه، صورت‌حساب اختصاصی و PDF", Icons.Default.Calculate, Color(0xFF0F9D58)) { vm.navigate(Screen.Accounting) } }
        item { SectionCard("گزارش‌ها", "گزارش روزانه و ماهانه با شماره ردیف", Icons.Default.Assessment, Purple) { vm.navigate(Screen.Reports) } }
        item { SectionCard("تقویم شمسی", "تاریخ ثبت و ورود ماشین‌ها", Icons.Default.CalendarMonth, Color(0xFF2563EB)) { vm.navigate(Screen.Calendar) } }
        item { SectionCard("وضعیت کالا", "فراخوان پارکینگ و کنترل وضعیت", Icons.Default.Inventory2, Color(0xFFF59E0B)) { vm.navigate(Screen.Status) } }
        item { SectionCard("بارهای موجود در پارکینگ", "تفکیک صاحب کالا و مرتب بر اساس نوبت", Icons.Default.LocalParking, ParkingRed) { vm.navigate(Screen.Parking) } }
        item { SectionCard("بارهای باسکول‌شده / در انتظار تخلیه", "تفکیک صاحب کالا و مرتب بر اساس نوبت", Icons.Default.MoveToInbox, WaitingGreen) { vm.navigate(Screen.Waiting) } }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
fun OwnersScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState()
    val owners = remember(rev, vm.selectedBorderId) { vm.repo.db.listOwners(vm.selectedBorderId) }
    var search by remember { mutableStateOf("") }
    var add by remember { mutableStateOf(false) }
    var edit by remember { mutableStateOf<Owner?>(null) }
    var delete by remember { mutableStateOf<Owner?>(null) }
    val filtered = owners.filter { it.name.contains(search, true) }
    Column(modifier.fillMaxSize()) {
        ScreenTitle("صاحبان کالا", onBack = { vm.back() }, action = { FilledIconButton(onClick={add=true}, colors=IconButtonDefaults.filledIconButtonColors(containerColor=Purple)) { Icon(Icons.Default.Add,null) } })
        Row(Modifier.padding(horizontal=16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.weight(1f)) { BorderSelector(vm) } }
        OutlinedTextField(search, {search=it}, modifier=Modifier.fillMaxWidth().padding(16.dp), placeholder={Text("جستجوی صاحب کالا...")}, leadingIcon={Icon(Icons.Default.Search,null)}, singleLine=true, shape=RoundedCornerShape(15.dp))
        if (filtered.isEmpty()) EmptyState("هنوز صاحب کالایی ثبت نشده") else LazyColumn(contentPadding=PaddingValues(horizontal=16.dp, vertical=4.dp), verticalArrangement=Arrangement.spacedBy(9.dp)) {
            items(filtered, key={it.id}) { owner ->
                OwnerCard(owner, onClick={vm.navigate(Screen.OwnerDetail(owner.id))}, onEdit={edit=owner}, onDelete={delete=owner})
            }
            item { Spacer(Modifier.height(18.dp)) }
        }
    }
    if (add) OwnerDialog(vm, null, onDismiss={add=false})
    edit?.let { OwnerDialog(vm, it, onDismiss={edit=null}) }
    delete?.let { o -> ConfirmDeleteDialog(o.name, onDismiss={delete=null}) { vm.repo.write { deleteOwner(o.id) }; delete=null } }
}

@Composable
private fun OwnerCard(owner: Owner, onClick:()->Unit, onEdit:()->Unit, onDelete:()->Unit) {
    var menu by remember { mutableStateOf(false) }
    val colors = listOf(Color(0xFFF3E8FF),Color(0xFFE8F0FF),Color(0xFFE7F8EF),Color(0xFFFFEDF2),Color(0xFFFFF1DE),Color(0xFFE9F7FF),Color(0xFFF4F4F5),Color(0xFFF0EAFF))
    Card(Modifier.fillMaxWidth().clickable(onClick=onClick), shape=RoundedCornerShape(17.dp), colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(14.dp), verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(45.dp).background(colors[owner.colorSeed % colors.size], RoundedCornerShape(13.dp)), contentAlignment=Alignment.Center) { Text(owner.name.take(1), fontWeight=FontWeight.Bold, fontSize=18.sp, color=Purple) }
            Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(owner.name, fontWeight=FontWeight.Bold, fontSize=16.sp); Text("${owner.cargoCount} نوع بار  |  ${owner.truckCount} ماشین ثبت‌شده", fontSize=12.sp, color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f)) }
            Box { IconButton(onClick={menu=true}) { Icon(Icons.Default.MoreVert,null) }; DropdownMenu(menu,{menu=false}) { DropdownMenuItem({Text("ویرایش")},{menu=false;onEdit()},leadingIcon={Icon(Icons.Default.Edit,null)}); DropdownMenuItem({Text("حذف")},{menu=false;onDelete()},leadingIcon={Icon(Icons.Default.Delete,null,tint=ParkingRed)}) } }
        }
    }
}

@Composable
private fun OwnerDialog(vm: AppViewModel, owner: Owner?, onDismiss:()->Unit) {
    val rev by vm.repo.revision.collectAsState(); val borders=remember(rev){vm.repo.db.listBorders()}
    var name by remember(owner?.id) { mutableStateOf(owner?.name ?: "") }; var note by remember(owner?.id){mutableStateOf(owner?.note?:"")}
    var borderId by remember(owner?.id, vm.selectedBorderId){mutableStateOf(owner?.borderId ?: vm.selectedBorderId ?: borders.firstOrNull()?.id)}
    var borderMenu by remember{mutableStateOf(false)}
    AlertDialog(onDismissRequest=onDismiss, title={Text(if(owner==null)"افزودن صاحب کالا" else "ویرایش صاحب کالا")}, text={Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
        OutlinedTextField(name,{name=it},label={Text("نام صاحب کالا")},singleLine=true)
        Box { OutlinedButton(onClick={borderMenu=true}, modifier=Modifier.fillMaxWidth()) { Text(borders.firstOrNull{it.id==borderId}?.name ?: "انتخاب مرز") }; DropdownMenu(borderMenu,{borderMenu=false}) { borders.forEach { b-> DropdownMenuItem({Text(b.name)},{borderId=b.id;borderMenu=false}) } } }
        OutlinedTextField(note,{note=it},label={Text("توضیحات")},minLines=2)
    }}, confirmButton={Button(onClick={ if(name.isNotBlank() && borderId!=null) { if(owner==null) vm.repo.write{addOwner(borderId!!,name,note)} else vm.repo.write{updateOwner(owner.id,name,note)}; onDismiss() } }){Text("ذخیره")}}, dismissButton={TextButton(onClick=onDismiss){Text("انصراف")}})
}

@Composable
fun OwnerDetailScreen(vm: AppViewModel, ownerId: Long, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState()
    val owner=remember(rev,ownerId){vm.repo.db.getOwner(ownerId)}
    val cargos=remember(rev,ownerId){vm.repo.db.listCargoTypes(ownerId)}
    val allTrucks=remember(rev,ownerId){vm.repo.db.listTrucksForOwner(ownerId)}
    val monthGroups=remember(allTrucks){allTrucks.groupBy { val d=Jalali.fromEpoch(it.createdAt).date; d.year to d.month }.toList().sortedByDescending { it.first.first * 100 + it.first.second }}
    var expandedMonth by remember(monthGroups.size){mutableStateOf(monthGroups.firstOrNull()?.first)}
    var add by remember{mutableStateOf(false)}; var edit by remember{mutableStateOf<CargoType?>(null)}; var delete by remember{mutableStateOf<CargoType?>(null)}
    if(owner==null){EmptyState("صاحب کالا پیدا نشد");return}
    Column(modifier.fillMaxSize()) {
        ScreenTitle(owner.name,onBack={vm.back()},action={FilledIconButton(onClick={add=true},colors=IconButtonDefaults.filledIconButtonColors(containerColor=Purple)){Icon(Icons.Default.Add,null)}})
        Card(Modifier.fillMaxWidth().padding(horizontal=16.dp), colors=CardDefaults.cardColors(containerColor=PurpleSoft), shape=RoundedCornerShape(18.dp)) { Row(Modifier.padding(15.dp)) { Column(Modifier.weight(1f)){Text("${owner.cargoCount} نوع بار",fontWeight=FontWeight.Bold);Text("${owner.truckCount} ماشین ثبت‌شده در کل",fontSize=12.sp)}; Icon(Icons.Default.Inventory2,null,tint=Purple) } }
        LazyColumn(contentPadding=PaddingValues(horizontal=16.dp,vertical=10.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
            item { Text("پرونده ماهانه",fontWeight=FontWeight.Bold,fontSize=18.sp) }
            if(monthGroups.isEmpty()) item { Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant),shape=RoundedCornerShape(15.dp)){Text("هنوز ماشینی برای این صاحب کالا ثبت نشده",modifier=Modifier.padding(16.dp),fontSize=12.sp)} }
            monthGroups.forEach { (key, trucks) ->
                val year=key.first; val month=key.second; val expanded=expandedMonth==key; val cargoCount=trucks.map{it.cargoTypeId}.distinct().size
                item(key="m-$year-$month") {
                    Card(Modifier.fillMaxWidth().clickable{expandedMonth=if(expanded)null else key},colors=CardDefaults.cardColors(containerColor=if(expanded)PurpleSoft else MaterialTheme.colorScheme.surface),shape=RoundedCornerShape(17.dp)) {
                        Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(43.dp).background(Purple.copy(alpha=.10f),RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center){Icon(Icons.Default.Folder,null,tint=Purple)};Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(Jalali.Date(year,month,1).monthTitle(),fontWeight=FontWeight.Bold,fontSize=16.sp);Text("${trucks.size} ماشین • $cargoCount نوع بار • ${Formatters.weightKg(trucks.sumOf{it.weightKg})}",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.62f))};Icon(if(expanded)Icons.Default.ExpandLess else Icons.Default.ExpandMore,null,tint=Purple)}
                    }
                }
                if(expanded) {
                    trucks.groupBy{it.cargoTypeId}.forEach { (_, cargoTrucks) ->
                        item { Surface(color=MaterialTheme.colorScheme.surfaceVariant,shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()){Row(Modifier.padding(horizontal=12.dp,vertical=8.dp)){Text(cargoTrucks.first().cargoTitle,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Text("${cargoTrucks.size} ماشین",fontSize=11.sp,color=Purple)}} }
                        items(cargoTrucks.sortedWith(compareBy<Truck>{if(it.parkingQueue>0)0 else 1}.thenBy{it.parkingQueue}),key={"month-${year}-${month}-${it.id}"}) { t -> TruckCard(t,showOwner=false,onEdit={vm.navigate(Screen.TruckForm(t.cargoTypeId,t.id))},onStatus={vm.setStatus(t.id,it)}) }
                    }
                }
            }
            item { Spacer(Modifier.height(6.dp)); HorizontalDivider(); Spacer(Modifier.height(6.dp)); Row(verticalAlignment=Alignment.CenterVertically){Text("مدیریت نوع بارها",fontWeight=FontWeight.Bold,fontSize=18.sp,modifier=Modifier.weight(1f));Text("برای ثبت بار و ماشین جدید",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.55f))} }
            if(cargos.isEmpty()) item { EmptyState("برای این صاحب کالا نوع باری ثبت نشده") } else items(cargos,key={"cargo-${it.id}"}) { c-> CargoCard(c,{vm.navigate(Screen.CargoDetail(c.id))},{edit=c},{delete=c}) }
            item{Spacer(Modifier.height(16.dp))}
        }
    }
    if(add) CargoDialog(vm,ownerId,null){add=false}; edit?.let{CargoDialog(vm,ownerId,it){edit=null}}; delete?.let{c->ConfirmDeleteDialog(c.title,{delete=null}){vm.repo.write{deleteCargoType(c.id)};delete=null}}
}

@Composable
private fun CargoCard(c: CargoType,onClick:()->Unit,onEdit:()->Unit,onDelete:()->Unit) {
    var menu by remember{mutableStateOf(false)}
    Card(Modifier.fillMaxWidth().clickable(onClick=onClick),shape=RoundedCornerShape(17.dp)) { Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically) { Box(Modifier.size(44.dp).background(PurpleSoft,RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center){Icon(Icons.Default.Category,null,tint=Purple)};Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(c.title,fontWeight=FontWeight.Bold,fontSize=16.sp);Text("${c.truckCount} ماشین • مبدأ: ${c.origin.ifBlank{"-"}}",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.62f));if(c.iraqiOwner.isNotBlank())Text("صاحب کالای عراقی: ${c.iraqiOwner}",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.55f))};Box{IconButton({menu=true}){Icon(Icons.Default.MoreVert,null)};DropdownMenu(menu,{menu=false}){DropdownMenuItem({Text("ویرایش")},{menu=false;onEdit()},leadingIcon={Icon(Icons.Default.Edit,null)});DropdownMenuItem({Text("حذف")},{menu=false;onDelete()},leadingIcon={Icon(Icons.Default.Delete,null,tint=ParkingRed)})}} } }
}

@Composable
private fun CargoDialog(vm: AppViewModel,ownerId:Long,cargo:CargoType?,onDismiss:()->Unit) {
    var title by remember(cargo?.id){mutableStateOf(cargo?.title?:"")};var origin by remember(cargo?.id){mutableStateOf(cargo?.origin?:"")};var iraqiOwner by remember(cargo?.id){mutableStateOf(cargo?.iraqiOwner?:"")};var broker by remember(cargo?.id){mutableStateOf(cargo?.iraqiBroker?:"")}
    AlertDialog(onDismissRequest=onDismiss,title={Text(if(cargo==null)"افزودن نوع بار" else "ویرایش نوع بار")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(title,{title=it},label={Text("نوع بار")},singleLine=true);OutlinedTextField(origin,{origin=it},label={Text("مبدأ بارگیری")},singleLine=true);OutlinedTextField(iraqiOwner,{iraqiOwner=it},label={Text("صاحب کالای عراقی")},singleLine=true);OutlinedTextField(broker,{broker=it},label={Text("ترخیصکار عراقی")},singleLine=true);Text("تاریخ بارگیری/ثبت ماشین‌ها به‌صورت خودکار ثبت می‌شود.",fontSize=11.sp,color=Purple)}},confirmButton={Button(onClick={if(title.isNotBlank()){if(cargo==null)vm.repo.write{addCargoType(ownerId,title,origin,iraqiOwner,broker)}else vm.repo.write{updateCargoType(cargo.id,title,origin,iraqiOwner,broker)};onDismiss()}}){Text("ذخیره")}},dismissButton={TextButton(onClick=onDismiss){Text("انصراف")}})
}

@Composable
fun CargoDetailScreen(vm: AppViewModel, cargoId: Long, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState(); val cargo=remember(rev,cargoId){vm.repo.db.getCargoType(cargoId)}; val trucks=remember(rev,cargoId){vm.repo.db.listTrucksForCargo(cargoId)}
    var deleteTruck by remember{mutableStateOf<Truck?>(null)}
    if(cargo==null){EmptyState("نوع بار پیدا نشد");return}
    Column(modifier.fillMaxSize()) {
        ScreenTitle("جزئیات نوع بار",onBack={vm.back()},action={FilledIconButton(onClick={vm.navigate(Screen.TruckForm(cargoId))},colors=IconButtonDefaults.filledIconButtonColors(containerColor=Purple)){Icon(Icons.Default.Add,null)}})
        Card(Modifier.fillMaxWidth().padding(horizontal=16.dp),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=PurpleSoft)) { Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(4.dp)){Text(cargo.title,fontSize=19.sp,fontWeight=FontWeight.Bold,color=Ink);Text("مبدأ بارگیری: ${cargo.origin.ifBlank{"-"}}");Text("صاحب کالای عراقی: ${cargo.iraqiOwner.ifBlank{"-"}}");Text("ترخیصکار عراقی: ${cargo.iraqiBroker.ifBlank{"-"}}");Text("تعداد ماشین: ${cargo.truckCount}",fontWeight=FontWeight.Bold,color=Purple)} }
        Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text("ماشین‌ها",fontWeight=FontWeight.Bold,fontSize=17.sp,modifier=Modifier.weight(1f));Button(onClick={vm.navigate(Screen.TruckForm(cargoId))}){Icon(Icons.Default.Add,null,Modifier.size(18.dp));Text(" ثبت ماشین")}}
        if(trucks.isEmpty())EmptyState("هنوز ماشینی برای این نوع بار ثبت نشده")else LazyColumn(contentPadding=PaddingValues(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(trucks,key={it.id}){t->TruckCard(t,showOwner=false,onEdit={vm.navigate(Screen.TruckForm(cargoId,t.id))},onStatus={vm.setStatus(t.id,it)})};item{Spacer(Modifier.height(16.dp))}}
    }
    deleteTruck?.let{t->ConfirmDeleteDialog("ماشین ${t.plate}",{deleteTruck=null}){vm.repo.write{deleteTruck(t.id)};deleteTruck=null}}
}

@Composable
fun TruckFormScreen(vm: AppViewModel, cargoId: Long, truckId: Long?, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState(); val cargo=remember(rev,cargoId){vm.repo.db.getCargoType(cargoId)}; val existing=remember(rev,truckId){truckId?.let{vm.repo.db.getTruck(it)}}
    val owner=remember(rev,cargo?.ownerId){cargo?.let{vm.repo.db.getOwner(it.ownerId)}}
    var plate by remember(existing?.id){mutableStateOf(existing?.plate?:"")}
    var weight by remember(existing?.id){mutableStateOf(existing?.weightKg?.toString()?.removeSuffix(".0")?:"")}
    var size by remember(existing?.id){mutableStateOf(existing?.size?:"")}
    var bundles by remember(existing?.id){mutableStateOf(existing?.bundleCount?.toString()?:"")}
    var phone by remember(existing?.id){mutableStateOf(existing?.phone?:"")}
    var driverName by remember(existing?.id){mutableStateOf(existing?.driverName?:"")}
    var driverPaymentAccount by remember(existing?.id){mutableStateOf(existing?.driverPaymentAccount?:"")}
    var noQueue by remember(existing?.id){mutableStateOf(existing?.parkingQueue?.let{it<=0}?:true)}
    var queue by remember(existing?.id){mutableStateOf(existing?.parkingQueue?.takeIf{it>0}?.toString()?:"")}
    var declarationType by remember(existing?.id){mutableStateOf(existing?.declarationType?:DeclarationType.COTTAGE)}
    var cottageNumber by remember(existing?.id){mutableStateOf(existing?.cottageNumber?:"")}
    var error by remember{mutableStateOf<String?>(null)}
    Column(modifier.fillMaxSize()) {
        ScreenTitle(if(existing==null)"ثبت ماشین جدید" else "ویرایش ماشین",onBack={vm.back()})
        LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            item { Card(colors=CardDefaults.cardColors(containerColor=PurpleSoft),shape=RoundedCornerShape(16.dp)){Column(Modifier.padding(14.dp)){Text(owner?.name?:"",fontWeight=FontWeight.Bold);Text("نوع بار: ${cargo?.title?:"-"}",fontSize=12.sp);Text("تاریخ و ساعت ثبت به‌صورت خودکار ذخیره می‌شود",fontSize=11.sp,color=Purple)}} }
            item { OutlinedTextField(plate,{plate=it},label={Text("شماره پلاک")},modifier=Modifier.fillMaxWidth(),singleLine=true) }
            item { OutlinedTextField(weight,{weight=it.filter{x->x.isDigit()||x=='.'}},label={Text("وزن بار (کیلوگرم)")},modifier=Modifier.fillMaxWidth(),singleLine=true) }
            item { OutlinedTextField(size,{size=it},label={Text("سایز بار")},modifier=Modifier.fillMaxWidth(),singleLine=true) }
            item { OutlinedTextField(bundles,{bundles=it.filter(Char::isDigit)},label={Text("تعداد بندل")},modifier=Modifier.fillMaxWidth(),singleLine=true) }
            item { OutlinedTextField(phone,{phone=it.filter{x->x.isDigit()||x=='+'}},label={Text("شماره تماس")},modifier=Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Default.Phone,null)}) }
            item { OutlinedTextField(driverName,{driverName=it},label={Text("نام راننده")},modifier=Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Default.Person,null)}) }
            item { OutlinedTextField(driverPaymentAccount,{driverPaymentAccount=it},label={Text("شماره شبا یا شماره کارت راننده")},modifier=Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Default.AccountBalance,null)},supportingText={Text("یکی از این دو مورد را وارد کن")}) }
            item {
                Card(colors=CardDefaults.cardColors(containerColor=PurpleSoft),shape=RoundedCornerShape(15.dp)) {
                    Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text("نوع اظهار",fontWeight=FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected=declarationType==DeclarationType.COTTAGE,
                                onClick={declarationType=DeclarationType.COTTAGE},
                                label={Text("کوتاژ")},
                                modifier=Modifier.weight(1f)
                            )
                            FilterChip(
                                selected=declarationType==DeclarationType.COMPANY,
                                onClick={declarationType=DeclarationType.COMPANY;cottageNumber=""},
                                label={Text("اظهار شرکت")},
                                modifier=Modifier.weight(1f)
                            )
                        }
                        if(declarationType==DeclarationType.COTTAGE) {
                            OutlinedTextField(
                                cottageNumber,
                                {cottageNumber=it.filter{ch->ch.isLetterOrDigit()||ch=='-'||ch=='/'}},
                                label={Text("شماره کوتاژ")},
                                modifier=Modifier.fillMaxWidth(),
                                singleLine=true,
                                leadingIcon={Icon(Icons.Default.ReceiptLong,null)}
                            )
                        } else {
                            Surface(color=MaterialTheme.colorScheme.surface,shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                                Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                                    Icon(Icons.Default.Business,null,tint=Purple)
                                    Spacer(Modifier.width(8.dp))
                                    Text("این ماشین با «اظهار شرکت» ثبت می‌شود و شماره کوتاژ لازم نیست.",fontSize=12.sp)
                                }
                            }
                        }
                    }
                }
            }
            item { Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant),shape=RoundedCornerShape(15.dp)){Row(Modifier.fillMaxWidth().clickable{noQueue=!noQueue;if(noQueue)queue=""}.padding(12.dp),verticalAlignment=Alignment.CenterVertically){Checkbox(noQueue,{noQueue=it;if(it)queue=""});Spacer(Modifier.width(6.dp));Column{Text("ماشین هنوز وارد پارکینگ نشده",fontWeight=FontWeight.Bold,fontSize=13.sp);Text("فعلاً بدون شماره نوبت ثبت می‌شود؛ بعداً با ویرایش همین ماشین نوبت را وارد کن.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f))}}} }
            item { OutlinedTextField(queue,{queue=it.filter(Char::isDigit)},label={Text("شماره نوبت پارکینگ")},modifier=Modifier.fillMaxWidth(),singleLine=true,enabled=!noQueue,leadingIcon={Icon(Icons.Default.ConfirmationNumber,null)},supportingText={Text(if(noQueue)"بعد از رسیدن ماشین به پارکینگ قابل ثبت است" else "ماشین‌های پارکینگ بر اساس این شماره مرتب می‌شوند")}) }
            error?.let{item{Text(it,color=ParkingRed,fontWeight=FontWeight.Bold)}}
            item { Button(onClick={
                val q=if(noQueue)0 else queue.toIntOrNull(); val w=weight.toDoubleOrNull()?:0.0; val b=bundles.toIntOrNull()?:0
                if(plate.isBlank()) error="شماره پلاک الزامی است"
                else if(declarationType==DeclarationType.COTTAGE && cottageNumber.isBlank()) error="شماره کوتاژ را وارد کن یا «اظهار شرکت» را انتخاب کن"
                else if(!noQueue && (q==null || q<=0)) error="شماره نوبت پارکینگ را وارد کن یا گزینه «هنوز وارد پارکینگ نشده» را فعال کن"
                else {
                    val finalQ=q?:0
                    val dup=if(finalQ>0)owner?.let{vm.repo.db.queueExists(it.borderId,finalQ,existing?.id)}else null
                    if(dup!=null) error="نوبت $finalQ قبلاً برای پلاک ${dup.plate} ثبت شده"
                    else {
                        if(existing==null) vm.repo.write{addTruck(cargoId,plate,w,size,b,phone,driverName,driverPaymentAccount,finalQ,declarationType,cottageNumber)}
                        else vm.repo.write{updateTruck(existing.id,plate,w,size,b,phone,driverName,driverPaymentAccount,finalQ,declarationType,cottageNumber)}
                        vm.back()
                    }
                }
            },modifier=Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(14.dp)){Icon(Icons.Default.Save,null);Spacer(Modifier.width(6.dp));Text("ذخیره ماشین")}}
            item{Spacer(Modifier.height(12.dp))}
        }
    }
}
