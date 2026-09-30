package ir.mohammad.lifeos

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttractionScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
    val teachers=listOf(
        "ناپلئون هیل" to "هدف دقیق، ایمان، تصمیم، پشتکار و Mastermind",
        "کوین ترودو" to "Teachability Index و مدیریت ورودی‌های ذهنی",
        "نویل گادرد" to "فرض نتیجه نهایی، SATS و Revision",
        "ابراهام هیکس" to "راهنمای هیجانی و توجه به حالت درونی",
        "روندا برن" to "خواستن، باور، دریافت، شکرگزاری و تجسم",
        "باب پراکتور" to "پارادایم، Goal Card و تغییر الگوهای ذهنی",
        "لوئیز هی" to "گفت‌وگوی درونی، تأییدها و Mirror Work",
        "وین دایر" to "نیت، معنا و همسویی با تصویر آینده",
        "جو دیسپنزا" to "مدیتیشن، تجسم و تمرین هویت آینده",
        "عباس منش" to "باورهای مالی، فراوانی و رزق",
        "محمد بصیری" to "باور مالی، هدف، چرایی و تصویرسازی",
        "امیر همتی" to "لیاقت، ترس، گفت‌وگوی درونی و ثروت"
    )
    var tab by remember { mutableIntStateOf(0) }
    var note by remember { mutableStateOf("") }
    var journal by remember { mutableStateOf(store.journals("manifestation")) }

    Page("قانون جذب",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=28.dp)){
            item{FeatureHero(Screen.ATTRACTION,"قانون جذب","اساتید • تمرین‌ها • پروژه خواسته‌ها • دفتر شواهد")}
            item{
                GlassCard(strong=true){
                    Row(verticalAlignment=Alignment.CenterVertically){
                        CosmicOrb(Modifier.size(78.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)){
                            Text("آکادمی قانون جذب",fontWeight=FontWeight.ExtraBold,fontSize=20.sp,color=Gold)
                            Text("آموزش • تمرین • پروژه خواسته‌ها • دفتر شواهد",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item{
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    listOf("اساتید","تمرین‌ها","پروژه‌ها").forEachIndexed{i,t->
                        FilterChip(
                            selected=tab==i,
                            onClick={tab=i},
                            label={Text(t)},
                            modifier=Modifier.weight(1f)
                        )
                    }
                }
            }

            if(tab==0){
                items(teachers){entry->
                    GlassCard{
                        Row(verticalAlignment=Alignment.CenterVertically){
                            Box(
                                Modifier.size(46.dp).clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(Gold,GoldDeep,Ink))),
                                contentAlignment=Alignment.Center
                            ){
                                Text(entry.first.take(1),color=Color.White,fontWeight=FontWeight.ExtraBold,fontSize=18.sp)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)){
                                Text(entry.first,fontWeight=FontWeight.ExtraBold)
                                Text(entry.second,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("▶",color=Gold)
                        }
                    }
                }
            } else if(tab==1){
                item{
                    GlassCard{
                        Text("تمرین امروز",fontWeight=FontWeight.ExtraBold,color=Gold)
                        Text("خواسته → باور فعلی → تصویر نتیجه → احساس → اقدام امروز → شواهد",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            note,{note=it},
                            modifier=Modifier.fillMaxWidth().height(145.dp),
                            placeholder={Text("تمرین امروز را بنویس…")}
                        )
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("ثبت در دفتر جذب"){
                            if(note.isNotBlank()){
                                store.addJournal(JournalEntry(kind="manifestation",text=note.trim()))
                                note=""
                                journal=store.journals("manifestation")
                                onChange()
                            }
                        }
                    }
                }
                items(journal.take(20),key={it.id}){row->
                    GlassCard{
                        Text(row.date,color=Gold,fontSize=10.sp)
                        Text(row.text)
                    }
                }
            } else {
                item{
                    GlassCard{
                        Text("پروژه خواسته‌ها",fontWeight=FontWeight.ExtraBold,color=Gold)
                        Text(
                            "هر خواسته یک پرونده مستقل دارد: چرایی، تصویر نهایی، باورهای کمک‌کننده و مخالف، تمرین، اقدام و شواهد.",
                            fontSize=12.sp,
                            color=MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(store.items("manifestation_project"),key={it.id}){x->
                    GlassCard{
                        Text(x.title,fontWeight=FontWeight.Bold)
                        Text(if(x.done) "تکمیل شده" else "در جریان",color=if(x.done)Success else Gold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CosmicOrb(modifier:Modifier=Modifier){
    Canvas(modifier){
        val c=Offset(size.width/2,size.height/2)
        drawCircle(
            Brush.radialGradient(listOf(Color.White,NeonBlue,NeonPurple,Color.Transparent),center=c,radius=size.minDimension*.52f),
            size.minDimension*.48f,c
        )
        drawCircle(Gold,size.minDimension*.18f,c,style=Stroke(size.minDimension*.025f))
        drawCircle(Color.White,size.minDimension*.045f,c)
    }
}

@Composable
fun PowerScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
    val modules=listOf(
        "شناخت رفتارهای پنهان" to "الگوهای فشار، گناه‌دادن، تناقض، تحقیر و مرزشکنی را ثبت و بررسی کن.",
        "تشخیص دستکاری" to "روی شواهد، تناقض روایت و درخواست واقعی طرف مقابل تمرکز کن.",
        "نفوذ و اقناع سالم" to "شفاف حرف بزن، ارزش پیشنهاد را توضیح بده و حق انتخاب طرف مقابل را حفظ کن.",
        "زبان بدن و ذهن" to "ایستادن باز، نگاه طبیعی، صدای شمرده و عجله نکردن.",
        "قاطعیت و مرزبندی" to "نه گفتن، مخالفت محترمانه و دفاع از حق خود بدون پرخاش.",
        "کنترل احساس دیگران؟ نه" to "هدف این بخش کنترل خود و تشخیص فشار است، نه وابسته‌کردن یا ترساندن دیگران.",
        "قدرت اجتماعی و کاری" to "شبکه‌سازی، مذاکره، بیان ارزش و آرامش زیر فشار.",
        "رهبری و مسئولیت" to "ثبات، تصمیم روشن، شنیدن و پایبندی به قول."
    )
    var tab by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }

    Page("روانشناسی سیاه",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(bottom=28.dp)){
            item{FeatureHero(Screen.POWER,"روانشناسی سیاه","شناخت انسان • دفاع در برابر دستکاری • قدرت اجتماعی")}
            item{
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    listOf("آموزش","تکنیک‌ها","سناریوها").forEachIndexed{i,t->
                        FilterChip(tab==i,{tab=i},{Text(t)},modifier=Modifier.weight(1f))
                    }
                }
            }
            if(tab<2){
                items(modules){entry->
                    GlassCard{
                        Text(entry.first,fontWeight=FontWeight.ExtraBold)
                        Text(entry.second,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                item{
                    GlassCard{
                        Text("Red Team من",fontWeight=FontWeight.ExtraBold,color=Gold)
                        Text("یک موقعیت سخت را بازسازی کن و پاسخ آرام، روشن و مرزبندی‌شده خودت را بنویس.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            text,{text=it},
                            modifier=Modifier.fillMaxWidth().height(135.dp),
                            placeholder={Text("سناریو و پاسخ انتخابی…")}
                        )
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("ثبت تمرین"){
                            if(text.isNotBlank()){
                                store.addJournal(JournalEntry(kind="power",text=text.trim()))
                                text=""
                                onChange()
                            }
                        }
                    }
                }
                items(store.journals("power").take(20),key={it.id}){x->
                    GlassCard{Text(x.date,color=Gold,fontSize=10.sp);Text(x.text)}
                }
            }
        }
    }
}

@Composable
private fun DarkPowerFigure(modifier:Modifier=Modifier){
    Canvas(modifier){
        val cx=size.width*.5f
        drawCircle(
            Brush.radialGradient(listOf(NeonPurple.copy(.5f),Color.Transparent),center=Offset(cx,size.height*.35f),radius=size.width*.38f),
            size.width*.38f,Offset(cx,size.height*.35f)
        )
        val hood=Path().apply{
            moveTo(cx,size.height*.10f)
            cubicTo(size.width*.29f,size.height*.25f,size.width*.27f,size.height*.60f,size.width*.34f,size.height*.85f)
            lineTo(size.width*.66f,size.height*.85f)
            cubicTo(size.width*.73f,size.height*.60f,size.width*.71f,size.height*.25f,cx,size.height*.10f)
            close()
        }
        drawPath(hood,Color(0xFF060609))
        drawPath(hood,Gold.copy(.42f),style=Stroke(3f))
        drawCircle(Color(0xFF15091D),size.width*.11f,Offset(cx,size.height*.39f))
        drawLine(NeonPink,Offset(cx-size.width*.06f,size.height*.39f),Offset(cx-size.width*.015f,size.height*.40f),4f)
        drawLine(NeonPink,Offset(cx+size.width*.015f,size.height*.40f),Offset(cx+size.width*.06f,size.height*.39f),4f)
        drawCircle(Gold.copy(.17f),size.width*.22f,Offset(cx,size.height*.72f))
    }
}

@Composable
fun SpiritualScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
    var acts by remember { mutableStateOf(store.items("spiritual")) }
    var newAct by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Page("معنویت و آرامش",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=28.dp)){
            item{FeatureHero(Screen.SPIRITUAL,"معنویت و آرامش","عبادت • مدیتیشن • آرامش ذهنی • معنا و هدف زندگی")}
            item{
                GlassCard(strong=true){
                    Text("من و خدا",fontWeight=FontWeight.ExtraBold,fontSize=20.sp,color=Gold)
                    Text("اعمال معنوی را خودت انتخاب می‌کنی؛ اینجا نمره ایمان یا ارزش انسانی وجود ندارد.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item{
                Row(verticalAlignment=Alignment.CenterVertically){
                    OutlinedTextField(newAct,{newAct=it},label={Text("عمل معنوی جدید")},modifier=Modifier.weight(1f),singleLine=true)
                    Spacer(Modifier.width(7.dp))
                    Button(
                        onClick={
                            if(newAct.isNotBlank()){
                                store.addItem(SimpleItem(title=newAct.trim(),group="spiritual"))
                                newAct=""
                                acts=store.items("spiritual")
                                onChange()
                            }
                        },
                        colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink)
                    ){Text("+")}
                }
            }
            if(acts.isEmpty()) item{GlassCard{EmptyHint("اعمال این بخش از قبل تعیین نشده‌اند؛ خودت اضافه‌شان کن.")}}
            items(acts,key={it.id}){x->
                GlassCard{
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Checkbox(
                            x.done,
                            {
                                store.toggleItem(x.id)
                                acts=store.items("spiritual")
                                onChange()
                            },
                            colors=CheckboxDefaults.colors(checkedColor=Gold,checkmarkColor=Ink)
                        )
                        Text(x.title,Modifier.weight(1f))
                        TextButton(onClick={
                            store.deleteItem(x.id)
                            acts=store.items("spiritual")
                            onChange()
                        }){Text("حذف",color=Danger)}
                    }
                }
            }
            item{
                SectionTitle("دفتر تجربه معنوی")
                GlassCard{
                    OutlinedTextField(note,{note=it},modifier=Modifier.fillMaxWidth().height(120.dp),placeholder={Text("تجربه امروز…")})
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("ثبت تجربه"){
                        if(note.isNotBlank()){
                            store.addJournal(JournalEntry(kind="spiritual_note",text=note.trim()))
                            note=""
                            onChange()
                        }
                    }
                }
            }
            item{
                GlassCard{
                    Text("آکادمی عرفان حلقه / فرادرمانی",fontWeight=FontWeight.Bold)
                    Text("مسیر آموزشی شخصی برای مطالعه و ثبت تجربه‌های معنوی؛ این بخش جایگزین درمان یا مراقبت پزشکی ضروری نیست.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AnalysisScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
    var mood by remember { mutableFloatStateOf(7f) }
    var discipline by remember { mutableFloatStateOf(6f) }
    var confidence by remember { mutableFloatStateOf(6f) }
    var anger by remember { mutableFloatStateOf(7f) }
    var loneliness by remember { mutableFloatStateOf(4f) }
    var focus by remember { mutableFloatStateOf(6f) }
    var checkins by remember { mutableStateOf(store.loadCheckIns()) }

    fun avg(f:(CheckIn)->Int):Double = if(checkins.isEmpty()) 0.0 else checkins.map(f).average()
    val aFocus=avg{it.focus}
    val aDis=avg{it.discipline}
    val aCon=avg{it.confidence}
    val aAng=avg{it.angerControl}
    val aMood=avg{it.mood}
    val aLonely=avg{it.loneliness}

    val strengths=listOf(
        "تمرکز" to aFocus,
        "انضباط" to aDis,
        "اعتماد به نفس" to aCon,
        "کنترل خشم" to aAng,
        "حال کلی" to aMood
    ).sortedByDescending{it.second}.take(3)

    val improve=listOf(
        "تمرکز" to (10.0-aFocus),
        "انضباط" to (10.0-aDis),
        "اعتماد به نفس" to (10.0-aCon),
        "کنترل خشم" to (10.0-aAng),
        "احساس تنهایی" to aLonely
    ).sortedByDescending{it.second}.take(3)

    Page("آنالیز من",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=28.dp)){
            item{
                FeatureHero(Screen.ANALYSIS,"آنالیز من","اسکن شخصیت • نقاط قوت • نقاط قابل بهبود • ردیابی پیشرفت")
            }
            item{
                GlassCard(strong=true){
                    Text("اسکن کامل شخصیت و رفتار شما",fontWeight=FontWeight.ExtraBold,fontSize=18.sp,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth().height(255.dp),contentAlignment=Alignment.Center){
                        ReferenceCrop(featureCrop(Screen.ANALYSIS),Modifier.size(170.dp,245.dp),22)
                        ScoreBubble("تفکر/تمرکز",aFocus,Modifier.align(Alignment.TopEnd))
                        ScoreBubble("روابط عاطفی",aMood,Modifier.align(Alignment.CenterEnd))
                        ScoreBubble("سلامت جسمی",if(store.loadWeights().isEmpty())0.0 else 7.5,Modifier.align(Alignment.BottomEnd))
                        ScoreBubble("کنترل هیجان",aAng,Modifier.align(Alignment.TopStart))
                        ScoreBubble("عزت نفس",aCon,Modifier.align(Alignment.CenterStart))
                        ScoreBubble("انضباط",aDis,Modifier.align(Alignment.BottomStart))
                    }
                    PrimaryButton("مشاهده تحلیل کامل"){}
                }
            }

            item{
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    GlassCard(Modifier.weight(1f)){
                        Text("نقاط قوت",color=Success,fontWeight=FontWeight.ExtraBold,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                        strengths.forEach{entry->
                            Text("• "+entry.first+(if(entry.second>0)"  "+String.format("%.1f",entry.second) else ""),fontSize=11.sp)
                        }
                    }
                    GlassCard(Modifier.weight(1f)){
                        Text("نقاط قابل بهبود",color=Danger,fontWeight=FontWeight.ExtraBold,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                        improve.forEach{entry->Text("• "+entry.first,fontSize=11.sp)}
                    }
                }
            }

            item{PrimaryButton("شروع مسیر اصلاح"){}}

            item{
                SectionTitle("چک‌این امروز","هر عدد یک ثبت روزانه است؛ نتیجه پزشکی یا برچسب شخصیت نیست.")
                GlassCard{
                    ScoreSlider("حال کلی",mood){mood=it}
                    ScoreSlider("انضباط",discipline){discipline=it}
                    ScoreSlider("اعتماد به نفس",confidence){confidence=it}
                    ScoreSlider("کنترل خشم",anger){anger=it}
                    ScoreSlider("احساس تنهایی",loneliness){loneliness=it}
                    ScoreSlider("تمرکز",focus){focus=it}
                    PrimaryButton("ثبت اسکن امروز"){
                        store.addCheckIn(
                            CheckIn(
                                mood=mood.toInt(),
                                discipline=discipline.toInt(),
                                confidence=confidence.toInt(),
                                angerControl=anger.toInt(),
                                loneliness=loneliness.toInt(),
                                focus=focus.toInt()
                            )
                        )
                        checkins=store.loadCheckIns()
                        onChange()
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisBody(modifier:Modifier=Modifier){
    Canvas(modifier){
        val cx=size.width/2
        val glow=Brush.radialGradient(
            listOf(Color.White.copy(.8f),NeonBlue.copy(.72f),NeonBlue.copy(.12f),Color.Transparent),
            center=Offset(cx,size.height*.45f),
            radius=size.width*.72f
        )
        drawCircle(glow,size.width*.65f,Offset(cx,size.height*.45f))
        val body=Color(0xFF5AE2FF)
        drawCircle(body,size.width*.11f,Offset(cx,size.height*.12f),style=Stroke(5f))
        drawLine(body,Offset(cx,size.height*.23f),Offset(cx,size.height*.66f),7f)
        drawLine(body,Offset(cx,size.height*.34f),Offset(cx-size.width*.24f,size.height*.52f),5f)
        drawLine(body,Offset(cx,size.height*.34f),Offset(cx+size.width*.24f,size.height*.52f),5f)
        drawLine(body,Offset(cx,size.height*.66f),Offset(cx-size.width*.18f,size.height*.94f),6f)
        drawLine(body,Offset(cx,size.height*.66f),Offset(cx+size.width*.18f,size.height*.94f),6f)
        listOf(
            .29f to NeonPurple,
            .39f to NeonBlue,
            .49f to Success,
            .59f to Gold
        ).forEach{entry->
            drawCircle(entry.second,size.width*.035f,Offset(cx,size.height*entry.first))
        }
        drawCircle(NeonPink,size.width*.045f,Offset(cx,size.height*.30f))
    }
}

@Composable
private fun ScoreBubble(label:String,value:Double,modifier:Modifier=Modifier){
    Column(modifier.width(82.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Text(label,fontSize=9.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
        Box(
            Modifier.size(42.dp).clip(CircleShape).background(Color(0xD8101620))
                .border(2.dp,if(value>=6)Success else if(value>0)Danger else Gold,CircleShape),
            contentAlignment=Alignment.Center
        ){
            Text(if(value<=0)"—" else String.format("%.1f",value),color=Color.White,fontWeight=FontWeight.ExtraBold,fontSize=12.sp)
        }
    }
}

@Composable
fun ScoreSlider(label:String,value:Float,onChange:(Float)->Unit){
    Row{
        Text(label,Modifier.weight(1f),fontWeight=FontWeight.Bold)
        Text(value.toInt().toString()+"/10",color=Gold,fontWeight=FontWeight.Bold)
    }
    Slider(
        value=value,
        onValueChange=onChange,
        valueRange=1f..10f,
        steps=8,
        colors=SliderDefaults.colors(thumbColor=Gold,activeTrackColor=Gold)
    )
}

@Composable
fun ReportsScreen(store:LifeStore,onBack:()->Unit){
    val checkins=store.loadCheckIns()
    Page("گزارش پیشرفت",onBack){
        LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=28.dp)){
            item{
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    listOf("هفته","ماه","سال").forEachIndexed{i,t->
                        FilterChip(selected=i==1,onClick={},label={Text(t)},modifier=Modifier.weight(1f))
                    }
                }
            }
            item{
                GlassCard(strong=true){
                    Text("روند پیشرفت",fontWeight=FontWeight.ExtraBold,fontSize=18.sp)
                    Spacer(Modifier.height(8.dp))
                    MultiProgressChart(checkins.takeLast(10))
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){
                        LegendDot(NeonPink,"حال")
                        LegendDot(NeonBlue,"تمرکز")
                        LegendDot(Success,"انضباط")
                        LegendDot(Gold,"کنترل خشم")
                    }
                }
            }
            item{
                val moneyRows=store.loadMoney()
                val spent=moneyRows.filter{it.kind=="expense"}.sumOf{it.amount}
                val inv=moneyRows.filter{it.kind=="investment"}.sumOf{it.amount}
                SectionTitle("خلاصه ماه")
                GlassCard{
                    ReportLine("بهترین پیشرفت",if(checkins.size>=2)"ادامه ثبت‌ها برای تحلیل روند" else "داده بیشتری ثبت کن",Success)
                    ReportLine("پایبندی برنامه",if(store.items("routine").isEmpty())"هنوز برنامه‌ای نساختی" else "بر اساس تیک‌های خودت",Gold)
                    ReportLine("خرج ثبت‌شده",money(spent),Danger)
                    ReportLine("سرمایه‌گذاری ثبت‌شده",money(inv),Success)
                    ReportLine("هدف ماه بعد","تمرکز روی یک تغییر مهم",Success)
                }
            }
            item{
                Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){
                    val inc=store.loadMoney().filter{it.kind=="income"}.sumOf{it.amount}+store.monthlyIncome
                    val exp=store.loadMoney().filter{it.kind=="expense"}.sumOf{it.amount}
                    val inv=store.loadMoney().filter{it.kind=="investment"}.sumOf{it.amount}
                    StatPill("درآمد",money(inc),Modifier.weight(1f))
                    StatPill("خرج",money(exp),Modifier.weight(1f))
                    StatPill("سرمایه",money(inv),Modifier.weight(1f))
                }
            }
            item{
                SectionTitle("روند وزن")
                MiniLineChart(store.loadWeights().takeLast(20).map{it.weight.toFloat()})
            }
        }
    }
}

@Composable
private fun MultiProgressChart(checkins:List<CheckIn>){
    Canvas(Modifier.fillMaxWidth().height(180.dp)){
        for(i in 0..4){
            val y=size.height*i/4f
            drawLine(MaterialTheme.colorScheme.onSurface.copy(.12f),Offset(0f,y),Offset(size.width,y),1f)
        }
        if(checkins.size<2) return@Canvas
        fun points(f:(CheckIn)->Int)=checkins.mapIndexed{i,v->
            Offset(size.width*i/(checkins.size-1),size.height-(f(v).coerceIn(0,10)/10f)*size.height)
        }
        fun series(p:List<Offset>,color:Color){
            for(i in 0 until p.lastIndex) drawLine(color,p[i],p[i+1],4f)
            p.forEach{drawCircle(color,5f,it)}
        }
        series(points{it.mood},NeonPink)
        series(points{it.focus},NeonBlue)
        series(points{it.discipline},Success)
        series(points{it.angerControl},Gold)
    }
}

@Composable
private fun LegendDot(color:Color,label:String){
    Row(verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(3.dp))
        Text(label,fontSize=8.sp)
    }
}

@Composable
private fun ReportLine(label:String,value:String,color:Color){
    Row(Modifier.fillMaxWidth().padding(vertical=5.dp),verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(7.dp))
        Text(label,Modifier.weight(1f),fontWeight=FontWeight.Bold,fontSize=12.sp)
        Text(value,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ReminderScreen(context:Context,onBack:()->Unit){
    var message by remember { mutableStateOf("یک یادآور را فعال کن؛ بعداً زمان‌های سفارشی هم اضافه می‌شوند.") }
    Page("یادآورها",onBack){
        SectionTitle("اعلان‌های هوشمند")
        listOf(
            Triple("شروع صبح",8 to 0,"برنامه امروز را مرور کن"),
            Triple("بدن و غذا",13 to 0,"غذا، آب و وضعیت بدنت را ثبت کن"),
            Triple("مرور مالی",20 to 30,"مخارج و سرمایه امروز را ثبت کن"),
            Triple("مرور شب",22 to 30,"شکرگزاری، رشد و برنامه فردا")
        ).forEachIndexed{i,entry->
            GlassCard(Modifier.padding(vertical=5.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Column(Modifier.weight(1f)){
                        Text(entry.first,fontWeight=FontWeight.Bold)
                        Text(
                            String.format("%02d:%02d",entry.second.first,entry.second.second)+" • "+entry.third,
                            fontSize=11.sp,
                            color=MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick={
                            ReminderReceiver.scheduleDaily(context,200+i,entry.second.first,entry.second.second,entry.first,entry.third)
                            message="یادآور «"+entry.first+"» فعال شد."
                        },
                        colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink)
                    ){Text("فعال")}
                }
            }
        }
        Text(message,color=Success,modifier=Modifier.padding(top=12.dp))
    }
}
