package ir.mohammad.lifeos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun FinanceScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var incomeText by remember{mutableStateOf(if(store.monthlyIncome==0L)"" else store.monthlyIncome.toString())}
 var amount by remember{mutableStateOf("")}
 var note by remember{mutableStateOf("")}
 var kind by remember{mutableStateOf("expense")}
 var category by remember{mutableStateOf("ضروری")}
 var records by remember{mutableStateOf(store.loadMoney().reversed())}
 val income=incomeText.toLongOrNull()?:store.monthlyIncome
 val alloc=listOf("ضروریات" to 35,"اقساط/تعهدات" to 15,"سرمایه‌گذاری" to 15,"پس‌انداز اضطراری" to 10,"رابطه/دیت/رفت‌وآمد" to 8,"باشگاه و رشد" to 7,"تفریح شخصی" to 5,"ذخیره آزاد" to 5)
 Page("مدیریت درآمد",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{FeatureHero(Screen.FINANCE,"موفقیت مالی","درآمد • سرمایه‌گذاری • مدیریت خرج • هوش مالی")}
   item{
    SectionTitle("درآمد این ماه","عدد را وارد کن؛ اپ سهم هر بخش را برایت حساب می‌کند.")
    Row(verticalAlignment=Alignment.CenterVertically){
     OutlinedTextField(incomeText,{incomeText=it.filter(Char::isDigit)},label={Text("درآمد خالص ماه (تومان)")},modifier=Modifier.weight(1f),singleLine=true)
     Spacer(Modifier.width(8.dp))
     Button(onClick={store.monthlyIncome=income;onChange()},colors=ButtonDefaults.buttonColors(containerColor=Ink,contentColor=Gold)){Text("ثبت")}
    }
   }
   item{
    GoldCard{
     Text("تقسیم پیشنهادی ۱۰۰٪",fontWeight=FontWeight.ExtraBold,fontSize=17.sp)
     Spacer(Modifier.height(8.dp))
     alloc.forEach{(n,p)->
      Row(Modifier.fillMaxWidth().padding(vertical=4.dp)){
       Text(n,Modifier.weight(1f));Text(p.toString()+"٪  •  "+money(income*p/100),fontWeight=FontWeight.Bold)
      }
     }
     HorizontalDivider(Modifier.padding(vertical=8.dp),color=Gold.copy(alpha=.4f))
     Text("بودجه رابطه این ماه: "+money(income*8/100),color=Gold,fontWeight=FontWeight.Bold)
    }
   }
   item{
    SectionTitle("ثبت گردش پول","خرج، درآمد اضافه یا سرمایه‌گذاری")
    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
     listOf("expense" to "خرج","income" to "درآمد","investment" to "سرمایه").forEach{(k,l)->
      FilterChip(selected=kind==k,onClick={kind=k},label={Text(l)})
     }
    }
    OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},label={Text("مبلغ")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(category,{category=it},label={Text("دسته")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(note,{note=it},label={Text("توضیح")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(8.dp))
    PrimaryButton("ثبت گردش"){val a=amount.toLongOrNull()?:0;if(a>0){store.addMoney(MoneyEntry(kind=kind,category=category,amount=a,note=note));amount="";note="";records=store.loadMoney().reversed();onChange()}}
   }
   item{
    val spent=store.loadMoney().filter{it.kind=="expense"}.sumOf{it.amount}
    val inv=store.loadMoney().filter{it.kind=="investment"}.sumOf{it.amount}
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
     StatPill("خرج ثبت‌شده",money(spent),Modifier.weight(1f))
     StatPill("سرمایه‌گذاری",money(inv),Modifier.weight(1f))
    }
   }
   items(records.take(40),key={it.id}){x->
    Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(16.dp)){
     Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){
      Column(Modifier.weight(1f)){Text(x.category,fontWeight=FontWeight.Bold);Text(x.date+" • "+x.note,color=Muted,fontSize=11.sp)}
      Text((if(x.kind=="expense")"-" else "+")+money(x.amount),color=if(x.kind=="expense")Danger else Success,fontWeight=FontWeight.Bold)
      TextButton(onClick={store.deleteMoney(x.id);records=store.loadMoney().reversed();onChange()}){Text("×",color=Muted)}
     }
    }
   }
  }
 }
}

@Composable
fun DebtScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var list by remember{mutableStateOf(store.loadDebts())}
 var title by remember{mutableStateOf("")};var owner by remember{mutableStateOf("")};var type by remember{mutableStateOf("بانک")}
 var total by remember{mutableStateOf("")};var paid by remember{mutableStateOf("")};var monthly by remember{mutableStateOf("")};var due by remember{mutableStateOf("")}
 Page("مسیر آزادی مالی",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=25.dp)){
   item{FeatureHero(Screen.DEBTS,"مسیر آزادی مالی","اقساط • تعهدات • پیشرفت پرداخت • آرامش مالی")}
   item{
    val remain=list.sumOf{(it.total-it.paid).coerceAtLeast(0)}
    GoldCard{Text("تمرکز روی پیشرفت، نه اضطراب",fontWeight=FontWeight.ExtraBold);Text("مانده تعهدات ثبت‌شده: "+money(remain),color=Gold,fontWeight=FontWeight.Bold)}
   }
   item{
    SectionTitle("پرونده جدید","وام بانکی، قرض شخصی، خرید اقساطی یا هر تعهد دیگر")
    OutlinedTextField(title,{title=it},label={Text("عنوان")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp));OutlinedTextField(owner,{owner=it},label={Text("مربوط به چه شخص/بانک/موضوعی؟")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp))
    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("بانک","شخص","خرید","سایر").forEach{FilterChip(type==it,{type=it},{Text(it)})}}
    OutlinedTextField(total,{total=it.filter(Char::isDigit)},label={Text("کل مبلغ")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp));OutlinedTextField(paid,{paid=it.filter(Char::isDigit)},label={Text("پرداخت‌شده")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp));OutlinedTextField(monthly,{monthly=it.filter(Char::isDigit)},label={Text("قسط ماهانه")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(6.dp));OutlinedTextField(due,{due=it.filter(Char::isDigit)},label={Text("روز سررسید ماه")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(8.dp))
    PrimaryButton("ساخت پرونده"){val t=total.toLongOrNull()?:0;if(title.isNotBlank()&&t>0){store.addDebt(DebtEntry(title=title,owner=owner,type=type,total=t,paid=paid.toLongOrNull()?:0,monthly=monthly.toLongOrNull()?:0,dueDay=due.toIntOrNull()?:1));title="";owner="";total="";paid="";monthly="";due="";list=store.loadDebts();onChange()}}
   }
   items(list,key={it.id}){x->
    GoldCard{
     Row{Column(Modifier.weight(1f)){Text(x.title,fontWeight=FontWeight.ExtraBold);Text(x.type+" • "+x.owner,color=Muted)};TextButton(onClick={store.deleteDebt(x.id);list=store.loadDebts();onChange()}){Text("حذف",color=Danger)}}
     val progress=if(x.total<=0)0f else (x.paid.toFloat()/x.total).coerceIn(0f,1f)
     LinearProgressIndicator(progress={progress},modifier=Modifier.fillMaxWidth().height(8.dp),color=Success,trackColor=GoldSoft)
     Spacer(Modifier.height(5.dp))
     Text("آزاد شده: "+money(x.paid)+"  •  باقی: "+money((x.total-x.paid).coerceAtLeast(0)))
     Text("قسط ماهانه: "+money(x.monthly)+"  |  سررسید روز "+x.dueDay,color=Muted,fontSize=12.sp)
    }
   }
  }
 }
}

@Composable
fun BodyScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var weights by remember{mutableStateOf(store.loadWeights())}
 var w by remember{mutableStateOf("")};var target by remember{mutableStateOf(store.targetWeight.toString())}
 var foods by remember{mutableStateOf(store.loadFoods().reversed())};var food by remember{mutableStateOf("")};var cal by remember{mutableStateOf("")};var protein by remember{mutableStateOf("")}
 var workouts by remember{mutableStateOf(store.items("workout"))}
 var workoutText by remember{mutableStateOf("")}
 Page("سلامت، غذا و باشگاه",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=30.dp)){
   item{FeatureHero(Screen.BODY,"سلامت و بدن","تغذیه • برنامه غذایی • خواب • انرژی • باشگاه")}
   item{
    SectionTitle("کنترل وزن","عدد روزانه را ثبت کن؛ روند مهم‌تر از بالا و پایین یک روز است.")
    Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){
     OutlinedTextField(w,{w=it},label={Text("وزن امروز")},modifier=Modifier.weight(1f),singleLine=true)
     OutlinedTextField(target,{target=it},label={Text("وزن هدف")},modifier=Modifier.weight(1f),singleLine=true)
    }
    Spacer(Modifier.height(7.dp))
    PrimaryButton("ثبت وزن"){val ww=w.toDoubleOrNull();if(ww!=null){store.addWeight(ww);store.targetWeight=target.toDoubleOrNull()?:store.targetWeight;w="";weights=store.loadWeights();onChange()}}
   }
   item{
    if(weights.isNotEmpty()){
     val last=weights.last().weight;val avg=weights.takeLast(7).map{it.weight}.average()
     Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){StatPill("وزن فعلی",last.toString()+" kg",Modifier.weight(1f));StatPill("میانگین ۷ ثبت",String.format("%.1f kg",avg),Modifier.weight(1f));StatPill("هدف",store.targetWeight.toString()+" kg",Modifier.weight(1f))}
     MiniLineChart(weights.takeLast(14).map{it.weight.toFloat()},Modifier.padding(top=10.dp))
    }
   }
   item{
    SectionTitle("غذاهای امروز","ثبت ساده: نام، کالری تقریبی و پروتئین")
    OutlinedTextField(food,{food=it},label={Text("غذا/خوراکی")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(5.dp));Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){OutlinedTextField(cal,{cal=it.filter(Char::isDigit)},label={Text("کالری")},modifier=Modifier.weight(1f),singleLine=true);OutlinedTextField(protein,{protein=it.filter(Char::isDigit)},label={Text("پروتئین g")},modifier=Modifier.weight(1f),singleLine=true)}
    Spacer(Modifier.height(6.dp));PrimaryButton("ثبت غذا"){if(food.isNotBlank()){store.addFood(FoodEntry(name=food,calories=cal.toIntOrNull()?:0,protein=protein.toIntOrNull()?:0));food="";cal="";protein="";foods=store.loadFoods().reversed();onChange()}}
    val today=LocalDate.now().toString();val ft=foods.filter{it.date==today}
    Text("امروز: "+ft.sumOf{it.calories}+" kcal  •  "+ft.sumOf{it.protein}+" g پروتئین",fontWeight=FontWeight.Bold,color=Gold,modifier=Modifier.padding(top=8.dp))
   }
   items(foods.take(10),key={it.id}){x->GlassCard{Row(Modifier.fillMaxWidth()){Text(x.name,Modifier.weight(1f));Text(x.calories.toString()+" kcal • "+x.protein+"g",fontWeight=FontWeight.Bold);TextButton(onClick={store.deleteFood(x.id);foods=store.loadFoods().reversed();onChange()}){Text("×")}}}}
   item{
    SectionTitle("برنامه باشگاه","حرکت‌ها را خودت اضافه کن؛ هیچ برنامه‌ای از قبل به تو تحمیل نمی‌شود.")
    GlassCard{
     Row(verticalAlignment=Alignment.CenterVertically){
      OutlinedTextField(workoutText,{workoutText=it},label={Text("حرکت یا برنامه جدید")},modifier=Modifier.weight(1f),singleLine=true)
      Spacer(Modifier.width(7.dp))
      Button(onClick={if(workoutText.isNotBlank()){store.addItem(SimpleItem(title=workoutText.trim(),group="workout"));workoutText="";workouts=store.items("workout");onChange()}},colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink)){Text("+")}
     }
     if(workouts.isEmpty()) Text("هنوز حرکت یا برنامه‌ای نساختی.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.padding(top=8.dp))
    }
   }
   items(workouts,key={it.id}){x->Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Checkbox(x.done,{store.toggleItem(x.id);workouts=store.items("workout");onChange()},colors=CheckboxDefaults.colors(checkedColor=Gold));Text(x.title,Modifier.weight(1f))}}
  }
 }
}

@Composable
fun InvestmentScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var amount by remember{mutableStateOf("")};var asset by remember{mutableStateOf("طلا")}
 var goal by remember{mutableStateOf("")};var goals by remember{mutableStateOf(store.items("investment_goal"))}
 Page("عادت سرمایه‌گذاری",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=25.dp)){
   item{FeatureHero(Screen.INVESTMENT,"سرمایه‌گذاری من","طلا • ارز • دارایی • عادت سرمایه‌گذاری")}
   item{GoldCard{Text("قاعده شخصی",fontWeight=FontWeight.ExtraBold);Text("هر ماه اول سهم سرمایه را کنار بگذار؛ بعد سراغ خرج آزاد برو.",color=Muted)}}
   item{
    SectionTitle("ثبت سرمایه‌گذاری")
    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("طلا","دلار","کریپتو","کسب‌وکار","سایر").forEach{FilterChip(asset==it,{asset=it},{Text(it)})}}
    OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},label={Text("مبلغ")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Spacer(Modifier.height(7.dp));PrimaryButton("ثبت"){val a=amount.toLongOrNull()?:0;if(a>0){store.addMoney(MoneyEntry(kind="investment",category=asset,amount=a));amount="";onChange()}}
   }
   item{
    val total=store.loadMoney().filter{it.kind=="investment"}.sumOf{it.amount};StatPill("کل سرمایه‌گذاری ثبت‌شده",money(total),Modifier.fillMaxWidth())
    SectionTitle("اهداف دارایی")
    Row{OutlinedTextField(goal,{goal=it},label={Text("مثلاً ۱۰ گرم طلا")},modifier=Modifier.weight(1f),singleLine=true);Spacer(Modifier.width(6.dp));Button(onClick={if(goal.isNotBlank()){store.addItem(SimpleItem(title=goal,group="investment_goal"));goal="";goals=store.items("investment_goal");onChange()}},colors=ButtonDefaults.buttonColors(containerColor=Ink,contentColor=Gold)){Text("+")}}
   }
   items(goals,key={it.id}){x->Row(verticalAlignment=Alignment.CenterVertically){Checkbox(x.done,{store.toggleItem(x.id);goals=store.items("investment_goal");onChange()});Text(x.title,Modifier.weight(1f));TextButton(onClick={store.deleteItem(x.id);goals=store.items("investment_goal");onChange()}){Text("حذف")}}}
  }
 }
}

@Composable
fun RelationshipScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var list by remember{mutableStateOf(store.loadRelationship().reversed())}
 var type by remember{mutableStateOf("دیدار")};var note by remember{mutableStateOf("")};var amount by remember{mutableStateOf("")};var emotion by remember{mutableFloatStateOf(7f)}
 Page("رابطه من",onBack){
  LazyColumn(verticalArrangement=Arrangement.spacedBy(9.dp),contentPadding=PaddingValues(bottom=25.dp)){
   item{FeatureHero(Screen.RELATIONSHIP,"روابط عاطفی","رابطه سالم • ارتباط • دیدار • هزینه • حل تعارض")}
   item{
    GoldCard{Text("هدف این بخش: رابطه قوی‌تر، نه کنترل آدم مقابل",fontWeight=FontWeight.ExtraBold);Text("رفتار خودت، بحث‌ها، دیدارها و هزینه‌ها را ثبت کن تا الگوها دیده شوند.",color=Muted)}
   }
   item{
    Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){listOf("دیدار","بحث","هدیه","کمک مالی","سفر").forEach{FilterChip(type==it,{type=it},{Text(it)})}}
    OutlinedTextField(note,{note=it},label={Text("شرح کوتاه")},modifier=Modifier.fillMaxWidth())
    Spacer(Modifier.height(5.dp));OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},label={Text("هزینه/مبلغ (اختیاری)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Text("حال/کیفیت این اتفاق: "+emotion.toInt()+"/10",modifier=Modifier.padding(top=7.dp));Slider(emotion,{emotion=it},valueRange=1f..10f,steps=8,colors=SliderDefaults.colors(thumbColor=Gold,activeTrackColor=Gold))
    PrimaryButton("ثبت"){if(note.isNotBlank()){store.addRelationship(RelationshipEntry(type=type,note=note,amount=amount.toLongOrNull()?:0,emotion=emotion.toInt()));note="";amount="";list=store.loadRelationship().reversed();onChange()}}
   }
   item{
    val total=list.sumOf{it.amount};val avg=if(list.isEmpty())0.0 else list.map{it.emotion}.average()
    Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){StatPill("هزینه رابطه",money(total),Modifier.weight(1f));StatPill("کیفیت میانگین",String.format("%.1f/10",avg),Modifier.weight(1f))}
   }
   items(list.take(30),key={it.id}){x->GoldCard{Row{Column(Modifier.weight(1f)){Text(x.type,fontWeight=FontWeight.Bold);Text(x.note);Text(x.date+" • کیفیت "+x.emotion+"/10",color=Muted,fontSize=11.sp)};Column(horizontalAlignment=Alignment.End){if(x.amount>0)Text(money(x.amount),color=Gold,fontWeight=FontWeight.Bold);TextButton(onClick={store.deleteRelationship(x.id);list=store.loadRelationship().reversed();onChange()}){Text("حذف",color=Danger)}}}}}
  }
 }
}
