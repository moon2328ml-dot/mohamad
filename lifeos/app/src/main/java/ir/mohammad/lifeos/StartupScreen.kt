package ir.mohammad.lifeos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartupScreen(){
 Box(
  Modifier.fillMaxSize().background(
   Brush.verticalGradient(listOf(Color(0xFFFFFBEE),Color.White))
  ),
  contentAlignment=Alignment.Center
 ){
  Column(horizontalAlignment=Alignment.CenterHorizontally){
   Image(
    painter=painterResource(R.drawable.app_logo),
    contentDescription="متد محمد",
    contentScale=ContentScale.Crop,
    modifier=Modifier.size(132.dp).clip(RoundedCornerShape(28.dp))
   )
   Spacer(Modifier.height(14.dp))
   Text("متد محمد",fontSize=24.sp,fontWeight=FontWeight.ExtraBold,color=Color(0xFFD6A928))
   Text("سیستم شخصی زندگی من",fontSize=12.sp,color=Color(0xFF555555))
  }
 }
}
