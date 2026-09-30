package ir.mohammad.lifeos

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttractionScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 val teachers=listOf(
  "ناپلئون هیل" to "هدف دقیق، ایمان، تلقین، تصمیم، پشتکار، Mastermind",
  "کوین ترودو" to "Teachability Index، ورودی‌های ذهنی و تمرکز روی خواسته",
  "نویل گادرد" to "فرض، تصویر نتیجه نهایی، SATS و Revision",
  "ابراهام هیکس" to "راهنمای هیجانی و توجه به حالت درونی",
  "روندا برن" to "خواستن، باور، دریافت، شکرگزاری و تجسم",
  "باب پراکتور" to "پارادایم، Goal Card و تغییر الگوهای ذهنی",
  "لوئیز هی" to "گفت‌وگوی درونی، تأییدها و Mirror Work",
  "وین دایر" to "نیت، معنا و همسویی با تصویری که می‌سازی",
  "جو دیسپنزا" to "مدیتیشن، تجسم و تمرین هویت آینده",
  "عباس منش" to "باورهای مالی، فراوانی و رزق",
  "محمد بصیری" to "باور مالی، هدف، چرایی و تصویرسازی",
  "امیر همتی" to "لیاقت، ترس، گفت‌وگوی درونی و ثروت"
 )
 var text by remember{mutableStateOf("")}
 var list by remember{mutableStateOf(store.journals("manifestation"))}
 Page("آکادمی قانون جذب",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{GoldCard{Text("مسیر چندساله باور → تمرین → اقدام → نتیجه",fontWeight=FontWeight.ExtraBold);Text("این بخش آموزه‌های مکاتب مورد علاقه‌ات را جدا نگه می‌دارد تا بعداً «متد محمد» از تجربه‌های خودت ساخته شود.",color=Muted)}}
   item{SectionTitle("کتابخانه استادها","هر مکتب ابزارهای خودش را دارد؛ قرار نیست همه را یکی فرض کنیم.")}
   items(teachers){(n,d)->Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(16.dp)){Column(Modifier.fillMaxWidth().padding(12.dp)){Text(n,fontWeight=FontWeight.ExtraBold);Text(d,color=Muted,fontSize=12.sp)}}}
   item{
    SectionTitle("تمرین امروز","خواسته، باور، تصویر، اقدام و شواهد را بنویس.")
    OutlinedTextField(text,{text=it},modifier=Modifier.fillMaxWidth().height(140.dp),placeholder={Text("مثلاً: خواسته من چیست؟ مانع ذهنی من چیست؟ امروز چه اقدام همسویی انجام می‌دهم؟")})
    Spacer(Modifier.height(7.dp));PrimaryButton("ثبت در دفتر جذب"){if(text.isNotBlank()){store.addJournal(JournalEntry(kind="manifestation",text=text));text="";list=store.journals("manifestation");onChange()}}
   }
   items(list.take(20),key={it.id}){x->GoldCard{Text(x.date,color=Gold,fontSize=11.sp);Text(x.text)}}
  }
 }
}

@Composable
fun PowerScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 val modules=listOf(
  "رادار دستکاری" to "شناخت فشار، گناه‌دادن، تناقض، تحقیر و مرزشکنی بدون ذهن‌خوانی.",
  "قاطعیت" to "نه گفتن، بیان مخالفت و درخواست حق خود بدون پرخاش.",
  "کنترل خشم" to "محرک → فکر → شدت → مکث → پاسخ انتخابی.",
  "زبان بدن" to "ایستادن باز، نگاه طبیعی، صدای شمرده و عجله نکردن.",
  "مذاکره" to "سؤال دقیق، شنیدن، پیشنهاد روشن و حفظ مرز.",
  "رهبری" to "مسئولیت، ثبات، پایبندی به قول و آرامش زیر فشار.",
  "دفاع در برابر فریب" to "تناقض روایت و شواهد مهم‌تر از حدس از روی ژست‌هاست.",
  "قدرت اجتماعی" to "حضور، ارتباط و اعتبار؛ نه ترساندن یا وابسته‌کردن دیگران."
 )
 var reflection by remember{mutableStateOf("")}
 Page("قدرت روانی و رهبری",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{GoldCard{Text("قوی باش، قابل‌بازی نباش",fontWeight=FontWeight.ExtraBold,fontSize=18.sp);Text("قدرت اینجا یعنی کنترل خود، مرزبندی، استراتژی و تشخیص بازی روانی؛ نه تحقیر و کنترل آدم‌ها.",color=Muted)}}
   items(modules){(n,d)->Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(16.dp)){Column(Modifier.fillMaxWidth().padding(13.dp)){Text(n,fontWeight=FontWeight.ExtraBold);Text(d,color=Muted,fontSize=12.sp)}}}
   item{
    SectionTitle("Red Team من","یک موقعیت سخت امروز را بازسازی کن.")
    OutlinedTextField(reflection,{reflection=it},modifier=Modifier.fillMaxWidth().height(130.dp),placeholder={Text("چه شد؟ چه چیزی تحریکم کرد؟ پاسخ قدرتمند و کنترل‌شده چه می‌توانست باشد؟")})
    Spacer(Modifier.height(7.dp));PrimaryButton("ثبت تمرین"){if(reflection.isNotBlank()){store.addJournal(JournalEntry(kind="power",text=reflection));reflection="";onChange()}}
   }
  }
 }
}

@Composable
fun SpiritualScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var acts by remember{mutableStateOf(store.items("spiritual"))}
 LaunchedEffect(Unit){if(acts.isEmpty()){listOf("نماز/عبادت انتخابی","دعا یا ذکر","قرآن/مطالعه معنوی","شکرگزاری","کار خیر","اتصال/تمرین معنوی شخصی").forEach{store.addItem(SimpleItem(title=it,group="spiritual"))};acts=store.items("spiritual")}}
 var note by remember{mutableStateOf("")}
 Page("من و خدا",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{GoldCard{Text("مسیر معنوی من",fontWeight=FontWeight.ExtraBold);Text("اینجا نمره ایمان نمی‌گیری؛ فقط تداوم اعمال و تجربه‌های خودت را می‌بینی.",color=Muted)}}
   item{SectionTitle("اعمال امروز")}
   items(acts,key={it.id}){x->Row(Modifier.fillMaxWidth(),verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){Checkbox(x.done,{store.toggleItem(x.id);acts=store.items("spiritual");onChange()},colors=CheckboxDefaults.colors(checkedColor=Gold));Text(x.title,Modifier.weight(1f))}}
   item{
    SectionTitle("مسیر عرفان حلقه / فرادرمانی شخصی","برای یادگیری، تجربه معنوی و ثبت دریافت‌های شخصی؛ جایگزین مراقبت پزشکی ضروری نیست.")
    listOf("مفاهیم پایه و واژه‌ها","شاهد و تسلیم","تمرین/اتصال شخصی","دفتر تجربه‌ها","مرور مرحله‌به‌مرحله").forEach{Text("• "+it,modifier=Modifier.padding(vertical=3.dp))}
    OutlinedTextField(note,{note=it},modifier=Modifier.fillMaxWidth().height(120.dp),placeholder={Text("تجربه معنوی یا دریافت امروز…")})
    Spacer(Modifier.height(7.dp));PrimaryButton("ثبت تجربه"){if(note.isNotBlank()){store.addJournal(JournalEntry(kind="spiritual_note",text=note));note="";onChange()}}
   }
  }
 }
}

@Composable
fun AnalysisScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var mood by remember{mutableFloatStateOf(7f)};var discipline by remember{mutableFloatStateOf(6f)};var confidence by remember{mutableFloatStateOf(6f)}
 var anger by remember{mutableFloatStateOf(7f)};var loneliness by remember{mutableFloatStateOf(4f)};var focus by remember{mutableFloatStateOf(6f)}
 var checkins by remember{mutableStateOf(store.loadCheckIns())}
 fun avg(f:(CheckIn)->Int):Double=if(checkins.isEmpty())0.0 else checkins.map(f).average()
 Page("آنالیز من",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{
    GoldCard{Text("مرکز اسکن رفتاری",fontWeight=FontWeight.ExtraBold,fontSize=18.sp);Text("یک اتفاق، برچسب شخصیت نیست. برنامه روندهای تکرارشونده را نشان می‌دهد.",color=Muted)}
   }
   item{
    SectionTitle("چک‌این امروز","۱ یعنی ضعیف/کم؛ ۱۰ یعنی عالی/زیاد. در «تنهایی» عدد بالاتر یعنی احساس تنهایی بیشتر.")
    ScoreSlider("حال کلی",mood){mood=it};ScoreSlider("انضباط",discipline){discipline=it};ScoreSlider("اعتمادبه‌نفس",confidence){confidence=it};ScoreSlider("کنترل خشم",anger){anger=it};ScoreSlider("احساس تنهایی",loneliness){loneliness=it};ScoreSlider("تمرکز",focus){focus=it}
    PrimaryButton("ثبت اسکن امروز"){store.addCheckIn(CheckIn(mood=mood.toInt(),discipline=discipline.toInt(),confidence=confidence.toInt(),angerControl=anger.toInt(),loneliness=loneliness.toInt(),focus=focus.toInt()));checkins=store.loadCheckIns();onChange()}
   }
   item{
    SectionTitle("آینه من","میانگین ثبت‌های تو، نه تشخیص پزشکی")
    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){StatPill("انضباط",String.format("%.1f",avg{it.discipline}),Modifier.weight(1f));StatPill("اعتمادبه‌نفس",String.format("%.1f",avg{it.confidence}),Modifier.weight(1f));StatPill("کنترل خشم",String.format("%.1f",avg{it.angerControl}),Modifier.weight(1f))}
    Spacer(Modifier.height(7.dp));Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){StatPill("تمرکز",String.format("%.1f",avg{it.focus}),Modifier.weight(1f));StatPill("حال",String.format("%.1f",avg{it.mood}),Modifier.weight(1f));StatPill("تنهایی",String.format("%.1f",avg{it.loneliness}),Modifier.weight(1f))}
   }
   item{
    val routine=store.items("routine");val adherence=if(routine.isEmpty())0 else routine.count{it.done}*100/routine.size
    val expenses=store.loadMoney().filter{it.kind=="expense"}.sumOf{it.amount};val investments=store.loadMoney().filter{it.kind=="investment"}.sumOf{it.amount}
    SectionTitle("داده‌های رفتاری متصل")
    GoldCard{Text("پایبندی روتین: "+adherence+"٪");Text("مخارج ثبت‌شده: "+money(expenses));Text("سرمایه‌گذاری ثبت‌شده: "+money(investments));Text("تعداد یادداشت رشد: "+store.journals("growth").size);Text("تعداد ثبت رابطه: "+store.loadRelationship().size)}
   }
  }
 }
}

@Composable
fun ScoreSlider(label:String,value:Float,onChange:(Float)->Unit){
 Row{Text(label,Modifier.weight(1f),fontWeight=FontWeight.Bold);Text(value.toInt().toString()+"/10",color=Gold,fontWeight=FontWeight.Bold)}
 Slider(value,onValueChange,valueRange=1f..10f,steps=8,colors=SliderDefaults.colors(thumbColor=Gold,activeTrackColor=Gold))
}

@Composable
fun ReportsScreen(store:LifeStore,onBack:()->Unit){
 Page("گزارش زندگی",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{SectionTitle("خلاصه فعلی","گزارش‌ها با هر ثبت جدید به‌روز می‌شوند.")}
   item{GoldCard{Text(store.exportSummary(),lineHeight=25.sp)}}
   item{
    val m=store.loadMoney();val inc=m.filter{it.kind=="income"}.sumOf{it.amount}+store.monthlyIncome;val exp=m.filter{it.kind=="expense"}.sumOf{it.amount};val inv=m.filter{it.kind=="investment"}.sumOf{it.amount}
    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){StatPill("درآمد",money(inc),Modifier.weight(1f));StatPill("خرج",money(exp),Modifier.weight(1f));StatPill("سرمایه",money(inv),Modifier.weight(1f))}
   }
   item{
    SectionTitle("روند وزن")
    MiniLineChart(store.loadWeights().takeLast(20).map{it.weight.toFloat()})
   }
   item{
    SectionTitle("گزارش شخصیت")
    val c=store.loadCheckIns()
    if(c.isEmpty())EmptyHint("هنوز چک‌این شخصیت ثبت نشده.") else GoldCard{Text("تعداد اسکن‌ها: "+c.size);Text("بهبود را با مقایسه روند چند هفته‌ای بررسی کن، نه یک روز.")}
   }
  }
 }
}

@Composable
fun ReminderScreen(context:Context,onBack:()->Unit){
 var message by remember{mutableStateOf("هنوز یادآوری تازه‌ای تنظیم نشده.")}
 Page("یادآورها",onBack){
  SectionTitle("اعلان‌های روزانه","یک لمس کافی است؛ زمان‌های پیش‌فرض بعداً قابل تغییر خواهند بود.")
  listOf(
   Triple("شروع صبح",8 to 0,"سه کار اصلی و روتین صبح"),
   Triple("بدن و غذا",13 to 0,"غذا، آب و وضعیت بدن را ثبت کن"),
   Triple("مرور مالی",20 to 30,"مخارج و سرمایه امروز را ثبت کن"),
   Triple("مرور شب",22 to 30,"شکرگزاری، رشد و برنامه فردا")
  ).forEachIndexed{i,(title,time,text)->
   GoldCard(Modifier.padding(vertical=5.dp)){
    Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold);Text(String.format("%02d:%02d",time.first,time.second)+" • "+text,color=Muted,fontSize=12.sp)};Button(onClick={ReminderReceiver.scheduleDaily(context,200+i,time.first,time.second,title,text);message="یادآوری «"+title+"» فعال شد."},colors=ButtonDefaults.buttonColors(containerColor=Ink,contentColor=Gold)){Text("فعال")}}
   }
  }
  Text(message,color=Success,modifier=Modifier.padding(top=12.dp))
 }
}
