package ir.mohammad.lifeos

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

private val routineColors=listOf(
 Color(0xFF11C86B), Color(0xFF278BFF), Color(0xFF8057FF),
 Color(0xFFFF7A1A), Color(0xFF00B9C8), Color(0xFFE93B68)
)

@Composable
fun RoutineScreen(store:LifeStore,onBack:()->Unit,onChange:()->Unit){
 var rows by remember { mutableStateOf(store.loadRoutine()) }
 var title by remember { mutableStateOf("") }
 var time by remember { mutableStateOf("08:00") }
 var colorIndex by remember { mutableIntStateOf(0) }

 val completed=rows.count{it.done}
 val percent=if(rows.isEmpty())0 else completed*100/rows.size
 val lastCheck=store.loadCheckIns().lastOrNull()
 val healthScore=run {
  var p=0
  if(store.loadWeights().isNotEmpty()) p+=35
  if(store.loadFoods().any{it.date==LocalDate.now().toString()}) p+=35
  if(store.items("workout").any{it.done}) p+=30
  p.coerceIn(0,100)
 }
 val focusScore=(lastCheck?.focus?:0)*10
 val studyScore=if(store.journals("growth").any{it.date==LocalDate.now().toString()})100 else 0
 val financeScore=if(store.loadMoney().any{it.date==LocalDate.now().toString()})100 else 0

 Page("برنامه روزانه",onBack){
  LazyColumn(
   modifier=Modifier.fillMaxSize(),
   verticalArrangement=Arrangement.spacedBy(9.dp),
   contentPadding=PaddingValues(bottom=28.dp)
  ){
   item{
    Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){
     Text(jalaliDate(LocalDate.now()),fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
     Spacer(Modifier.height(4.dp))
     Text("برنامه را خودت می‌سازی",fontWeight=FontWeight.ExtraBold,fontSize=17.sp)
    }
   }

   item{
    GlassCard(strong=true){
     Text("افزودن به برنامه",fontWeight=FontWeight.ExtraBold,color=Gold)
     Spacer(Modifier.height(7.dp))
     Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){
      OutlinedTextField(
       value=time,
       onValueChange={v->
        val clean=v.filter{it.isDigit()||it==':'}.take(5)
        time=clean
       },
       label={Text("ساعت")},
       modifier=Modifier.width(104.dp),
       singleLine=true
      )
      OutlinedTextField(
       value=title,
       onValueChange={title=it},
       label={Text("عنوان کار/روتین")},
       modifier=Modifier.weight(1f),
       singleLine=true
      )
     }
     Spacer(Modifier.height(8.dp))
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp),verticalAlignment=Alignment.CenterVertically){
      routineColors.forEachIndexed{i,c->
       Surface(
        onClick={colorIndex=i},
        shape=CircleShape,
        color=c,
        border=if(colorIndex==i) androidx.compose.foundation.BorderStroke(3.dp,Gold) else null,
        modifier=Modifier.size(if(colorIndex==i)30.dp else 25.dp)
       ){}
      }
      Spacer(Modifier.weight(1f))
      Button(
       onClick={
        if(title.isNotBlank()){
         val normalized=normalizeTime(time)
         store.addRoutine(RoutineEntry(title=title.trim(),time=normalized,color=colorIndex))
         rows=store.loadRoutine()
         title=""
         onChange()
        }
       },
       colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink),
       shape=RoundedCornerShape(14.dp)
      ){Text("افزودن",fontWeight=FontWeight.Bold)}
     }
    }
   }

   item{
    GlassCard{
     Row(verticalAlignment=Alignment.CenterVertically){
      Text("پیشرفت امروز",Modifier.weight(1f),fontWeight=FontWeight.ExtraBold)
      Text(percent.toString()+"٪",color=Gold,fontWeight=FontWeight.ExtraBold)
     }
     Spacer(Modifier.height(7.dp))
     LinearProgressIndicator(
      progress={percent/100f},
      modifier=Modifier.fillMaxWidth().height(9.dp),
      color=Success,
      trackColor=MaterialTheme.colorScheme.surfaceVariant
     )
    }
   }

   if(rows.isEmpty()){
    item{
     GlassCard{
      EmptyHint("هنوز هیچ روتینی نساختی. اولین مورد را با ساعت دلخواه خودت اضافه کن.")
     }
    }
   }else{
    items(rows,key={it.id}){row->
     val c=routineColors[row.color.coerceIn(0,routineColors.lastIndex)]
     GlassCard{
      Row(verticalAlignment=Alignment.CenterVertically){
       Box(
        Modifier.size(38.dp).background(c,CircleShape).border(2.dp,Color.White.copy(.55f),CircleShape),
        contentAlignment=Alignment.Center
       ){
        Text(if(row.done)"✓" else "•",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Black)
       }
       Spacer(Modifier.width(9.dp))
       Column(Modifier.weight(1f)){
        Text(row.title,fontWeight=FontWeight.ExtraBold,fontSize=14.sp)
        Text(row.time,color=Gold,fontWeight=FontWeight.Bold,fontSize=12.sp)
       }
       Checkbox(
        checked=row.done,
        onCheckedChange={
         store.toggleRoutine(row.id)
         rows=store.loadRoutine()
         onChange()
        },
        colors=CheckboxDefaults.colors(checkedColor=Success,checkmarkColor=Color.White)
       )
       TextButton(onClick={
        store.deleteRoutine(row.id)
        rows=store.loadRoutine()
        onChange()
       }){Text("×",color=Danger,fontSize=18.sp)}
      }
     }
    }
   }

   item{
    SectionTitle("پیشرفت امروز")
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){
     ProgressRing(healthScore,"سلامت")
     ProgressRing(focusScore,"تمرکز")
     ProgressRing(studyScore,"مطالعه")
     ProgressRing(financeScore,"مالی")
    }
   }
  }
 }
}

private fun normalizeTime(raw:String):String{
 val parts=raw.split(":")
 val h=parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0,23)?:8
 val m=parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0,59)?:0
 return String.format("%02d:%02d",h,m)
}

fun jalaliDate(date:LocalDate):String{
 val gDays=intArrayOf(31,28,31,30,31,30,31,31,30,31,30,31)
 val jDays=intArrayOf(31,31,31,31,31,31,30,30,30,30,30,29)
 var gy=date.year-1600
 val gm=date.monthValue-1
 var gd=date.dayOfMonth-1
 var gDayNo=365*gy+(gy+3)/4-(gy+99)/100+(gy+399)/400
 for(i in 0 until gm) gDayNo+=gDays[i]
 val leap=(date.year%4==0&&date.year%100!=0)||date.year%400==0
 if(gm>1&&leap) gDayNo++
 gDayNo+=gd
 var jDayNo=gDayNo-79
 val jNp=jDayNo/12053
 jDayNo%=12053
 var jy=979+33*jNp+4*(jDayNo/1461)
 jDayNo%=1461
 if(jDayNo>=366){
  jy+=(jDayNo-1)/365
  jDayNo=(jDayNo-1)%365
 }
 var jm=0
 while(jm<11 && jDayNo>=jDays[jm]){
  jDayNo-=jDays[jm]
  jm++
 }
 val jd=jDayNo+1
 return "امروز • "+jy.toString()+"/"+String.format("%02d",jm+1)+"/"+String.format("%02d",jd)
}
