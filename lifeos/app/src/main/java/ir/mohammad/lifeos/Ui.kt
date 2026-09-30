package ir.mohammad.lifeos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

val Gold = Color(0xFFFFC83D)
val GoldDeep = Color(0xFFD79809)
val GoldSoft = Color(0xFFFFF1C3)
val Ink = Color(0xFF0A0B0E)
val Night = Color(0xFF07090F)
val Night2 = Color(0xFF10151F)
val Muted = Color(0xFF6D7078)
val AppBg = Color(0xFFF9F7F1)
val Success = Color(0xFF18B777)
val Danger = Color(0xFFE74E5D)
val NeonBlue = Color(0xFF27C7FF)
val NeonPink = Color(0xFFFF2A78)
val NeonPurple = Color(0xFF7D51FF)

val LocalDarkMode = compositionLocalOf { false }

@Composable
fun LifeTheme(dark:Boolean=false, content:@Composable () -> Unit){
 val scheme = if(dark) darkColorScheme(
  primary=Gold, onPrimary=Ink,
  background=Night, onBackground=Color(0xFFF7F4EC),
  surface=Color(0xFF121720), onSurface=Color(0xFFF7F4EC),
  surfaceVariant=Color(0xFF1A202B), onSurfaceVariant=Color(0xFFC7CBD2),
  secondary=NeonBlue
 ) else lightColorScheme(
  primary=GoldDeep, onPrimary=Ink,
  background=AppBg, onBackground=Ink,
  surface=Color.White, onSurface=Ink,
  surfaceVariant=Color(0xFFF6F2E7), onSurfaceVariant=Color(0xFF555861),
  secondary=NeonBlue
 )
 CompositionLocalProvider(LocalDarkMode provides dark){
  MaterialTheme(
   colorScheme=scheme,
   typography=Typography(
    headlineMedium=androidx.compose.ui.text.TextStyle(fontSize=25.sp,fontWeight=FontWeight.ExtraBold),
    titleLarge=androidx.compose.ui.text.TextStyle(fontSize=20.sp,fontWeight=FontWeight.ExtraBold),
    titleMedium=androidx.compose.ui.text.TextStyle(fontSize=16.sp,fontWeight=FontWeight.Bold),
    bodyLarge=androidx.compose.ui.text.TextStyle(fontSize=15.sp,fontWeight=FontWeight.Medium),
    bodyMedium=androidx.compose.ui.text.TextStyle(fontSize=13.sp)
   ),
   content=content
  )
 }
}

@Composable
fun AppBackground(modifier:Modifier=Modifier){
 val dark=LocalDarkMode.current
 Canvas(modifier.fillMaxSize()){
  val bg = if(dark) Brush.verticalGradient(listOf(Color(0xFF05070C),Color(0xFF0C111A),Color(0xFF05070C))) else
   Brush.verticalGradient(listOf(Color(0xFFFFFFFF),Color(0xFFFFFCF4),Color(0xFFF7F0DD)))
  drawRect(bg)
  val glow=if(dark) Gold.copy(alpha=.10f) else Gold.copy(alpha=.12f)
  drawCircle(glow,radius=size.width*.55f,center=Offset(size.width*.12f,size.height*.10f))
  drawCircle(NeonBlue.copy(alpha=if(dark).07f else .045f),radius=size.width*.45f,center=Offset(size.width*.96f,size.height*.30f))
  drawCircle(NeonPurple.copy(alpha=if(dark).055f else .03f),radius=size.width*.38f,center=Offset(size.width*.15f,size.height*.85f))
 }
}

@Composable
fun MysticEye(modifier:Modifier=Modifier,glow:Boolean=true){
 Canvas(modifier=modifier){
  val w=size.width; val h=size.height; val cy=h/2f
  if(glow) drawCircle(Gold.copy(alpha=.18f),w*.46f,Offset(w*.5f,cy))
  val tri=Path().apply{
   moveTo(w*.5f,h*.05f); lineTo(w*.94f,h*.88f); lineTo(w*.06f,h*.88f); close()
  }
  drawPath(tri,GoldDeep,style=Stroke(width=max(3f,w*.035f)))
  val eye=Path().apply{
   moveTo(w*.13f,cy)
   quadraticBezierTo(w*.5f,h*.24f,w*.87f,cy)
   quadraticBezierTo(w*.5f,h*.76f,w*.13f,cy)
   close()
  }
  drawPath(eye,Gold,style=Stroke(width=max(3f,w*.026f)))
  drawCircle(Brush.radialGradient(listOf(NeonBlue,Color(0xFF0B4B8D),Color.Black)),radius=w*.16f,center=Offset(w*.5f,cy))
  drawCircle(Ink,radius=w*.075f,center=Offset(w*.5f,cy))
  drawCircle(Color.White,radius=w*.025f,center=Offset(w*.46f,cy-h*.035f))
  drawLine(Gold,Offset(w*.5f,0f),Offset(w*.5f,h*.16f),strokeWidth=w*.012f)
  drawLine(Gold,Offset(w*.5f,h*.84f),Offset(w*.5f,h),strokeWidth=w*.012f)
 }
}

@Composable
fun Page(title:String,onBack:(()->Unit)?=null,content:@Composable ColumnScope.()->Unit){
 Box(Modifier.fillMaxSize()){
  AppBackground()
  Column(Modifier.fillMaxSize().statusBarsPadding()){
   Row(
    Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=8.dp),
    verticalAlignment=Alignment.CenterVertically
   ){
    if(onBack!=null){
     GlassIconButton("‹",onBack)
    } else Spacer(Modifier.size(42.dp))
    Text(title,Modifier.weight(1f),textAlign=TextAlign.Center,style=MaterialTheme.typography.titleLarge)
    Spacer(Modifier.size(42.dp))
   }
   Box(Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent,Gold.copy(.7f),Color.Transparent))))
   Column(Modifier.fillMaxSize().padding(horizontal=12.dp),content=content)
  }
 }
}

@Composable
fun GlassCard(
 modifier:Modifier=Modifier,
 strong:Boolean=false,
 content:@Composable ColumnScope.()->Unit
){
 val dark=LocalDarkMode.current
 val fill=if(dark) Color(0xB319202B) else Color(0xDFFFFFFF)
 val border=if(strong) Gold.copy(.88f) else Gold.copy(if(dark).50f else .55f)
 Card(
  modifier=modifier.shadow(if(strong)10.dp else 4.dp,RoundedCornerShape(22.dp)),
  shape=RoundedCornerShape(22.dp),
  colors=CardDefaults.cardColors(containerColor=Color.Transparent)
 ){
  Column(
   Modifier.fillMaxWidth()
    .background(
     Brush.verticalGradient(
      if(dark) listOf(fill,Color(0x9B0C1118))
      else listOf(fill,Color(0xCFFAF6EC))
     ),
     RoundedCornerShape(22.dp)
    )
    .border(if(strong)1.5.dp else 1.dp,border,RoundedCornerShape(22.dp))
    .padding(14.dp),
   content=content
  )
 }
}

@Composable
fun GoldCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit)=GlassCard(modifier,false,content)

@Composable
fun GlassIconButton(symbol:String,onClick:()->Unit){
 val dark=LocalDarkMode.current
 Surface(
  modifier=Modifier.size(42.dp),
  onClick=onClick,
  shape=CircleShape,
  color=if(dark)Color(0xAA161B24) else Color(0xEFFFFFFF),
  border=androidx.compose.foundation.BorderStroke(1.dp,Gold.copy(.6f))
 ){
  Box(contentAlignment=Alignment.Center){Text(symbol,color=if(dark)Gold else Ink,fontSize=22.sp,fontWeight=FontWeight.Bold)}
 }
}

@Composable
fun SectionTitle(text:String,sub:String?=null){
 Column(Modifier.fillMaxWidth().padding(top=14.dp,bottom=8.dp)){
  Text(text,fontWeight=FontWeight.ExtraBold,fontSize=18.sp)
  if(sub!=null) Text(sub,color=MaterialTheme.colorScheme.onSurfaceVariant,fontSize=12.sp)
 }
}

@Composable
fun StatPill(label:String,value:String,modifier:Modifier=Modifier){
 val dark=LocalDarkMode.current
 Column(
  modifier.background(
   if(dark) Color(0xB31A202B) else Color(0xDFFFFFFF),
   RoundedCornerShape(17.dp)
  ).border(1.dp,Gold.copy(.55f),RoundedCornerShape(17.dp)).padding(9.dp),
  horizontalAlignment=Alignment.CenterHorizontally
 ){
  Text(value,fontWeight=FontWeight.ExtraBold,color=if(dark)Gold else Ink,fontSize=16.sp)
  Text(label,color=MaterialTheme.colorScheme.onSurfaceVariant,fontSize=10.sp,textAlign=TextAlign.Center)
 }
}

@Composable
fun ProgressRing(value:Int,label:String,modifier:Modifier=Modifier){
 val dark=LocalDarkMode.current
 Box(modifier.size(62.dp),contentAlignment=Alignment.Center){
  Canvas(Modifier.fillMaxSize()){
   drawArc(if(dark)Color(0xFF2B3039) else Color(0xFFE6E2D7),-90f,360f,false,style=Stroke(8f))
   drawArc(if(value>=70)Success else if(value>=50)Gold else Danger,-90f,3.6f*value,false,style=Stroke(8f,cap=StrokeCap.Round))
  }
  Column(horizontalAlignment=Alignment.CenterHorizontally){
   Text("$value%",fontWeight=FontWeight.ExtraBold,fontSize=12.sp)
   Text(label,fontSize=8.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
  }
 }
}

@Composable
fun MiniLineChart(values:List<Float>,modifier:Modifier=Modifier){
 Canvas(modifier=modifier.height(120.dp).fillMaxWidth()){
  if(values.size<2)return@Canvas
  val min=values.minOrNull()?:0f; val maxv=values.maxOrNull()?:1f; val span=max(1f,maxv-min)
  val pts=values.mapIndexed{i,v->Offset(size.width*i/(values.size-1),size.height-(v-min)/span*size.height)}
  for(i in 0 until pts.lastIndex) drawLine(Gold,pts[i],pts[i+1],strokeWidth=5f)
  pts.forEach{drawCircle(NeonBlue,6f,it)}
 }
}

@Composable
fun PrimaryButton(text:String,modifier:Modifier=Modifier,onClick:()->Unit){
 Button(
  onClick=onClick,
  modifier=modifier.fillMaxWidth().heightIn(min=50.dp),
  shape=RoundedCornerShape(16.dp),
  colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Ink),
  elevation=ButtonDefaults.buttonElevation(defaultElevation=5.dp)
 ){Text(text,fontWeight=FontWeight.ExtraBold)}
}

@Composable
fun EmptyHint(text:String){
 Text(text,color=MaterialTheme.colorScheme.onSurfaceVariant,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth().padding(24.dp))
}

fun money(v:Long):String{
 val s=v.toString(); val b=StringBuilder(); var c=0
 for(i in s.indices.reversed()){b.append(s[i]);c++;if(c%3==0&&i!=0)b.append(',')}
 return b.reverse().toString()+" تومان"
}
