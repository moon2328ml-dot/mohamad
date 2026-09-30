package ir.mohammad.lifeos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

val Gold=Color(0xFFD6A928)
val GoldSoft=Color(0xFFFFF3C2)
val Ink=Color(0xFF111111)
val Muted=Color(0xFF6D6D6D)
val AppBg=Color(0xFFFDFCF8)
val Success=Color(0xFF16835A)
val Danger=Color(0xFFB23A48)

@Composable
fun LifeTheme(content: @Composable () -> Unit){
 MaterialTheme(
  colorScheme=lightColorScheme(primary=Gold,onPrimary=Ink,background=AppBg,surface=Color.White,onSurface=Ink,secondary=Ink),
  typography=Typography(
   headlineMedium=androidx.compose.ui.text.TextStyle(fontSize=25.sp,fontWeight=FontWeight.ExtraBold),
   titleLarge=androidx.compose.ui.text.TextStyle(fontSize=20.sp,fontWeight=FontWeight.Bold),
   titleMedium=androidx.compose.ui.text.TextStyle(fontSize=16.sp,fontWeight=FontWeight.Bold),
   bodyLarge=androidx.compose.ui.text.TextStyle(fontSize=15.sp),
   bodyMedium=androidx.compose.ui.text.TextStyle(fontSize=13.sp)
  ),
  content=content
 )
}

@Composable
fun MysticEye(modifier:Modifier=Modifier){
 Canvas(modifier=modifier){
  val w=size.width;val h=size.height;val cy=h/2f
  val eye=Path().apply{moveTo(w*.06f,cy);quadraticBezierTo(w*.5f,h*.06f,w*.94f,cy);quadraticBezierTo(w*.5f,h*.94f,w*.06f,cy);close()}
  drawPath(eye,Gold,style=Stroke(width=max(3f,w*.025f)))
  drawCircle(Gold, radius=w*.18f, center=Offset(w*.5f,cy))
  drawCircle(Color.White, radius=w*.11f, center=Offset(w*.5f,cy))
  drawCircle(Ink, radius=w*.055f, center=Offset(w*.5f,cy))
  drawCircle(Color.White, radius=w*.016f, center=Offset(w*.47f,cy-h*.025f))
  drawLine(Gold,Offset(w*.5f,0f),Offset(w*.5f,h*.18f),strokeWidth=w*.018f)
  drawLine(Gold,Offset(w*.5f,h*.82f),Offset(w*.5f,h),strokeWidth=w*.018f)
 }
}

@Composable
fun Page(title:String,onBack:(()->Unit)?=null,content: @Composable ColumnScope.() -> Unit){
 Column(Modifier.fillMaxSize().background(AppBg).statusBarsPadding()){
  Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=10.dp),verticalAlignment=Alignment.CenterVertically){
   if(onBack!=null) TextButton(onClick=onBack){Text("‹",fontSize=30.sp,color=Ink)}
   Text(title,modifier=Modifier.weight(1f),textAlign=TextAlign.Center,style=MaterialTheme.typography.titleLarge)
   if(onBack!=null) Spacer(Modifier.width(48.dp))
  }
  HorizontalDivider(color=Gold.copy(alpha=.25f))
  Column(Modifier.fillMaxSize().padding(horizontal=14.dp),content=content)
 }
}

@Composable
fun GoldCard(modifier:Modifier=Modifier,content: @Composable ColumnScope.() -> Unit){
 Card(modifier,shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(2.dp)){
  Column(Modifier.fillMaxWidth().border(1.dp,Gold.copy(alpha=.5f),RoundedCornerShape(20.dp)).padding(15.dp),content=content)
 }
}

@Composable
fun SectionTitle(text:String,sub:String?=null){
 Column(Modifier.fillMaxWidth().padding(top=14.dp,bottom=8.dp)){
  Text(text,fontWeight=FontWeight.ExtraBold,fontSize=18.sp)
  if(sub!=null)Text(sub,color=Muted,fontSize=12.sp)
 }
}

@Composable
fun StatPill(label:String,value:String,modifier:Modifier=Modifier){
 Column(modifier.background(GoldSoft,RoundedCornerShape(15.dp)).padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){
  Text(value,fontWeight=FontWeight.ExtraBold,color=Ink,fontSize=17.sp)
  Text(label,color=Muted,fontSize=11.sp,textAlign=TextAlign.Center)
 }
}

@Composable
fun MiniLineChart(values:List<Float>,modifier:Modifier=Modifier){
 Canvas(modifier=modifier.height(120.dp).fillMaxWidth()){
  if(values.size<2)return@Canvas
  val min=values.minOrNull()?:0f;val maxv=values.maxOrNull()?:1f;val span=max(1f,maxv-min)
  val pts=values.mapIndexed{i,v->Offset(size.width*i/(values.size-1),size.height-(v-min)/span*size.height)}
  for(i in 0 until pts.lastIndex)drawLine(Gold,pts[i],pts[i+1],strokeWidth=5f)
  pts.forEach{drawCircle(Ink,6f,it)}
 }
}

@Composable
fun PrimaryButton(text:String,modifier:Modifier=Modifier,onClick:()->Unit){
 Button(onClick=onClick,modifier=modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink)){
  Text(text,fontWeight=FontWeight.Bold)
 }
}

@Composable
fun EmptyHint(text:String){
 Text(text,color=Muted,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth().padding(24.dp))
}

fun money(v:Long):String{
 val s=v.toString();val b=StringBuilder();var c=0
 for(i in s.indices.reversed()){b.append(s[i]);c++;if(c%3==0&&i!=0)b.append(',')}
 return b.reverse().toString()+" تومان"
}
