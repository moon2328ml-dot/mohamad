package ir.mohammad.lifeos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RefCrop(val x:Int,val y:Int,val w:Int,val h:Int)

private val growthCrop=RefCrop(5,286,196,126)
private val financeCrop=RefCrop(208,286,198,126)
private val relationCrop=RefCrop(414,286,200,126)
private val bodyCrop=RefCrop(622,286,197,126)
private val spiritCrop=RefCrop(827,286,202,126)
private val powerCrop=RefCrop(1035,286,200,126)
private val attractionCrop=RefCrop(1243,286,201,126)
private val analysisCrop=RefCrop(1450,286,216,126)
private val analysisBodyCrop=RefCrop(572,520,260,385)
private val darkFigureCrop=RefCrop(1094,520,259,385)
val homeHeroCrop=RefCrop(20,0,290,270)

fun featureCrop(screen:Screen):RefCrop = when(screen){
 Screen.GROWTH -> growthCrop
 Screen.FINANCE, Screen.DEBTS, Screen.INVESTMENT -> financeCrop
 Screen.RELATIONSHIP -> relationCrop
 Screen.BODY -> bodyCrop
 Screen.SPIRITUAL, Screen.GRATITUDE -> spiritCrop
 Screen.POWER -> darkFigureCrop
 Screen.ATTRACTION -> attractionCrop
 Screen.ANALYSIS -> analysisBodyCrop
 else -> growthCrop
}

@Composable
fun ReferenceCrop(
 crop:RefCrop,
 modifier:Modifier=Modifier,
 radius:Int=18
){
 BoxWithConstraints(
  modifier=modifier.clip(RoundedCornerShape(radius.dp))
 ){
  val fullW=maxWidth*(1672f/crop.w.toFloat())
  val fullH=maxHeight*(941f/crop.h.toFloat())
  val offsetX=-(maxWidth*(crop.x.toFloat()/crop.w.toFloat()))
  val offsetY=-(maxHeight*(crop.y.toFloat()/crop.h.toFloat()))
  Image(
   painter=painterResource(R.drawable.lifeos_sprite),
   contentDescription=null,
   contentScale=ContentScale.FillBounds,
   modifier=Modifier.requiredSize(fullW,fullH).offset(x=offsetX,y=offsetY)
  )
 }
}

@Composable
fun CategoryReferenceArt(screen:Screen,modifier:Modifier=Modifier){
 val crop=when(screen){
  Screen.GROWTH -> growthCrop
  Screen.FINANCE,Screen.DEBTS,Screen.INVESTMENT -> financeCrop
  Screen.RELATIONSHIP -> relationCrop
  Screen.BODY -> bodyCrop
  Screen.SPIRITUAL,Screen.GRATITUDE -> spiritCrop
  Screen.POWER -> powerCrop
  Screen.ATTRACTION -> attractionCrop
  Screen.ANALYSIS -> analysisCrop
  else -> growthCrop
 }
 ReferenceCrop(crop,modifier)
}

@Composable
fun FeatureHero(
 screen:Screen,
 title:String,
 subtitle:String,
 modifier:Modifier=Modifier
){
 val dark=LocalDarkMode.current
 Box(
  modifier.fillMaxWidth().height(158.dp)
   .shadow(10.dp,RoundedCornerShape(24.dp))
   .clip(RoundedCornerShape(24.dp))
   .border(1.4.dp,Gold.copy(.92f),RoundedCornerShape(24.dp))
 ){
  ReferenceCrop(featureCrop(screen),Modifier.fillMaxSize(),24)
  Box(
   Modifier.fillMaxSize().background(
    Brush.verticalGradient(
     listOf(
      Color.Transparent,
      if(dark) Color(0x55000000) else Color(0x22000000),
      Color(0xDF05070B)
     )
    )
   )
  )
  Box(
   Modifier.fillMaxWidth().height(1.dp)
    .background(Brush.horizontalGradient(listOf(Color.Transparent,Color.White.copy(.85f),Color.Transparent)))
    .align(Alignment.TopCenter)
  )
  Column(
   Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(14.dp)
  ){
   Text(title,color=Color.White,fontWeight=FontWeight.ExtraBold,fontSize=21.sp)
   Text(subtitle,color=Color.White.copy(.92f),fontSize=11.sp)
  }
 }
}

@Composable
fun GlassSectionLabel(title:String,subtitle:String?=null){
 Column(Modifier.fillMaxWidth()){
  Text(title,fontWeight=FontWeight.ExtraBold,fontSize=17.sp,color=if(LocalDarkMode.current)Gold else MaterialTheme.colorScheme.onSurface)
  if(subtitle!=null) Text(subtitle,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
 }
}
