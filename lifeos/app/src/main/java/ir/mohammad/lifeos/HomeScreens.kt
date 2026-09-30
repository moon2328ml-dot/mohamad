package ir.mohammad.lifeos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dash(
    val icon:String,
    val title:String,
    val sub:String,
    val screen:Screen,
    val c1:Color,
    val c2:Color
)

@Composable
fun AccountScreen(
    store:LifeStore,
    dark:Boolean,
    onToggleDark:()->Unit,
    onDone:()->Unit,
    onBack:(()->Unit)?
){
    var name by remember { mutableStateOf(store.name) }
    var email by remember { mutableStateOf(store.email) }
    var target by remember { mutableStateOf(store.targetWeight.toString()) }

    Page("تنظیمات", onBack){
        Spacer(Modifier.height(12.dp))
        GlassCard(strong=true){
            Row(verticalAlignment=Alignment.CenterVertically){
                Image(
                    painter=painterResource(R.drawable.app_logo),
                    contentDescription="متد محمد",
                    contentScale=ContentScale.Crop,
                    modifier=Modifier.size(86.dp).clip(RoundedCornerShape(22.dp)).border(1.5.dp,Gold,RoundedCornerShape(22.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)){
                    Text("متد محمد",fontSize=24.sp,fontWeight=FontWeight.ExtraBold)
                    Text("سیستم شخصی زندگی من",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        SectionTitle("ظاهر برنامه")
        GlassCard{
            Row(verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)){
                    Text(if(dark) "حالت دارک" else "حالت روشن",fontWeight=FontWeight.Bold)
                    Text("پنل شیشه‌ای، حاشیه طلایی و نورپردازی مرجع",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked=dark,
                    onCheckedChange={onToggleDark()},
                    colors=SwitchDefaults.colors(checkedThumbColor=Gold,checkedTrackColor=GoldDeep)
                )
            }
        }

        SectionTitle("پروفایل شخصی")
        OutlinedTextField(name,{name=it},label={Text("نام")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(email,{email=it},label={Text("ایمیل")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(target,{target=it},label={Text("وزن هدف")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Spacer(Modifier.height(14.dp))
        PrimaryButton("ذخیره تنظیمات"){
            if(name.isNotBlank()){
                store.name=name.trim()
                store.email=email.trim()
                store.targetWeight=target.toDoubleOrNull() ?: store.targetWeight
                onDone()
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "این نسخه تک‌کاربره است. اطلاعات فعلاً روی گوشی و Android Backup نگهداری می‌شود.",
            fontSize=11.sp,
            textAlign=TextAlign.Center,
            color=MaterialTheme.colorScheme.onSurfaceVariant,
            modifier=Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HomeScreen(
    store:LifeStore,
    version:Int,
    dark:Boolean,
    onToggleDark:()->Unit,
    onOpen:(Screen)->Unit
){
    val routine=store.loadRoutine()
    val goals=store.items("daily_goal")
    val totalToday=routine.size+goals.size
    val done=routine.count{it.done}+goals.count{it.done}
    val progress=if(totalToday==0) 0 else (done*100/totalToday).coerceIn(0,100)
    val investments=store.loadMoney().filter{it.kind=="investment"}.sumOf{it.amount}
    val latestWeight=store.loadWeights().lastOrNull()?.weight

    val cards=listOf(
        Dash("◉","رشد فردی","اعتماد به نفس • عادت‌ها • انگیزه",Screen.GROWTH,Color(0xFF3422C9),Color(0xFF23B6FF)),
        Dash("▥","موفقیت مالی","درآمد • سرمایه‌گذاری • هوش مالی",Screen.FINANCE,Color(0xFFB46A00),Color(0xFFFFD348)),
        Dash("♥","روابط عاطفی","رابطه سالم • ارتباط • رشد مشترک",Screen.RELATIONSHIP,Color(0xFFD90B3E),Color(0xFFFF3E88)),
        Dash("◆","سلامت و بدن","تغذیه • ورزش • خواب • انرژی",Screen.BODY,Color(0xFF5D2415),Color(0xFFFF6B2C)),
        Dash("♨","معنویت و آرامش","عبادت • آرامش ذهن • معنا",Screen.SPIRITUAL,Color(0xFF8A5B00),Color(0xFFFFD45B)),
        Dash("◐","روانشناسی سیاه","دفاع • مرزبندی • قدرت اجتماعی",Screen.POWER,Color(0xFF35004A),Color(0xFFFF2A87)),
        Dash("∪","قانون جذب","باور • تجسم • تمرین • اقدام",Screen.ATTRACTION,Color(0xFF39229A),Color(0xFF00C8FF)),
        Dash("▥","آنالیز من","اسکن شخصیت • نقاط قوت • مسیر اصلاح",Screen.ANALYSIS,Color(0xFF123872),Color(0xFF22D4FF))
    )

    Box(Modifier.fillMaxSize()){
        AppBackground()
        Column(Modifier.fillMaxSize().statusBarsPadding()){
            Row(
                Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=7.dp),
                verticalAlignment=Alignment.CenterVertically
            ){
                Box(
                    Modifier.size(48.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Gold,GoldDeep)))
                        .border(1.dp,Gold,CircleShape),
                    contentAlignment=Alignment.Center
                ){
                    Text(store.name.ifBlank{"م"}.take(1),color=Ink,fontWeight=FontWeight.ExtraBold,fontSize=19.sp)
                }
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)){
                    Text(store.name.ifBlank{"محمد"},fontWeight=FontWeight.ExtraBold,fontSize=18.sp)
                    Text("نسخه بهتر من در حال ساخته‌شدن است",fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
                GlassIconButton(if(dark) "☀" else "☾",onToggleDark)
            }

            LazyColumn(
                Modifier.weight(1f),
                contentPadding=PaddingValues(start=10.dp,end=10.dp,bottom=12.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp)
            ){
                item{
                    Box(
                        Modifier.fillMaxWidth().height(176.dp)
                            .shadow(12.dp,RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.5.dp,Gold,RoundedCornerShape(24.dp))
                    ){
                        Image(
                            painter=painterResource(R.drawable.app_logo),
                            contentDescription="متد محمد",
                            contentScale=ContentScale.Crop,
                            modifier=Modifier.fillMaxSize()
                        )
                        Box(
                            Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(.08f),Color.Black.copy(.84f)))
                            )
                        )
                        Column(
                            Modifier.fillMaxSize().padding(13.dp),
                            verticalArrangement=Arrangement.Bottom
                        ){
                            Text("متد محمد",color=Gold,fontSize=22.sp,fontWeight=FontWeight.ExtraBold)
                            Text("زندگی آگاهانه، قدرت واقعی، نسخه بهتر من",color=Color.White,fontSize=11.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment=Alignment.CenterVertically){
                                LinearProgressIndicator(
                                    progress={progress/100f},
                                    modifier=Modifier.weight(1f).height(8.dp).clip(CircleShape),
                                    color=Success,
                                    trackColor=Color.White.copy(.25f)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(progress.toString()+"%",color=Color.White,fontWeight=FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                item{
                    PrimaryButton("ادامه مسیر امروز"){
                        onOpen(if(routine.isNotEmpty()) Screen.ROUTINE else Screen.DAILY_GOALS)
                    }
                }

                item{
                    LazyVerticalGrid(
                        columns=GridCells.Fixed(4),
                        modifier=Modifier.fillMaxWidth().height(205.dp),
                        userScrollEnabled=false,
                        horizontalArrangement=Arrangement.spacedBy(6.dp),
                        verticalArrangement=Arrangement.spacedBy(7.dp)
                    ){
                        gridItems(cards){card->HomeCategoryTile(card,onOpen)}
                    }
                }

                item{
                    Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){
                        StatPill("مسیر امروز",progress.toString()+"%",Modifier.weight(1f))
                        StatPill("سرمایه",money(investments),Modifier.weight(1f))
                        StatPill("وزن",latestWeight?.let{it.toString()+" kg"} ?: "—",Modifier.weight(1f))
                    }
                }

                item{
                    GlassCard(strong=true){
                        Text("کنترل زندگی در دستان توست",fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=Gold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if(totalToday==0) "برای امروز هنوز برنامه‌ای نساختی. برنامه و روتین را خودت می‌چینی."
                            else if(progress==100) "مسیر امروز کامل شد؛ نتیجه را ثبت کن و برای فردا آماده شو."
                            else "فقط روی قدم بعدی تمرکز کن؛ برنامه جای تو تصمیم نمی‌گیرد.",
                            fontSize=12.sp,
                            color=MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            BottomNav(Screen.HOME,onOpen)
        }
    }
}

@Composable
private fun HomeCategoryTile(card:Dash,onOpen:(Screen)->Unit){
    Box(
        Modifier.fillMaxWidth().height(96.dp)
            .shadow(7.dp,RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp,Gold.copy(.88f),RoundedCornerShape(16.dp))
            .clickable{onOpen(card.screen)}
    ){
        CategoryReferenceArt(card.screen,Modifier.fillMaxSize())
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent,Color.Transparent,Color.Black.copy(.78f))
                )
            )
        )
        Text(
            card.title,
            color=Color.White,
            fontSize=9.sp,
            fontWeight=FontWeight.ExtraBold,
            textAlign=TextAlign.Center,
            modifier=Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal=3.dp,vertical=6.dp)
        )
    }
}

@Composable
fun BottomNav(current:Screen,onOpen:(Screen)->Unit){
    val dark=LocalDarkMode.current
    val nav=listOf(
        Triple("⌂","خانه",Screen.HOME),
        Triple("✓","برنامه من",Screen.ROUTINE),
        Triple("▥","گزارش‌ها",Screen.REPORTS),
        Triple("▤","کتابخانه",Screen.LIBRARY),
        Triple("⚙","تنظیمات",Screen.ACCOUNT)
    )
    Surface(
        color=if(dark) Color(0xEE090C12) else Color(0xF7FFFFFF),
        shadowElevation=14.dp,
        tonalElevation=4.dp
    ){
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().height(66.dp),
            verticalAlignment=Alignment.CenterVertically
        ){
            nav.forEach{entry->
                val selected=current==entry.third
                Column(
                    Modifier.weight(1f).fillMaxHeight().clickable{onOpen(entry.third)}.padding(vertical=7.dp),
                    horizontalAlignment=Alignment.CenterHorizontally,
                    verticalArrangement=Arrangement.Center
                ){
                    Text(entry.first,color=if(selected)Gold else MaterialTheme.colorScheme.onSurfaceVariant,fontSize=18.sp,fontWeight=FontWeight.Bold)
                    Text(entry.second,color=if(selected)Gold else MaterialTheme.colorScheme.onSurfaceVariant,fontSize=9.sp,fontWeight=if(selected)FontWeight.Bold else FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ChecklistScreen(store:LifeStore,title:String,group:String,onBack:()->Unit,onChange:()->Unit){
    var currentItems by remember { mutableStateOf(store.items(group)) }
    var text by remember { mutableStateOf("") }
    Page(title,onBack){
        SectionTitle(
            if(group=="routine") "روتین را خودت بساز" else "لیست من",
            if(group=="routine") "این بخش عمداً از اول خالی است؛ برنامه، ترتیب و محتوایش فقط دست خودت است."
            else "مواردی که خودت تعریف می‌کنی اینجا ثبت می‌شوند."
        )

        if(currentItems.isNotEmpty()){
            val complete=currentItems.count{it.done}
            GlassCard{
                LinearProgressIndicator(
                    progress={complete.toFloat()/currentItems.size},
                    modifier=Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                    color=Gold,
                    trackColor=MaterialTheme.colorScheme.surfaceVariant
                )
                Text(complete.toString()+" از "+currentItems.size+" انجام شده",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.padding(top=6.dp))
            }
            Spacer(Modifier.height(8.dp))
        }

        Row(verticalAlignment=Alignment.CenterVertically){
            OutlinedTextField(
                text,{text=it},
                label={Text(if(group=="routine") "کار یا روتین جدید" else "مورد جدید")},
                modifier=Modifier.weight(1f),singleLine=true
            )
            Spacer(Modifier.width(7.dp))
            Button(
                onClick={
                    if(text.isNotBlank()){
                        store.addItem(SimpleItem(title=text.trim(),group=group))
                        text=""
                        currentItems=store.items(group)
                        onChange()
                    }
                },
                colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink),
                shape=RoundedCornerShape(14.dp)
            ){Text("+",fontSize=24.sp)}
        }

        Spacer(Modifier.height(9.dp))
        if(currentItems.isEmpty()){
            GlassCard{EmptyHint(if(group=="routine") "این صفحه خالی است تا اولین روتین را خودت بسازی." else "هنوز موردی ثبت نشده.")}
        }

        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(bottom=22.dp)){
            items(currentItems,key={it.id}){item->
                GlassCard{
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Checkbox(
                            checked=item.done,
                            onCheckedChange={
                                store.toggleItem(item.id)
                                currentItems=store.items(group)
                                onChange()
                            },
                            colors=CheckboxDefaults.colors(checkedColor=Gold,checkmarkColor=Ink)
                        )
                        Text(item.title,Modifier.weight(1f),fontWeight=if(item.done)FontWeight.Normal else FontWeight.Bold)
                        TextButton(onClick={
                            store.deleteItem(item.id)
                            currentItems=store.items(group)
                            onChange()
                        }){Text("حذف",color=Danger,fontSize=11.sp)}
                    }
                }
            }
        }
    }
}

@Composable
fun JournalScreen(store:LifeStore,title:String,kind:String,prompt:String,onBack:()->Unit,onChange:()->Unit){
    var text by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf(store.journals(kind)) }
    Page(title,onBack){
        SectionTitle(prompt)
        GlassCard{
            OutlinedTextField(
                text,{text=it},
                modifier=Modifier.fillMaxWidth().height(130.dp),
                placeholder={Text("اینجا بنویس…")}
            )
            Spacer(Modifier.height(8.dp))
            PrimaryButton("ثبت"){
                if(text.isNotBlank()){
                    store.addJournal(JournalEntry(kind=kind,text=text.trim()))
                    text=""
                    rows=store.journals(kind)
                    onChange()
                }
            }
        }
        SectionTitle("تاریخچه")
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(bottom=22.dp)){
            items(rows,key={it.id}){row->
                GlassCard{
                    Text(row.date,color=Gold,fontSize=10.sp,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(row.text)
                    TextButton(
                        onClick={
                            store.deleteJournal(row.id)
                            rows=store.journals(kind)
                            onChange()
                        },
                        modifier=Modifier.align(Alignment.End)
                    ){Text("حذف",color=Danger)}
                }
            }
        }
    }
}

@Composable
fun GrowthHubScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
    Page("رشد فردی",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=25.dp)){
            item{
                Box(
                    Modifier.fillMaxWidth().height(145.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.5.dp,Gold,RoundedCornerShape(22.dp))
                ){
                    Image(
                        painter=painterResource(R.drawable.growth_card),
                        contentDescription=null,
                        contentScale=ContentScale.Crop,
                        modifier=Modifier.fillMaxSize()
                    )
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(.82f)))))
                    Column(Modifier.fillMaxSize().padding(14.dp),verticalArrangement=Arrangement.Bottom){
                        Text("رشد فردی",color=Color.White,fontWeight=FontWeight.ExtraBold,fontSize=22.sp)
                        Text("اعتماد به نفس • عادت‌ها • انگیزه • مدیریت زمان",color=Color.White.copy(.9f),fontSize=11.sp)
                    }
                }
            }
            item{
                GlassCard{
                    Text("قدرت من",fontWeight=FontWeight.ExtraBold,color=Gold)
                    Text("اعتمادبه‌نفس، قول به خود، اقدام با وجود ترس و بانک شواهد.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item{
                GlassCard{
                    Text("آزمایشگاه رشد",fontWeight=FontWeight.ExtraBold,color=Gold)
                    Text("اتفاق → واکنش → محرک → نتیجه → پاسخ بهتر دفعه بعد",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item{
                GlassCard{
                    Text("دفتر رشد",fontWeight=FontWeight.ExtraBold,color=Gold)
                    Text("تعداد یادداشت‌های رشد: "+store.journals("growth").size,fontSize=12.sp)
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
    Page("کتابخانه",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=25.dp)){
            item{
                GlassCard(strong=true){
                    Text("کتابخانه شخصی متد محمد",fontWeight=FontWeight.ExtraBold,fontSize=19.sp,color=Gold)
                    Text("آموزش‌ها و تمرین‌ها به‌صورت موضوعی نگهداری می‌شوند.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(listOf(
                "قانون جذب" to "استادها، تمرین‌ها، پروژه خواسته‌ها و دفتر شواهد",
                "قدرت روانی" to "مرزبندی، قاطعیت، مذاکره و دفاع در برابر دستکاری",
                "رشد فردی" to "اعتمادبه‌نفس، عادت، تمرکز و مدیریت زمان",
                "مالی" to "بودجه، بدهی، سرمایه‌گذاری و آزادی مالی",
                "معنویت" to "من و خدا، شکرگزاری و مسیرهای معنوی شخصی"
            )){entry->
                GlassCard{
                    Text(entry.first,fontWeight=FontWeight.Bold,color=Gold)
                    Text(entry.second,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
