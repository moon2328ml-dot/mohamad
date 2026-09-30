package ir.mohammad.lifeos

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun AccountScreen(store:LifeStore,onDone:()->Unit,onBack:(()->Unit)?){
 var name by remember{mutableStateOf(store.name)}
 var email by remember{mutableStateOf(store.email)}
 var target by remember{mutableStateOf(store.targetWeight.toString())}
 Page("حساب شخصی",onBack){
  Spacer(Modifier.height(24.dp))
  GoldCard{
   Row(verticalAlignment=Alignment.CenterVertically){
    Box(Modifier.size(76.dp).background(GoldSoft,CircleShape),contentAlignment=Alignment.Center){MysticEye(Modifier.size(62.dp))}
    Spacer(Modifier.width(14.dp))
    Column{Text("متد محمد",fontSize=24.sp,fontWeight=FontWeight.ExtraBold);Text("سیستم شخصی مدیریت زندگی",color=Muted)}
   }
  }
  SectionTitle("پروفایل من","اطلاعات روی دستگاه ذخیره می‌شود و Android Backup هم فعال است.")
  OutlinedTextField(name,{name=it},label={Text("نام")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  Spacer(Modifier.height(8.dp))
  OutlinedTextField(email,{email=it},label={Text("ایمیل (اختیاری)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  Spacer(Modifier.height(8.dp))
  OutlinedTextField(target,{target=it},label={Text("وزن هدف")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  Spacer(Modifier.height(18.dp))
  PrimaryButton("ذخیره حساب"){
   if(name.isNotBlank()){
    store.name=name.trim();store.email=email.trim();store.targetWeight=target.toDoubleOrNull()?:store.targetWeight;onDone()
   }
  }
 }
}

data class Dash(val icon:String,val title:String,val sub:String,val screen:Screen)

@Composable
fun HomeScreen(store:LifeStore,version:Int,onOpen:(Screen)->Unit){
 val routine=store.items("routine")
 val done=routine.count{it.done}
 val expenses=store.loadMoney().filter{it.kind=="expense"}.sumOf{it.amount}
 val latestWeight=store.loadWeights().lastOrNull()?.weight
 val cards=listOf(
  Dash("☀","روتین روزانه","کارهای طول روز",Screen.ROUTINE),
  Dash("✓","اهداف روزانه","سه پیروزی امروز",Screen.DAILY_GOALS),
  Dash("◈","مدیریت مالی","درآمد، مخارج، پس‌انداز",Screen.FINANCE),
  Dash("⌁","قسط و بدهی","مسیر آزادی مالی",Screen.DEBTS),
  Dash("♜","سرمایه‌گذاری","طلا، ارز، دارایی",Screen.INVESTMENT),
  Dash("♥","سلامت و بدن","وزن، غذا، باشگاه",Screen.BODY),
  Dash("★","اهداف سالانه","نقشه امسال",Screen.YEARLY_GOALS),
  Dash("∞","اهداف بلندمدت","زندگی‌ای که می‌سازم",Screen.LONG_GOALS),
  Dash("✦","شکرگزاری","دفتر نعمت‌ها",Screen.GRATITUDE),
  Dash("↗","رشد فردی","درس‌های من",Screen.GROWTH),
  Dash("♡","رابطه من","رفتار، دیدار، هزینه",Screen.RELATIONSHIP),
  Dash("◉","قانون جذب","آکادمی و تمرین",Screen.ATTRACTION),
  Dash("◆","قدرت روانی","مرزبندی و رهبری",Screen.POWER),
  Dash("☾","من و خدا","عبادت و معنویت",Screen.SPIRITUAL),
  Dash("⌕","آنالیز من","اسکن الگوها",Screen.ANALYSIS),
  Dash("▦","گزارش‌ها","روزانه تا سالانه",Screen.REPORTS),
  Dash("✎","خاطرات","تایم‌لاین زندگی",Screen.MEMORIES),
  Dash("⚒","آزمایشگاه رشد","اشتباه → اصلاح",Screen.MISTAKES),
  Dash("⏰","یادآورها","اعلان‌های روزانه",Screen.REMINDERS),
  Dash("⚙","حساب","تنظیمات پروفایل",Screen.ACCOUNT)
 )
 Column(Modifier.fillMaxSize().background(AppBg).statusBarsPadding()){
  LazyVerticalGrid(columns=GridCells.Fixed(2),contentPadding=PaddingValues(12.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
   item(span={androidx.compose.foundation.lazy.grid.GridItemSpan(2)}){
    GoldCard{
     Row(verticalAlignment=Alignment.CenterVertically){
      Box(Modifier.size(92.dp).background(Ink,RoundedCornerShape(24.dp)),contentAlignment=Alignment.Center){MysticEye(Modifier.size(76.dp))}
      Spacer(Modifier.width(14.dp))
      Column(Modifier.weight(1f)){
       Text("سلام "+store.name,fontWeight=FontWeight.ExtraBold,fontSize=23.sp)
       Text(LocalDate.now().toString(),color=Muted,fontSize=12.sp)
       Spacer(Modifier.height(6.dp))
       Text("کنترل زندگی در دستان توست",fontWeight=FontWeight.Bold,color=Gold)
      }
     }
    }
   }
   item(span={androidx.compose.foundation.lazy.grid.GridItemSpan(2)}){
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
     StatPill("روتین",if(routine.isEmpty())"—" else ((done*100/routine.size).toString()+"%"),Modifier.weight(1f))
     StatPill("خرج ثبت‌شده",money(expenses),Modifier.weight(1f))
     StatPill("وزن",latestWeight?.let{it.toString()+" kg"}?:"ثبت نشده",Modifier.weight(1f))
    }
   }
   gridItems(cards){c->
    Card(Modifier.fillMaxWidth().height(142.dp).clickable{onOpen(c.screen)},shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(2.dp)){
     Column(Modifier.fillMaxSize().padding(14.dp),verticalArrangement=Arrangement.SpaceBetween){
      Box(Modifier.size(42.dp).background(Ink,RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Text(c.icon,color=Gold,fontSize=22.sp,fontWeight=FontWeight.Bold)}
      Column{Text(c.title,fontWeight=FontWeight.ExtraBold,fontSize=15.sp);Text(c.sub,color=Muted,fontSize=11.sp,maxLines=2)}
     }
    }
   }
   item(span={androidx.compose.foundation.lazy.grid.GridItemSpan(2)}){Spacer(Modifier.height(20.dp))}
  }
 }
}

@Composable
fun ChecklistScreen(store:LifeStore,title:String,group:String,onBack:()->Unit,onChange:()->Unit){
 var list by remember{mutableStateOf(store.items(group))}
 var text by remember{mutableStateOf("")}
 Page(title,onBack){
  SectionTitle("لیست من","هر مورد را تیک بزن؛ اطلاعات بعد از بستن برنامه می‌ماند.")
  val done=list.count{it.done}
  LinearProgressIndicator(progress={if(list.isEmpty())0f else done.toFloat()/list.size},modifier=Modifier.fillMaxWidth().height(9.dp),color=Gold,trackColor=GoldSoft)
  Text(if(list.isEmpty())"هنوز موردی ثبت نشده" else (done.toString()+" از "+list.size+" انجام شده"),color=Muted,fontSize=12.sp,modifier=Modifier.padding(vertical=6.dp))
  Row(verticalAlignment=Alignment.CenterVertically){
   OutlinedTextField(text,{text=it},label={Text("مورد جدید")},modifier=Modifier.weight(1f),singleLine=true)
   Spacer(Modifier.width(8.dp))
   Button(onClick={if(text.isNotBlank()){store.addItem(SimpleItem(title=text.trim(),group=group));text="";list=store.items(group);onChange()}},colors=ButtonDefaults.buttonColors(containerColor=Ink,contentColor=Gold)){Text("+",fontSize=24.sp)}
  }
  Spacer(Modifier.height(8.dp))
  LazyColumn(verticalArrangement=Arrangement.spacedBy(7.dp),contentPadding=PaddingValues(bottom=20.dp)){
   items(list,key={it.id}){x->
    Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(15.dp)){
     Row(Modifier.fillMaxWidth().padding(10.dp),verticalAlignment=Alignment.CenterVertically){
      Checkbox(x.done,{store.toggleItem(x.id);list=store.items(group);onChange()},colors=CheckboxDefaults.colors(checkedColor=Gold,checkmarkColor=Ink))
      Text(x.title,Modifier.weight(1f),fontWeight=if(x.done)FontWeight.Normal else FontWeight.SemiBold)
      TextButton(onClick={store.deleteItem(x.id);list=store.items(group);onChange()}){Text("حذف",color=Danger,fontSize=11.sp)}
     }
    }
   }
  }
 }
}

@Composable
fun JournalScreen(store:LifeStore,title:String,kind:String,prompt:String,onBack:()->Unit,onChange:()->Unit){
 var text by remember{mutableStateOf("")}
 var list by remember{mutableStateOf(store.journals(kind))}
 Page(title,onBack){
  SectionTitle(prompt,"کوتاه هم بنویسی کافی است؛ مهم تداوم و صداقت با خودته.")
  OutlinedTextField(text,{text=it},modifier=Modifier.fillMaxWidth().height(140.dp),placeholder={Text("اینجا بنویس…")})
  Spacer(Modifier.height(8.dp))
  PrimaryButton("ثبت"){if(text.isNotBlank()){store.addJournal(JournalEntry(kind=kind,text=text.trim()));text="";list=store.journals(kind);onChange()}}
  SectionTitle("تاریخچه")
  LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(bottom=20.dp)){
   items(list,key={it.id}){x->
    GoldCard{
     Text(x.date,color=Gold,fontSize=11.sp,fontWeight=FontWeight.Bold)
     Spacer(Modifier.height(5.dp));Text(x.text)
     TextButton(onClick={store.deleteJournal(x.id);list=store.journals(kind);onChange()},modifier=Modifier.align(Alignment.End)){Text("حذف",color=Danger)}
    }
   }
  }
 }
}
