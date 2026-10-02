package ir.bordermanager.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.bordermanager.BuildConfig
import ir.bordermanager.data.*
import ir.bordermanager.pdf.PdfExporter
import ir.bordermanager.util.Formatters
import ir.bordermanager.util.Jalali

@Composable
fun AccountingScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val context=LocalContext.current; val rev by vm.repo.revision.collectAsState(); val y=vm.accountingYear;val m=vm.accountingMonth
    val summary=remember(rev,vm.selectedBorderId,y,m){vm.repo.db.monthSummary(vm.selectedBorderId,y,m)}
    val ownerSummaries=remember(rev,vm.selectedBorderId,y,m){vm.repo.db.ownerMonthSummaries(vm.selectedBorderId,y,m)}
    val trucks=remember(rev,vm.selectedBorderId,y,m){vm.repo.db.monthlyTrucks(vm.selectedBorderId,y,m)}
    val accountings=remember(rev,vm.selectedBorderId,y,m){vm.repo.db.monthlyAccounting(vm.selectedBorderId,y,m)}
    val truckCosts=remember(rev,vm.selectedBorderId,y,m){vm.repo.db.truckAccountingFor(trucks,y,m)}
    val borders=remember(rev){vm.repo.db.listBorders()}; val borderTitle=borders.firstOrNull{it.id==vm.selectedBorderId}?.name?:"همه مرزها"
    Column(modifier.fillMaxSize()) {
        ScreenTitle("حسابرسی ماهانه",onBack={vm.back()})
        Row(Modifier.padding(horizontal=16.dp)){BorderSelector(vm)}
        Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){IconButton(onClick=vm::nextAccountingMonth){Icon(Icons.Default.ChevronRight,null,tint=Purple)};Text(Jalali.Date(y,m,1).monthTitle(),fontWeight=FontWeight.Bold,fontSize=20.sp);IconButton(onClick=vm::previousAccountingMonth){Icon(Icons.Default.ChevronLeft,null,tint=Purple)}}
        Row(Modifier.fillMaxWidth().padding(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.weight(1f)){StatCard("ماشین‌ها",Formatters.number(summary.truckCount),Icons.Default.LocalShipping,Purple)};Box(Modifier.weight(1f)){StatCard("مجموع وزن",Formatters.weightKg(summary.totalWeightKg),Icons.Default.Scale,WaitingGreen)}}
        Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            Button(onClick={val f=PdfExporter.monthlyReport(context,y,m,borderTitle,trucks,accountings,truckAccounting=truckCosts);PdfExporter.share(context,f)},modifier=Modifier.weight(1f),enabled=trucks.isNotEmpty(),shape=RoundedCornerShape(13.dp)){Icon(Icons.Default.PictureAsPdf,null);Spacer(Modifier.width(5.dp));Text("PDF گزارش کلی ماه")}
        }
        Text("صاحبان کالا در این ماه",fontWeight=FontWeight.Bold,fontSize=17.sp,modifier=Modifier.padding(horizontal=16.dp,vertical=5.dp))
        AccountingTableHeader()
        LazyColumn(
            modifier = Modifier.heightIn(max = 235.dp).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ownerSummaries, key = { "accounting-row-${it.owner.id}" }) { s ->
                AccountingTableRow(
                    rowNumber = ownerSummaries.indexOf(s) + 1,
                    owner = s.owner.name,
                    truckCount = s.truckCount,
                    total = s.accounting?.total ?: 0L,
                    paid = s.accounting?.paidAmount ?: 0L,
                    remaining = s.accounting?.remaining ?: 0L
                )
            }
        }
        if(ownerSummaries.isEmpty())EmptyState("در این ماه ماشینی ثبت نشده")else LazyColumn(Modifier.weight(1f),contentPadding=PaddingValues(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){items(ownerSummaries,key={it.owner.id}){s->
            Card(Modifier.fillMaxWidth().clickable{vm.navigate(Screen.OwnerAccounting(s.owner.id,y,m))},shape=RoundedCornerShape(17.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Surface(color=PurpleSoft,shape=RoundedCornerShape(8.dp)){Text("${ownerSummaries.indexOf(s)+1}",fontWeight=FontWeight.Bold,color=Purple,modifier=Modifier.padding(horizontal=9.dp,vertical=6.dp))};Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(s.owner.name,fontWeight=FontWeight.Bold,fontSize=16.sp);Text("${s.truckCount} ماشین • ${Formatters.weightKg(s.totalWeightKg)}",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.62f));s.accounting?.let{Text("جمع هزینه: ${Formatters.toman(it.total)} • مانده: ${Formatters.toman(it.remaining)}",fontSize=11.sp,color=Purple)}};Icon(Icons.Default.PictureAsPdf,null,tint=Purple)}}
        };item{Spacer(Modifier.height(16.dp))}}
    }
}

@Composable
fun OwnerAccountingScreen(vm: AppViewModel, ownerId: Long, year: Int, month: Int, modifier: Modifier = Modifier) {
    val context=LocalContext.current; val rev by vm.repo.revision.collectAsState(); val owner=remember(rev,ownerId){vm.repo.db.getOwner(ownerId)}
    val trucks=remember(rev,ownerId,year,month){vm.repo.db.monthlyTrucks(owner?.borderId,year,month,ownerId)}
    val saved=remember(rev,ownerId,year,month){vm.repo.db.getAccounting(ownerId,year,month)}
    val truckCosts=remember(rev,ownerId,year,month){vm.repo.db.truckAccountingFor(trucks,year,month)}
    val borders=remember(rev){vm.repo.db.listBorders()}; val borderTitle=borders.firstOrNull{it.id==owner?.borderId}?.name?:"-"
    var clearance by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.clearanceFee?.toString()?:"")};var declaration by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.declarationFee?.toString()?:"")};var freight by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.freightFee?.toString()?:"")};var crane by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.craneFee?.toString()?:"")};var misc by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.miscFee?.toString()?:"")};var paid by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.paidAmount?.toString()?:"")};var note by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.note?:"")};var invoice by remember(saved?.id,saved?.updatedAt){mutableStateOf(saved?.invoiceNo?:"INV-$year-${month.toString().padStart(2,'0')}-$ownerId")}
    if(owner==null){EmptyState("صاحب کالا پیدا نشد");return}
    fun money(s:String)=s.filter(Char::isDigit).toLongOrNull()?:0L
    val total=money(clearance)+money(declaration)+money(freight)+money(crane)+money(misc);val remaining=total-money(paid)
    fun current()=Accounting(ownerId=ownerId,jalaliYear=year,jalaliMonth=month,clearanceFee=money(clearance),declarationFee=money(declaration),freightFee=money(freight),craneFee=money(crane),miscFee=money(misc),paidAmount=money(paid),note=note,invoiceNo=invoice)
    Column(modifier.fillMaxSize()) {
        ScreenTitle("صورت‌حساب ${owner.name}",onBack={vm.back()})
        LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
            item{Card(colors=CardDefaults.cardColors(containerColor=PurpleSoft),shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(15.dp)){Text(Jalali.Date(year,month,1).monthTitle(),fontWeight=FontWeight.Bold,color=Purple);Text("${trucks.size} ماشین • ${Formatters.weightKg(trucks.sumOf{it.weightKg})}",fontSize=13.sp);Text("هزینه‌ها برای هر ماشین جدا ذخیره می‌شود؛ اطلاعات حساب کلی قبلی هم نگه داشته شده است.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f))}}}
            item{OutlinedTextField(invoice,{invoice=it},label={Text("شماره صورت‌حساب")},modifier=Modifier.fillMaxWidth(),singleLine=true)}
            item{Text("حساب کلی صاحب کالا (اطلاعات قبلی)",fontWeight=FontWeight.Bold,fontSize=14.sp)}
            item{MoneyField("حق ترخیص کلی",clearance){clearance=it}}
            item{MoneyField("هزینه اظهار کلی",declaration){declaration=it}}
            item{MoneyField("مبلغ کرایه کلی",freight){freight=it}}
            item{MoneyField("هزینه جرثقیل کلی",crane){crane=it}}
            item{MoneyField("هزینه‌های متفرقه کلی",misc){misc=it}}
            item{MoneyField("مبلغ پرداخت‌شده کلی",paid){paid=it}}
            item{OutlinedTextField(note,{note=it},label={Text("توضیحات حسابداری")},modifier=Modifier.fillMaxWidth(),minLines=2)}
            item{Card(colors=CardDefaults.cardColors(containerColor=PurpleSoft),shape=RoundedCornerShape(16.dp)){Column(Modifier.padding(15.dp)){Row(Modifier.fillMaxWidth()){Text("جمع کل هزینه‌ها",modifier=Modifier.weight(1f),fontWeight=FontWeight.Bold);Text(Formatters.toman(total),fontWeight=FontWeight.Bold,color=Purple)};Spacer(Modifier.height(6.dp));Row(Modifier.fillMaxWidth()){Text("مانده حساب",modifier=Modifier.weight(1f));Text(Formatters.toman(remaining),fontWeight=FontWeight.Bold,color=if(remaining>0)ParkingRed else WaitingGreen)};Text(if(remaining<=0)"تسویه‌شده" else "بدهکار",color=if(remaining<=0)WaitingGreen else ParkingRed,fontSize=12.sp,fontWeight=FontWeight.Bold)}}}
            item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick={vm.repo.write{saveAccounting(current())};Toast.makeText(context,"حساب کلی ذخیره شد",Toast.LENGTH_SHORT).show()},modifier=Modifier.weight(1f)){Icon(Icons.Default.Save,null);Text(" ذخیره")};Button(onClick={val a=current();vm.repo.write{saveAccounting(a)};val f=PdfExporter.ownerInvoice(context,owner,year,month,borderTitle,trucks,a,truckCosts);PdfExporter.share(context,f)},modifier=Modifier.weight(1f),enabled=trucks.isNotEmpty()){Icon(Icons.Default.PictureAsPdf,null);Text(" PDF اختصاصی")}}}
            item{Text("هزینه‌های اختصاصی هر ماشین",fontWeight=FontWeight.Bold,fontSize=17.sp,modifier=Modifier.padding(top=8.dp))}
            items(trucks,key={it.id}){t->
                Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    TruckCard(t,onEdit={vm.navigate(Screen.TruckForm(t.cargoTypeId,t.id))})
                    TruckCostEditorRow(t, year, month, truckCosts[t.id]) { cost -> vm.repo.write { saveTruckAccounting(cost) } }
                }
            }
            item{Spacer(Modifier.height(16.dp))}
        }
    }
}

@Composable
private fun TruckCostEditorRow(
    truck: Truck,
    year: Int,
    month: Int,
    saved: TruckAccounting?,
    onSave: (TruckAccounting) -> Unit
) {
    var editing by remember(truck.id, saved?.updatedAt) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f)),
        shape = RoundedCornerShape(13.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ردیف ${truck.plate} • هزینه‌های این ماشین", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    if (saved == null) "هزینه‌ای ثبت نشده" else "جمع ${Formatters.toman(saved.total)} • پرداخت ${Formatters.toman(saved.paidAmount)} • مانده ${Formatters.toman(saved.remaining)}",
                    fontSize = 11.sp,
                    color = Purple
                )
            }
            TextButton(onClick = { editing = true }) { Text(if (saved == null) "ثبت هزینه" else "ویرایش") }
        }
    }
    if (editing) TruckCostDialog(truck, year, month, saved, onDismiss = { editing = false }) { cost ->
        onSave(cost)
        editing = false
    }
}

@Composable
private fun TruckCostDialog(
    truck: Truck,
    year: Int,
    month: Int,
    saved: TruckAccounting?,
    onDismiss: () -> Unit,
    onSave: (TruckAccounting) -> Unit
) {
    var clearance by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.clearanceFee?.takeIf { it != 0L }?.toString() ?: "") }
    var declaration by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.declarationFee?.takeIf { it != 0L }?.toString() ?: "") }
    var freight by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.freightFee?.takeIf { it != 0L }?.toString() ?: "") }
    var crane by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.craneFee?.takeIf { it != 0L }?.toString() ?: "") }
    var misc by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.miscFee?.takeIf { it != 0L }?.toString() ?: "") }
    var paid by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.paidAmount?.takeIf { it != 0L }?.toString() ?: "") }
    var note by remember(truck.id, saved?.updatedAt) { mutableStateOf(saved?.note ?: "") }
    fun amount(text: String) = text.filter(Char::isDigit).toLongOrNull() ?: 0L
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("هزینه‌های ماشین ${truck.plate}") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 470.dp)) {
                item { MoneyField("حق ترخیص", clearance) { clearance = it } }
                item { MoneyField("هزینه اظهار", declaration) { declaration = it } }
                item { MoneyField("کرایه", freight) { freight = it } }
                item { MoneyField("جرثقیل", crane) { crane = it } }
                item { MoneyField("هزینه متفرقه", misc) { misc = it } }
                item { MoneyField("پرداخت‌شده برای این ماشین", paid) { paid = it } }
                item { OutlinedTextField(note, { note = it }, label = { Text("توضیح هزینه") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(TruckAccounting(
                    id = saved?.id ?: 0,
                    truckId = truck.id,
                    jalaliYear = year,
                    jalaliMonth = month,
                    clearanceFee = amount(clearance),
                    declarationFee = amount(declaration),
                    freightFee = amount(freight),
                    craneFee = amount(crane),
                    miscFee = amount(misc),
                    paidAmount = amount(paid),
                    note = note
                ))
            }) { Text("ذخیره هزینه این ماشین") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
fun ReportsScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val rev by vm.repo.revision.collectAsState()
    val today = Jalali.now().date
    val dayTrucks = remember(rev, vm.selectedBorderId, today.year, today.month, today.day) {
        vm.repo.db.trucksOnJalaliDay(vm.selectedBorderId, today.year, today.month, today.day)
    }
    val monthTrucks = remember(rev, vm.selectedBorderId, today.year, today.month) {
        vm.repo.db.monthlyTrucks(vm.selectedBorderId, today.year, today.month)
    }
    val accountings = remember(rev, vm.selectedBorderId, today.year, today.month) {
        vm.repo.db.monthlyAccounting(vm.selectedBorderId, today.year, today.month)
    }
    val borderTitle = remember(rev, vm.selectedBorderId) {
        vm.repo.db.listBorders().firstOrNull { it.id == vm.selectedBorderId }?.name ?: "همه مرزها"
    }
    Column(modifier.fillMaxSize()) {
        ScreenTitle("گزارش‌ها", onBack = { vm.back() })
        Row(Modifier.padding(horizontal = 16.dp)) { BorderSelector(vm) }
        Text("گزارش امروز: ${today.format()}", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val costs = vm.repo.db.truckAccountingFor(dayTrucks, today.year, today.month)
                    val f = PdfExporter.dailyReport(context, today.year, today.month, today.day, borderTitle, dayTrucks, accountings, costs)
                    PdfExporter.share(context, f)
                },
                enabled = dayTrucks.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) { Icon(Icons.Default.PictureAsPdf, null); Spacer(Modifier.width(4.dp)); Text("PDF روزانه") }
            OutlinedButton(
                onClick = {
                    val costs = vm.repo.db.truckAccountingFor(monthTrucks, today.year, today.month)
                    val f = PdfExporter.monthlyReport(context, today.year, today.month, borderTitle, monthTrucks, accountings, truckAccounting=costs)
                    PdfExporter.share(context, f)
                },
                enabled = monthTrucks.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) { Icon(Icons.Default.PictureAsPdf, null); Spacer(Modifier.width(4.dp)); Text("PDF ماهانه") }
        }
        Text("پیش‌نمایش جدولی • هر ردیف شماره مستقل دارد", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .62f), modifier = Modifier.padding(16.dp, 14.dp, 16.dp, 6.dp))
        ReportTableHeader()
        if (dayTrucks.isEmpty()) {
            EmptyState("برای امروز گزارشی ثبت نشده")
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(dayTrucks, key = { "report-row-${it.id}" }) { truck ->
                    ReportTableRow(dayTrucks.indexOf(truck) + 1, truck)
                }
            }
        }
    }
}

@Composable
private fun ReportTableHeader() {
    Surface(color = PurpleSoft, shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("ردیف", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
            Text("پلاک", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("صاحب کالا", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
            Text("نوبت", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(.7f))
            Text("وضعیت", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ReportTableRow(rowNumber: Int, truck: Truck) {
    Card(shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(rowNumber.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple, modifier = Modifier.width(38.dp))
            Text(truck.plate, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(truck.ownerName, fontSize = 10.sp, modifier = Modifier.weight(1.2f), maxLines = 1)
            Text(if (truck.parkingQueue > 0) truck.parkingQueue.toString() else "-", fontSize = 11.sp, modifier = Modifier.weight(.7f))
            Text(truck.status.titleFa, fontSize = 10.sp, color = Purple, modifier = Modifier.weight(1f), maxLines = 1)
        }
    }
}

@Composable
private fun MoneyField(label:String,value:String,onValue:(String)->Unit){OutlinedTextField(value,{onValue(it.filter(Char::isDigit))},label={Text(label)},suffix={Text("تومان")},modifier=Modifier.fillMaxWidth(),singleLine=true)}

@Composable
private fun AccountingTableHeader() {
    Surface(color = PurpleSoft, shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("ردیف", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
            Text("صاحب کالا", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
            Text("ماشین", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(.65f))
            Text("کل", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("مانده", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AccountingTableRow(rowNumber: Int, owner: String, truckCount: Int, total: Long, paid: Long, remaining: Long) {
    Card(shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(rowNumber.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple, modifier = Modifier.width(38.dp))
            Text(owner, fontSize = 11.sp, modifier = Modifier.weight(1.3f), maxLines = 1)
            Text(truckCount.toString(), fontSize = 11.sp, modifier = Modifier.weight(.65f))
            Text(Formatters.toman(total), fontSize = 10.sp, modifier = Modifier.weight(1f), maxLines = 1)
            Text(Formatters.toman(remaining), fontSize = 10.sp, color = if (remaining > 0) ParkingRed else WaitingGreen, modifier = Modifier.weight(1f), maxLines = 1)
        }
    }
}

@Composable
fun SettingsScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val prefs=vm.prefs; var company by remember{mutableStateOf(prefs.companyName)};var phone by remember{mutableStateOf(prefs.companyPhone)};var address by remember{mutableStateOf(prefs.companyAddress)}
    Column(modifier.fillMaxSize()) {
        ScreenTitle("تنظیمات",onBack={vm.back()})
        LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            item{Text("حساب کاربری و بکاپ",fontWeight=FontWeight.Bold,fontSize=17.sp)}
            item{Card(colors=CardDefaults.cardColors(containerColor=PurpleSoft),shape=RoundedCornerShape(17.dp)){Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text("حساب متصل",fontWeight=FontWeight.Bold,color=Purple);Text(vm.accountEmail.ifBlank{"—"},fontSize=13.sp);if(vm.syncMessage.isNotBlank())Text(vm.syncMessage,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={vm.syncNow()},enabled=!vm.syncBusy,modifier=Modifier.weight(1f)){Icon(Icons.Default.CloudSync,null);Text(if(vm.syncBusy)" در حال همگام‌سازی" else " همگام‌سازی")};OutlinedButton(onClick={vm.logout()},modifier=Modifier.weight(1f)){Icon(Icons.Default.Logout,null);Text(" خروج")}}}}}
            item{Text("ظاهر برنامه",fontWeight=FontWeight.Bold,fontSize=17.sp)}
            item{Card(shape=RoundedCornerShape(17.dp)){Column(Modifier.padding(14.dp)){Text("تم",fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){FilterChip(vm.themeMode=="LIGHT",{vm.setTheme("LIGHT")},{Text("سفید و بنفش")});FilterChip(vm.themeMode=="DARK",{vm.setTheme("DARK")},{Text("تیره")});FilterChip(vm.themeMode=="SYSTEM",{vm.setTheme("SYSTEM")},{Text("سیستم")})};Row(verticalAlignment=Alignment.CenterVertically){Text("نمایش فشرده لیست‌ها",modifier=Modifier.weight(1f));Switch(vm.compactMode,{vm.setCompact(it)})}}}}
            item{Text("مشخصات برای PDF",fontWeight=FontWeight.Bold,fontSize=17.sp)}
            item{OutlinedTextField(company,{company=it},label={Text("نام بازرگانی / شرکت")},modifier=Modifier.fillMaxWidth(),singleLine=true)}
            item{OutlinedTextField(phone,{phone=it},label={Text("شماره تماس")},modifier=Modifier.fillMaxWidth(),singleLine=true)}
            item{OutlinedTextField(address,{address=it},label={Text("نشانی")},modifier=Modifier.fillMaxWidth())}
            item{Button(onClick={prefs.companyName=company;prefs.companyPhone=phone;prefs.companyAddress=address},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Save,null);Text(" ذخیره مشخصات PDF")}}
            item{HorizontalDivider()}
            item{SectionCard("مدیریت مرزها","افزودن، ویرایش و حذف مرزها",Icons.Default.LocationOn){vm.navigate(Screen.Borders)}}
            item{Card(colors=CardDefaults.cardColors(containerColor=PurpleSoft),shape=RoundedCornerShape(17.dp)){Column(Modifier.padding(14.dp)){Text("نسخه اصلی برنامه",fontWeight=FontWeight.Bold,color=Purple);Text("نسخه ${BuildConfig.VERSION_NAME}",fontSize=12.sp);Text("داده‌ها داخل گوشی باقی می‌مانند و هنگام ورود به حساب، بکاپ ابری شخصی همگام می‌شود. PDFها هم از اطلاعات واقعی برنامه ساخته می‌شوند.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f))}}}
            item{Spacer(Modifier.height(18.dp))}
        }
    }
}

@Composable
fun BordersScreen(vm: AppViewModel, modifier: Modifier = Modifier) {
    val rev by vm.repo.revision.collectAsState();val borders=remember(rev){vm.repo.db.listBorders()};var add by remember{mutableStateOf(false)};var edit by remember{mutableStateOf<Border?>(null)};var delete by remember{mutableStateOf<Border?>(null)}
    Column(modifier.fillMaxSize()) {
        ScreenTitle("مدیریت مرزها",onBack={vm.back()},action={FilledIconButton(onClick={add=true},colors=IconButtonDefaults.filledIconButtonColors(containerColor=Purple)){Icon(Icons.Default.Add,null)}})
        Text("برای هر مرز اطلاعات جدا ذخیره می‌شود. گزینه «همه مرزها» فقط برای مشاهده و گزارش تجمیعی است.",modifier=Modifier.padding(horizontal=16.dp,vertical=8.dp),fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.62f))
        LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(borders,key={it.id}){b->Card(shape=RoundedCornerShape(16.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.LocationOn,null,tint=Purple);Spacer(Modifier.width(10.dp));Text(b.name,modifier=Modifier.weight(1f),fontWeight=FontWeight.Bold);IconButton({edit=b}){Icon(Icons.Default.Edit,null)};IconButton({delete=b}){Icon(Icons.Default.Delete,null,tint=ParkingRed)}}}}}
    }
    if(add) BorderDialog(null,{add=false}){name->vm.repo.write{addBorder(name)};add=false}
    edit?.let{b->BorderDialog(b,{edit=null}){name->vm.repo.write{updateBorder(b.id,name)};edit=null}}
    delete?.let{b->ConfirmDeleteDialog(b.name,{delete=null}){if(vm.selectedBorderId==b.id)vm.selectedBorderId=null;vm.repo.write{deleteBorder(b.id)};delete=null}}
}

@Composable
private fun BorderDialog(border:Border?,onDismiss:()->Unit,onSave:(String)->Unit){var name by remember(border?.id){mutableStateOf(border?.name?:"")};AlertDialog(onDismissRequest=onDismiss,title={Text(if(border==null)"افزودن مرز" else "ویرایش مرز")},text={OutlinedTextField(name,{name=it},label={Text("نام مرز")},singleLine=true)},confirmButton={Button(onClick={if(name.isNotBlank())onSave(name)}){Text("ذخیره")}},dismissButton={TextButton(onClick=onDismiss){Text("انصراف")}})}
