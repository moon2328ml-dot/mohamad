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

@Composable
fun AccountScreen(
 store:LifeStore,
 dark:Boolean,
 onToggleDark:()->Unit,
 onDone:()->Unit,
 onBack:(()->Unit)?
){
 var name by remember{mutableStateOf(store.name)}
 var email by remember{mutableStateOf(store.email)}
 var target by remember{mutableStateOf(store.targetWeight.toString())}
 Page("ØªÙ†Ø¸ÛŒÙ…Ø§Øª",onBack){
  Spacer(Modifier.height(12.dp))
  GoldCard{
   Row(verticalAlignment=Alignment.CenterVertically){
    Image(
     painter=painterResource(R.drawable.app_logo),
     contentDescription="Ù„ÙˆÚ¯ÙˆÛŒ Ù…ØªØ¯ Ù…Ø­Ù…Ø¯",
     contentScale=ContentScale.Crop,
     modifier=Modifier.size(86.dp).clip(RoundedCornerShape(22.dp)).border(1.5.dp,Gold,RoundedCornerShape(22.dp))
    )
    Spacer(Modifier.width(14.dp))
    Column(Modifier.weight(1f)){
     Text("Ù…ØªØ¯ Ù…Ø­Ù…Ø¯",fontSize=25.sp,fontWeight=FontWeight.ExtraBold)
     Text("Ø³ÛŒØ³ØªÙ… Ø´Ø®ØµÛŒ Ø²Ù†Ø¯Ú¯ÛŒ Ù…Ù†",color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
   }
  }
  SectionTitle("Ø¸Ø§Ù‡Ø± Ø¨Ø±Ù†Ø§Ù„Ù‡")
  GlassCard{
   Row(verticalAlignment=Alignment.CenterVertically){
    Column(Modifier.weight(1f)){
     Text(if(dark)"Ø­Ø§Ù„Øª Ø¯Ø§Ø±Ú© ÙØ¹Ø§Ù„ Ø§Ø³Øª" else "Ø­Ø§Ù„Øª Ø±ÙˆØ´Ù† ÙØ¹Ø§Ù„ Ø§Ø³Øª",fontWeight=FontWeight.Bold)
     Text("Ø³ÙÛŒØ¯/Ø·Ù„Ø§ÛŒÛŒ Ùˆ Ù…Ø´Ú©ÛŒ/Ø·Ù„Ø§ÛŒÛŒ Ø¨Ø§ Ù¾Ù†Ù„â€ŒÙ‡Ø§ÛŒ Ø´ÛŒØ´Ù‡â€ŒØ§ÛŒ",color=MaterialTheme.colorScheme.onSurfaceVariant,fontSize=12.sp)
    }
    Switch(checked=dark,onCheckedChange={onToggleDark()},colors=SwitchDefaults.colors(checkedThumbColor=Gold,checkedTrackColor=GoldDeep))
   }
  }
  SectionTitle("Ù¾Ø±ÙˆÙØ§ÛŒÙ„ Ù…Ù†","Ø§ÛŒÙ† Ø§Ù¾ ØªÚ©â€ŒÚ©Ø§Ø±Ø¨Ø±Ù‡ Ùˆ Ø¨Ø±Ø§ÛŒ Ø§Ø³ØªÙØ§Ø¯Ù‡ Ø´Ø®ØµÛŒ ØªÙˆ Ø·Ø±Ø§Ø­ÛŒ Ø´Ø¯Ù‡.")
  OutlinedTextField(name,{name=it},label={Text("Ù†Ø§Ù…")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  Spacer(Modifier.height(8.dp))
  OutlinedTextField(email,{email=it},label={Text("Ø§ÛŒÙ…ÛŒÙ„ Ø­Ø³Ø§Ø¨ (Ø§Ø®ØªÛŒØ§Ø±ÛŒ Ø¯Ø± Ø§ÛŒÙ† Ù†Ø³Ø®Ù‡)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  Spacer(Modifier.height(8.dp))
  OutlinedTextField(target,{target=it},label={Text("ÙˆØ²Ù† Ù‡Ø¯Ù")},modifier=Modifier.fillMaxWidth(),singleLine=true)
  Spacer(Modifier.height(16.dp))
  PrimaryButton("Ø°Ø®ÛŒØ±Ù‡ ØªÙ†Ø¸ÛŒÙ…Ø§Øª"){
   if(name.isNotBlank()){
    store.name=name.trim()
    store.email=email.trim()
    store.targetWeight=target.toDoubleOrNull()?:store.targetWeight
    onDone()
   }
  }
  Spacer(Modifier.height(8.dp))
  Text(
   "Ø§Ø·Ù„Ø§Ø¹Ø§Øª ÙØ¹Ù„Ø§Ù‹ Ø±ÙˆÛŒ Ú¯ÙˆØ´ÛŒ Ùˆ Android Backup Ù†Ú¯Ù‡Ø¯Ø§Ø±ÛŒ Ù…ÛŒâ€ŒØ´ÙˆÙ†Ø¯Ø› ÙˆØ±ÙˆØ¯ Ø§Ø¨Ø±ÛŒ Ø§Ø®ØªØµØ§ØµÛŒ Ù‡Ù†ÙˆØ² ÙØ¹Ø§Ù„ Ù†Ø´Ø¯Ù‡.",
   color=MaterialTheme.colorScheme.onSurfaceVariant,
   fontSize=11.sp,
   textAlign=TextAlign.Center,
   modifier=Modifier.fillMaxWidth()
  )
 }
}

data class Dash(
 val icon:String,
 val title:String,
 val sub:String,
 val screen:Screen,
 val c1:Color,
 val c2:Color
)

@Composable
fun HomeScreen(
 store:LifeStore,
 version:Int,
 dark:Boolean,
 onToggleDark:()->Unit,
 onOpen:(Screen)->Unit
){
 val routine=store.items("routine")
 val goals=store.items("daily_goal")
 val allToday=routine+goals
 val done=allToday.count{it.done}
 val progress=if(allToday.isEmpty())0 else (done*100/allToday.size).coerceIn(0,100)
 val expenses=store.loadMoney().filter{it.kind=="expense"}.sumOf{it.amount}
 val investments=store.loadMoney().filter{it.kind=="investment"}.sumOf{it.amount}
 val latestWeight=store.loadWeights().lastOrNull()?.weight
 val cards=listOf(
  Dash("ðŸ§ ","Ø±Ø´Ø¯ ÙØ±Ø¯ÛŒ","Ø§Ø¹ØªÙ…Ø§Ø¯ Ø¨Ù‡ Ù†ÙØ³ â€¢ Ø¹Ø§Ø¯Øªâ€ŒÙ‡Ø§ â€¢ Ø§Ù†Ú¯ÛŒØ²Ù‡",Screen.GROWTH,Color(0xFF3024C8),Color(0xFF23A9FF)),
  Dash("â–¥","Ù…ÙˆÙÙ‚ÛŒØª Ù…Ø§Ù„ÛŒ","Ø¯Ø±Ø¢Ù…Ø¯ â€¢ Ø³Ø±Ù…Ø§ÛŒÙ‡â€ŒÚ¯Ø°Ø§Ø±ÛŒ â€¢ Ù‡ÙˆØ´ Ù…Ø§Ù„ÛŒ",Screen.FINANCE,Color(0xFFFFA000),Color(0xFFFFD54F)),
  Dash("â™¥","Ø±ÙˆØ§Ø¨Ø· Ø¹Ø§Ø·ÙÛŒ","Ø±Ø§Ø¨Ø·Ù‡ Ø³Ø§Ù„Ù… â€¢ Ø§Ø±ØªØ¨Ø§Ø· â€¢ Ø±Ø´Ø¯ Ù…Ø´ØªØ±Ú©",Screen.RELATIONSHIP,Color(0xFFFF1744),Color(0xFFFF6E9F)),
  Dash("ðŸ‹","Ø³Ù„Ø§Ù…Øª Ùˆ Ø¨Ø¯Ù†","ØªØºØ°ÛŒÙ‡ â€¢ ÙˆØ±Ø²Ø´ â€¢ Ø®ÙˆØ§Ø¨ â€¢ Ø§Ù†Ø±Ú˜ÛŒ",Screen.BODY,Color(0xFF5B2A19),Color(0xFFFF6D2D)),
  Dash("â™¨","Ù…Ø¹Ù†ÙˆÛŒØª Ùˆ Ø¢Ø±Ø§Ù…Ø´","Ø¹Ø¨Ø§Ø¯Øª â€¢ Ù…Ø¯ÛŒØªÛŒØ´Ù† â€¢ Ø¢Ø±Ø§Ù…Ø´ Ø°Ù‡Ù†",Screen.SPIRITUAL,Color(0xFFCB7B00),Color(0xFFFFD56A)),
  Dash("â—","Ø±ÙˆØ§Ù†Ø´Ù†Ø§Ø³ÛŒ Ø³ÛŒØ§Ù‡","Ø¯ÙØ§Ø¹ â€¢ Ù…Ø±Ø²Ø¨Ù†Ø¯ÛŒ â€¢ Ù‚Ø¯Ø±Øª Ø§Ø¬ØªÙ…Ø§Ø¹ÛŒ",Screen.POWER,Color(0xFF4A0A70),Color(0xFFFF2B8A)),
  Dash("âˆª","Ù‚Ø§Ù†ÙˆÙ† Ø¬Ø°Ø¨","Ø¨Ø§ÙˆØ± â€¢ ØªØ¬Ø³Ù… â€¢ ØªÙ…Ø±ÛŒÙ† â€¢ Ø§Ù‚Ø¯Ø§Ù…",Screen.ATTRACTION,Color(0xFF5929C7),Color(0xFF00C8FF)),
  Dash("â–¥","Ø¢Ù†Ø§Ù„ÛŒØ² Ù…Ù†","Ø§Ø³Ú©Ù† Ø´Ø®ØµÛŒØª â€¢ Ù†Ù‚Ø§Ø· Ù‚ÙˆØª â€¢ Ù…Ø³ÛŒØ± Ø§ØµÙ„Ø§Ø­",Screen.ANALYSIS,Color(0xFF143B7A),Color(0xFF1EC8FF))
 )
 Box(Modifier.fillMaxSize()){
  AppBackground()
  Column(Modifier.fillMaxSize().statusBarsPadding()){
   Row(
    Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=8.dp),
    verticalAlignment=Alignment.CenterVertically
   ){
    Box(
     Modifier.size(48.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Gold,GoldDeep))).border(1.dp,Gold,CircleShape),
     contentAlignment=Alignment.Center
    ){
     Text((store.name.ifBlank{"Ù…"}).take(1),fontWeight=FontWeight.ExtraBold,color=Ink,fontSize=19.sp)
    }
    Spacer(Modifier.width(9.dp))
    Column(Modifier.weight(1f)){
     Text(store.name.ifBlank{"Ù…Ø­Ù…Ø¯"},fontWeight=FontWeight.ExtraBold,fontSize=18.sp)
     Text("Ù†Ø³Ø®Ù‡ Ø¨Ù‡ØªØ± Ù…Ù† Ø¯Ø± Ø­Ø§Ù„ Ø³Ø§Ø®ØªÙ‡â€ŒØ´Ø¯Ù† Ø§Ø³Øª",fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
    GlassIconButton(if(dark)"â˜€" else "â˜¾",onToggleDark)
   }

   LazyColumn(
    modifier=Modifier.weight(1f),
    contentPadding=PaddingValues(horizontal=10.dp,bottom=10.dp),
    verticalArrangement=Arrangement.spacedBy(10.dp)
   ){
    item{
     Box(
      Modifier.fillMaxWidth().height(170.dp)
       .shadow(12.dp,RoundedCornerShape(24.dp))
       .clip(RoundedCornerShape(24.dp))
       .border(1.5.dp,Gold,RoundedCornerShape(24.dp))
     ){
      Image(
       painter=painterResource(R.drawable.app_logo),
       contentDescription="Ù…ØªØ¯ Ù…Ø­Ù…Ø¯",
       contentScale=ContentScale.Crop,
       modifier=Modifier.fillMaxSize()
      )
      Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(.10f),Color.Black.copy(.80f)))))
      Column(
       Modifier.fillMaxSize().padding(13.dp),
       verticalArrangement=Arrangement.Bottom
      {
       Text("Ù…ØªØ¯ Ù…Ø­Ù…Ø¯",color=Gold,fontSize=22.sp,fontWeight=FontWeight.ExtraBold)
       Text("Ø²Ù†Ø¯Ù¯ÛŒ Ø¢Ú¯Ø§Ù‡Ù†ØØŒ Ù‚Ø¯Ø±Øª ÙˆØ§Ù‚Ø¹ÙŠ, Ù†Ø³Ø®Ù‡ Ø¨Ù‡ØªØ± Ù…Ù†",color=Color.White,fontSize=11.sp)
       Spacer(Modifier.height(8.dp))
       Row(verticalAlignment=Alignment.CenterVertically){
        LinearProgressIndicator(
         progress={progress/100f},
         modifier=Modifier.weight(1f).height(8.dp).clip(CircleShape),
         color=Success,
         trackColor=Color.White.copy(.28f)
        )
        Spacer(Modifier.width(8.dp))
        Text(progress.toString()+"%",color=Color.White,fontWeight=FontWeight.ExtraBold)
       }
      }
     }
    }
    item{
     PrimaryButton("Ø§Ø¯Ø§Ù…Ù‡ Ù…Ø³ÛŒØ± Ø§Ù…Ø±ÙˆØ²"){
      onOpen(if(routine.isNotEmpty())Screen.ROUTINE else Screen.DAILY_GOALS)
     }
    }
    item{
     LazyVerticalGrid(
      columns=GridCells.Fixed(4),
      modifier=Modifier.fillMaxWidth().height(204.dp),
      userScrollEnabled=false,
      horizontalArrangement=Arrangement.spacedBy(6.dp),
      verticalArrangement=Arrangement.spacedBy(7.dp)
     ){
      gridItems(cards){c->HomeCategoryTile(c,onOpen)}
     }
   }
    item{
     Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){
      StatPill("Ù…ÛŒØ±Ø§Ù‡ Ø§Ù…Ø±ÙˆØ²",progress.toString()+"%",Modifier.weight(1f))
      StatPill("Ø³Ø±Ù…Ø§ÛŒÙ‡",money(investments),Modifier.weight(1f))
      StatPill("ÙˆØ²Ù†",latestWeight?.let{it.toString()+" kg"}?:"â€”",Modifier.weight(1f))
     }
   }
    item{
     GlassCard(strong=true){
      Text("Ú©Ù†ÛŒØ±Ù„ Ø²Ù†Ø¯Ù¯ÛŒ Ø¯Ø±Ø¯Ø³ØªØ§Ù† ØªÙˆ Ø§Ø³Øª",fontSize=17.sp,fontWeight=FontWeight.ExtraBold,color=Gold)
      Spacer(Modifier.height(4.dp))
      Text(
       if(allToday.isEmpty())"Ø¨Ø±Ø§ÛŒ Ø§Ù…Ø±ÙˆØ² Ù‡Ù†ÙˆØ² Ø¨Ø±Ù†Ø§Ù…Ù‡â€ŒØ§ÙŠ Ø³Ø§Ø®ØªÛŒ. Ø§Ø² Â«Ø±Ù†Ø§Ù…Ø‡ Ù…Ù†#Š0ƒn3bœƒŠ0ƒfb¿fbŸfƒbŸfbÇf#bÈƒbÓbÇf#bäƒj§ff¸ˆ(€€€€€€•±Í”¥˜¡ÁÉ½É•ÍÌôôÄÀÀ¤‹fn3bÇbŸfƒbŸfbÇf#bÈƒj§bŸffƒbÓb¿fblƒfb«n3b³fƒbÇbœƒb¯b£b¨ƒj§fƒf ƒb£bÇbŸn0ƒfbÇb¿bœƒbÛ˜‚â ¢VÇ6R-˜‹˜-‹r‹˜¸Â˜-Šý˜RŠ‹Šý˜¢Š­˜]‹šý‹"ª˜c²Š‹Š}˜mŠ}˜]˜r˜-‹Š}‹˜m¸Í‹=Š¢ŠÍŠ}˜¢Š­˜‚Š­‹]˜]¸Í˜RŠšý¸Í‹Šòâ"À¢6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÀ¢föçE6—¦SÓ"ç7 ¢¢–b†W‡Vç6W3ã—°¢76W"„ÖöF–f–W"æ†V–v‡BƒræG’¢FW‡B‚-Ší‹ŠÂŠ½ŠŠ®(Í‹MŠý˜s¢"¶ÖöæW’†W‡Vç6W2’ÆföçE6—¦SÓç7¢Ð¢Ð¢Ð¢Ð ¢&÷GFöÔæb†7W'&VçCÕ67&VVâä„ôÔRÆöä÷VãÖöä÷Vâ¢Ð¢Ð§Ð ¤6ö×÷6&ÆP§&—fFRgVâ†öÖT6FVv÷'•F–ÆR†3¤F6‚Æöä÷Vã¢…67&VVâ’ÓåVæ—B—°¢fÂF&³ÔÆö6ÄF&´ÖöFRæ7W'&Vç@¢&÷‚€¢ÖöF–f–W"æf–ÆÄÖ…v–GF‚‚’æ†V–v‡Bƒ“bæG¢ç6†F÷rƒbæGÅ&÷VæFVD6÷&æW%6†RƒbæG’¢æ6Æ—…&÷VæFVD6÷&æW%6†RƒbæG’¢æ&6¶w&÷VæB€¢''W6‚çfW'F–6Äw&F–VçB€¢–b†F&²’Æ—7Döb„6öÆ÷"ƒ„Sƒ#s#’Ä6öÆ÷"ƒ„TScƒB’¢VÇ6RÆ—7Döb„6öÆ÷"ƒ„c”dddddb’Ä6öÆ÷"ƒ„TdctcDB’¢¢¢æ&÷&FW"ƒæGÄvöÆBæ6÷’‚ãsVb’Å&÷VæFVD6÷&æW%6†RƒbæG’¢æ6Æ–6¶&ÆW¶öä÷Vâ†2ç67&VVâ—Ð¢çFF–ærƒbæG’À¢6öçFVçDÆ–væÖVçCÔÆ–væÖVçBä6VçFW ¢—°¢6öÇVÖâ††÷&—¦öçFÄÆ–væÖVçCÔÆ–væÖVçBä6VçFW$†÷&—¦öçFÆÇ’—°¢&÷‚€¢ÖöF–f–W"ç6—¦RƒC"æG’æ6Æ—…&÷VæFVD6÷&æW%6†Rƒ2æG’¢æ&6¶w&÷VæB„''W6‚ç&F–Äw&F–VçB†Æ—7Döb†2æ3"æ6÷’‚ã“Vb’Æ2æ3æ6÷’‚ã“&b’Ä6öÆ÷"ä&Æ6²’’¢æ&÷&FW"ƒæGÄvöÆBæ6÷’‚ãSVb’Å&÷VæFVD6÷&æW%6†Rƒ2æG’’À¢6öçFVçDÆ–væÖVçCÔÆ–væÖVçBä6VçFW ¢°¢FW‡B†2æ–6öâÆföçE6—¦SÓ#ç7Æ6öÆ÷#Ô6öÆ÷"åv†—FRÆföçEvV–v‡CÔföçEvV–v‡BäW‡G&&öÆB¢Ð¢76W"„ÖöF–f–W"æ†V–v‡BƒRæG’¢FW‡B†2çF—FÆRÆföçE6—¦SÓ’ãRç7ÆföçEvV–v‡CÔföçEvV–v‡BäW‡G&&öÆBÆÖ„Æ–æW3ÓÇFW‡DÆ–vãÕFW‡DÆ–vâä6VçFW"¢Ð¢Ð§Ð ¤6ö×÷6&ÆP¦gVâ&÷GFöÔæb†7W'&VçC¥67&VVâÆöä÷Vã¢…67&VVâ’ÓåVæ—B—°¢fÂæd—FV×3ÖÆ—7Döb€¢G&—ÆR‚.(È""Â-ŠíŠ}˜m˜r"Å67&VVâä„ôÔR’À¢G&—ÆR‚.)É2"Â-Š‹˜mŠ}˜]˜r˜]˜b"Å67&VVâå$õUD”äR’À¢G&—ÆR‚.)jR"Â-ªý‹-Š}‹‹N(Í˜}Šr"Å67&VVâå$Uõ%E2’À¢G&—ÆR‚.)jB"Â-ªŠ}Š­Š}ŠŠíŠ}˜m˜r"Å67&VVâäÄ”%$%’’À¢G&—ÆR‚.)ª"Â-Š­˜m‹¸Í˜]Š}Š¢"Å67&VVâä44õTåB¢¢fÂF&³ÔÆö6ÄF&´ÖöFRæ7W'&Vç@¢7W&f6R€¢6öÆ÷#Ö–b†F&²”6öÆ÷"ƒ„TS#S2’VÇ6R6öÆ÷"ƒ„ctddddddb’À¢6†F÷tVÆWfF–öãÓBæGÀ¢FöæÄVÆWfF–öãÓBæG ¢—°¢&÷r€¢ÖöF–f–W"æf–ÆÄÖ…v–GF‚‚’ææf–vF–öä&'5FF–ær‚’æ†V–v‡BƒcbæG’çFF–ær††÷&—¦öçFÃÓ2æG’À¢fW'F–6ÄÆ–væÖVçCÔÆ–væÖVçBä6VçFW%fW'F–6ÆÇ¢’°¢æd—FV×2æf÷$V6‡²†–6öâÆÆ&VÂÇF&vWB’Óà¢fÂ6VÆV7FVCÖ7W'&VçCÓ×F&vW@¢6öÇVÖâ€¢ÖöF–f–W"çvV–v‡Bƒb’æf–ÆÄÖ„†V–v‡B‚’æ6Æ–6¶&ÆW¶öä÷Vâ‡F&vWB—ÒçFF–ær‡fW'F–6ÃÓræG’À¢†÷&—¦öçFÄÆ–væÖVçCÔÆ–væÖVçBä6VçFW$†÷&—¦öçFÆÇ’À¢fW'F–6Ä'&ævVÖVçCÔ'&ævVÖVçBä6VçFW ¢—°¢FW‡B†–6öâÆ6öÆ÷#Ö–b‡6VÆV7FVB”vöÆBVÇ6RÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓ‚ç7ÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆB¢FW‡B†Æ&VÂÆ6öÆ÷#Ö–b‡6VÆV7FVB”vöÆBVÇ6RÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓ’ç7ÆföçEvV–v‡CÖ–b‡6VÆV7FVB”föçEvV–v‡Bä&öÆBVÇ6RföçEvV–v‡BäÖVF—VÒ¢Ð¢Ð¢Ð§Ð§Ð ¤6ö×÷6&ÆP¦gVâ6†V6¶Æ—7E67&VVâ‡7F÷&S¤Æ–fU7F÷&RÇF—FÆS¥7G&–ærÆw&÷W¥7G&–ærÆöä&6³¢‚’ÓåVæ—BÆöä6†ævS¢‚’ÓåVæ—B—°¢f"—FVÔÆ—7B'’&VÖVÖ&W'¶×WF&ÆU7FFTöb‡7F÷&Ræ—FV×2†w&÷W’—Ð¢f"FW‡B'’&VÖVÖ&W'¶×WF&ÆU7FFTöb‚""—Ð¢vR‡F—FÆRÆöä&6²—°¢6V7F–öåF—FÆR€¢–b†w&÷WÓÒ'&÷WF–æR"’-‹˜Š­˜­˜b‹ŠrŠí˜ŠýŠ¢Š‹=Š}‹""VÇ6R-˜M¸Í‹=Š¢˜]˜b"À¢–b†w&÷WÓÒ'&÷WF–æR"’-˜}¸ÍŠiÒ‹˜Š­¸Í˜mˆÂŠ}‹"˜-Š˜BŠ‹Š}˜­‰²Š­‹˜­˜Í˜b˜m‹MŠý˜}˜NÈÂŠ­ŠÍŠ­Š˜Â˜‚˜]ŠÝŠ­˜Š}¸ÂŠ}˜b‹]˜ŠÝ˜r˜˜-‹rŠý‹=Š¢Ší˜ŠýŠ¢Š}‹=Š¢â ¢VÇ6R-˜}‹˜]˜‹Šò‹ŠrŠ­¸Íª’Š‹-˜m‰²Š}‹}˜MŠ}‹Š}Š¢Š‹ŠòŠ}‹"Š‹=Š­˜bŠ‹˜mŠ}˜M˜r˜]¸Î(Í˜]Š}˜mŠòâ ¢¢fÂFöæSÖ—FVÔÆ—7Bæ6÷VçG¶—BæFöæWÐ¢–b†—FVÔÆ—7Bæ—4æ÷DV×G’‚’—°¢vÆ746&G°¢Æ–æV%&öw&W74–æF–6F÷"‡&öw&W73×¶FöæRçFôfÆöB‚’ö—FVÔÆ—7Bç6—¦WÒÆÖöF–f–W#ÔÖöF–f–W"æf–ÆÄÖ…v–GF‚‚’æ†V–v‡Bƒ’æG’æ6Æ—„6—&6ÆU6†R’Æ6öÆ÷#ÔvöÆBÇG&6´6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRç7W&f6Uf&–çB¢FW‡B†FöæRçFõ7G&–ær‚’²"Š}‹""¶—FVÔÆ—7Bç6—¦R²"Š}˜mŠÍŠ}˜R‹MŠý˜r"Æ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓç7ÆÖöF–f–W#ÔÖöF–f–W"çFF–ær‡F÷ÓbæG’¢Ð¢76W"„ÖöF–f–W"æ†V–v‡Bƒ‚æG’¢Ð¢&÷r‡fW'F–6ÄÆ–væÖVçCÔÆ–væÖVçBä6VçFW%fW'F–6ÆÇ’—°¢÷WFÆ–æVEFW‡Df–VÆB€¢FW‡BÇ·FW‡CÖ—GÒÀ¢Æ&VÃ×µFW‡B†–b†w&÷WÓÒ'&÷WF–æR"’-ªŠ}‹¸ÍŠr‹˜Š­¸Í˜bŠÍŠý¸ÍŠò"VÇ6R-˜]˜‹ŠòŠÍŠý¸ÍŠò"—ÒÀ¢ÖöF–f–W#ÔÖöF–f–W"çvV–v‡Bƒb’À¢6–ævÆTÆ–æS×G'VP¢¢76W"„ÖöF–f–W"çv–GF‚ƒ‚æG’¢'WGFöâ€¢öä6Æ–6³×°¢–b‡FW‡Bæ—4æ÷D&Ææ²‚’—°¢7F÷&RæFD—FVÒ…6–×ÆT—FVÒ‡F—FÆS×FW‡BçG&–Ò‚’Æw&÷WÖw&÷W’“·FW‡CÒ"#¶—FVÔÆ—7C×7F÷&Ræ—FV×2†w&÷W“¶öä6†ævR‚¢Ð¢ÒÀ¢6öÆ÷'3Ô'WGFöäFVfVÇG2æ'WGFöä6öÆ÷'2†6öçF–æW$6öÆ÷#ÔvöÆBÆ6öçFVçD6öÆ÷#Ô–æ²’À¢6†SÕ&÷VæFVD6÷&æW%6†RƒBæG¢—µFW‡B‚"²"ÆföçE6—¦SÓ#Bç7—Ð¢Ð¢76W"„ÖöF–f–W"æ†V–v‡BƒæG’¢–b†—FVÔÆ—7Bæ—4V×G’‚’—°¢vÆ746&G´V×G”†–çB†–b†w&÷WÓÒ'&÷WF–æR"’-Š}¸Í˜b‹]˜ŠÝ˜r‹˜]ŠýŠ}˜²ŠíŠ}˜M¸ÂŠ}‹=Š¢âŠ}˜˜M¸Í˜b‹˜Š­¸Í˜mŠ¢‹ŠrŠí˜ŠýŠ¢Š}‹mŠ}˜˜rª˜bâ"VÇ6R-˜}˜m˜‹"˜]˜‹Šý¸ÂŠ½ŠŠ¢˜m‹MŠý˜râ"—Ð¢Ð¢Æ§”6öÇVÖâ‡fW'F–6Ä'&ævVÖVçCÔ'&ævVÖVçBç76VD'’ƒ‚æG’Æ6öçFVçEFF–æsÕFF–æufÇVW2†&÷GFöÓÓ#æG’—°¢—FV×2†—FVÔÆ—7BÆ¶W“×¶—Bæ–GÒ—·‚Óà¢vÆ746&G°¢&÷r‡fW'F–6ÄÆ–væÖVçCÔÆ–væÖVçBä6VçFW%fW'F–6ÆÇ’—°¢6†V6¶&÷‚‡‚æFöæRÇ·7F÷&RçFövvÆT—FVÒ‡‚æ–B“¶—FVÔÆ—7C×7F÷&Ræ—FV×2†w&÷W“¶öä6†ævR‚—ÒÆ6öÆ÷'3Ô6†V6¶&÷„FVfVÇG2æ6öÆ÷'2†6†V6¶VD6öÆ÷#ÔvöÆBÆ6†V6¶Ö&´6öÆ÷#Ô–æ²’¢6öÇVÖâ„ÖöF–f–W"çvV–v‡Bƒb’—°¢FW‡B‡‚çF—FÆRÆföçEvV–v‡CÖ–b‡‚æFöæR”föçEvV–v‡Bäæ÷&ÖÂVÇ6RföçEvV–v‡Bä&öÆB¢FW‡B‡‚æFFRÆ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓ’ç7¢Ð¢FW‡D'WGFöâ†öä6Æ–6³×·7F÷&RæFVÆWFT—FVÒ‡‚æ–B“¶—FVÔÆ—7C×7F÷&Ræ—FV×2†w&÷W“¶öä6†ævR‚—Ò—µFW‡B‚-ŠÝ‹˜"Æ6öÆ÷#ÔFævW"ÆföçE6—¦SÓç7—Ð¢Ð¢Ð¢Ð¢Ð¢Ð§Ð ¤6ö×÷6&ÆP¦gVâ¦÷W&æÅ67&VVâ‡7F÷&S¤Æ–fU7F÷&RÇF—FÆS¥7G&–ærÆ¶–æC¥7G&–ærÇ&ö×C¥7G&–ærÆöä&6³¢‚’ÓåVæ—BÆöä6†ævS¢‚’ÓåVæ—B—°¢f"FW‡B'’&VÖVÖ&W'¶×WF&ÆU7FFTöb‚""—Ð¢f"¦÷W&æÄÆ—7B'’&VÖVÖ&W'¶×WF&ÆU7FFTöb‡7F÷&Ræ¦÷W&æÇ2†¶–æB’—Ð¢vR‡F—FÆRÆöä&6²—°¢6V7F–öåF—FÆR‡&ö×BÂ-ª˜Š­Š}˜r˜}˜RŠ˜m˜¸Í‹=¸ÂªŠ}˜¸ÂŠ}‹=Š­‰²˜]˜}˜RŠ­ŠýŠ}˜˜R˜‚‹]ŠýŠ}˜-Š¢ŠŠrŠí˜ŠýŠ¢Š}‹=Š¢â"¢vÆ746&G°¢÷WFÆ–æVEFW‡Df–VÆB‡FW‡BÇ·FW‡CÖ—GÒÆÖöF–f–W#ÔÖöF–f–W"æf–ÆÄÖ…v–GF‚‚’æ†V–v‡Bƒ3æG’ÇÆ6V†öÆFW#×µFW‡B‚-Š}¸Í˜mŠÍŠrŠ˜m˜¸Í‹>(
b"—Ò¢76W"„ÖöF–f–W"æ†V–v‡Bƒ‚æG’¢&–Ö'”'WGFöâ‚-Š½ŠŠ¢"—°¢–b‡FW‡Bæ—4æ÷D&Ææ²‚’—°¢7F÷&RæFD¦÷W&æÂ„¦÷W&æÄVçG'’†¶–æCÖ¶–æBÇFW‡C×FW‡BçG&–Ò‚’’“·FW‡CÒ"#¶¦÷W&æÄÆ—7C×7F÷&Ræ¦÷W&æÇ2†¶–æB“¶öä6†ævR‚¢Ð¢Ð¢Ð¢6V7F–öåF—FÆR‚-Š­Š}‹¸ÍŠí¨m˜r"¢Æ§”6öÇVÖâ‡fW'F–6Ä'&ævVÖVçCÔ'&ævVÖVçBç76VD'’ƒ‚æG’Æ6öçFVçEFF–æsÕFF–æufÇVW2†&÷GFöÓÓ#æG’—°¢—FV×2†¦÷W&æÄÆ—7BÆ¶W“×¶—Bæ–GÒ—·‚Óà¢vÆ746&G°¢FW‡B‡‚æFFRÆ6öÆ÷#ÔvöÆBÆföçE6—¦SÓç7ÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆB¢76W"„ÖöF–f–W"æ†V–v‡BƒRæG’¢FW‡B‡‚çFW‡B¢FW‡D'WGFöâ†öä6Æ–6³×·7F÷&RæFVÆWFT¦÷W&æÂ‡‚æ–B“¶¦÷W&æÄÆ—7C×7F÷&Ræ¦÷W&æÇ2†¶–æB“¶öä6†ævR‚—ÒÆÖöF–f–W#ÔÖöF–f–W"æÆ–vâ„Æ–væÖVçBäVæB’—µFW‡B‚-ŠÝ‹˜"Æ6öÆ÷#ÔFævW"—Ð¢Ð¢Ð¢Ð¢Ð§Ð ¤6ö×÷6&ÆP¦gVâw&÷wF„‡V%67&VVâ‡7F÷&S¤Æ–fU7F÷&RÆöä&6³¢‚’ÓåVæ—BÆöä6†ævS¢‚’ÓåVæ—B—°¢vR‚-‹‹MŠò˜‹Šý¸Â"Æöä&6²—°¢Æ§”6öÇVÖâ‡fW'F–6Ä'&ævVÖVçCÔ'&ævVÖVçBç76VD'’ƒæG’Æ6öçFVçEFF–æsÕFF–æufÇVW2†&÷GFöÓÓ#RæG’—°¢—FV×°¢&÷‚„ÖöF–f–W"æf–ÆÄÖ…v–GF‚‚’æ†V–v‡BƒCRæG’æ6Æ—…&÷VæFVD6÷&æW%6†Rƒ#"æG’’æ&÷&FW"ƒãRæGÄvöÆBÅ&÷VæFVD6÷&æW%6†Rƒ#"æG’’—°¢–ÖvR‡–çFW%&W6÷W&6R…"æG&v&ÆRæw&÷wF…ö6&B’ÆçVÆÂÆ6öçFVçE66ÆSÔ6öçFVçE66ÆRä7&÷ÆÖöF–f–W#ÔÖöF–f–W"æf–ÆÄÖ…6—¦R‚’¢&÷‚„ÖöF–f–W"æf–ÆÄÖ…6—¦R‚’æ&6¶w&÷VæB„''W6‚çfW'F–6Äw&F–VçB†Æ—7Döb„6öÆ÷"åG&ç7&VçBÄ6öÆ÷"ä&Æ6²æ6÷’‚ãƒ&b’’’’¢6öÇVÖâ„ÖöF–f–W"æf–ÆÄÖ…6—¦R‚’çFF–ærƒBæG’ÇfW'F–6Ä'&ævVÖVçCÔ'&ævVÖVçBä&÷GFöÒ—°¢FW‡B‚-‹‹MŠò˜‹Šý¸Â"Æ6öÆ÷#Ô6öÆ÷"åv†—FRÆföçEvV–v‡CÔföçEvV–v‡BäW‡G&&öÆBÆföçE6—¦SÓ#"ç7¢FW‡B‚-Š}‹Š­˜]Š}ŠòŠ˜r˜m˜‹2(
"‹Š}ŠýŠ®(Í˜}Šr(
"Š}˜mªý¸Í‹-˜r(
"˜]Šý¸Í‹¸ÍŠ¢‹-˜]Š}˜b"Æ6öÆ÷#Ô6öÆ÷"åv†—FRæ6÷’‚ã–b’ÆföçE6—¦SÓç7¢Ð¢Ð¢Ð¢—FV×°¢6V7F–öåF—FÆR‚-˜-Šý‹Š¢˜]˜b"¢vÆ746&G°¢FW‡B‚-Š}‹Š­˜]Š}ŠòŠ˜r˜m˜‹2"ÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆB¢FW‡B‚-˜-˜˜BŠ˜rŠí˜ŠýŠ­ˆÂŠ}˜-ŠýŠ}˜RŠŠr˜ŠÍ˜ŠòŠ­‹‹=ˆÂŠŠ}˜mª’‹M˜Š}˜}Šò˜‚˜m‹ŠýŠŠ}˜b˜]˜Š}ŠÍ˜}˜râ"Æ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓ"ç7¢Ð¢Ð¢—FV×°¢vÆ746&G°¢FW‡B‚-Š-‹-˜]Š}¸Í‹MªýŠ}˜r‹‹MŠò"ÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆB¢FW‡B‚-Š}Š­˜Š}˜"(i"˜Š}ª˜m‹B(i"˜]ŠÝ‹ª’(i"˜mŠ­¸ÍŠÍ˜r(i"›íŠ}‹=ŠâŠ˜}Š­‹Šý˜‹˜rŠ‹Šò"Æ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓ"ç7¢Ð¢Ð¢—FV×°¢vÆ746&G°¢FW‡B‚-Šý˜Š­‹‹‹MŠò"ÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆB¢FW‡B‚-Šý‹‹>(Í˜}Š}¸Â‹˜‹-Š}˜m˜r˜‚Š}˜Mªý˜˜}Š}¸ÂŠ­ª‹Š}‹‹M˜˜mŠý˜r‹ŠrŠ½ŠŠ¢ª˜bâ"Æ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çBÆföçE6—¦SÓ"ç7¢76W"„ÖöF–f–W"æ†V–v‡BƒræG’¢FW‡B‚-Š­‹ŠýŠ}Šò¸ÍŠ}ŠýŠýŠ}‹MŠ®(Í˜}Š}¸Â‹‹MŠó¢"·7F÷&Ræ¦÷W&æÇ2‚&w&÷wF‚"’ç6—¦RÆ6öÆ÷#ÔvöÆBÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆB¢Ð¢Ð¢Ð¢Ð§Ð ¤6ö×÷6&ÆP¦gVâÆ–'&'•67&VVâ‡7F÷&S¤Æ–fU7F÷&RÆöä&6³¢‚’ÓåVæ—BÆöä6†ævS¢‚’ÓåVæ—B—°¢vR‚-ªŠ­Š}ŠŠíŠ}˜m˜r"Æöä&6²—°¢Æ§”6öÇVÖâ‡fW'F–6Ä'&ævVÖVçCÔ'&ævVÖVçBç76VD'’ƒ’æG’Æ6öçFVçEFF–æsÕFF–æufÇVW2†&÷GFöÓÓ#RæG’—°¢—FV×°¢vöÆD6&G°¢FW‡B‚-ªŠ­Š}ŠŠíŠ}˜m˜r‹MŠí‹]¸Â˜]Š­Šò˜]ŠÝ˜]Šò"ÆföçEvV–v‡CÔföçEvV–v‡BäW‡G&&öÆBÆföçE6—¦SÓ’ç7¢FW‡B‚-Š-˜]˜‹-‹N(Í˜}Šr˜‚Š­˜]‹¸Í˜n(Í˜}ŠrŠ‹Š}‹=Š}‹2˜]˜‹m˜‹’ŠÍŠýŠr˜mªý˜rŠýŠ}‹MŠ­˜r˜]¸Î(Í‹M˜˜mŠòâ"Æ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çB¢Ð¢Ð¢—FV×2†Æ—7Döb€¢-˜-Š}˜m˜˜bŠÍ‹Š‚"Fò-Š}‹=Š­Š}Šý˜}Š}ˆÂŠ­˜]‹¸Í˜n(Í˜}Š}ˆÂ›í‹˜©˜rŠí˜Š}‹=Š­˜~(Í˜}Šr˜‚Šý˜Š­‹‹M˜Š}˜}Šò"À¢-˜-Šý‹Š¢‹˜Š}˜m¸Â"Fò-˜]‹‹-Š˜mŠý¸ÍˆÂ˜-Š}‹}‹¸ÍŠ­ˆÂ˜]‹Š}ª‹˜r˜‚ŠýˆŠ}‹’Šý‹Š‹Š}Š‹Šý‹=Š­ªŠ}‹¸Â"À¢-‹‹MŠò˜‹Šý¸Â"Fò-Š}‹Š­˜]Š}ŠòŠ˜r˜m˜‹=ˆÂ‹Š}ŠýŠ­ˆÂŠ­˜]‹ª‹"˜‚˜]Šý¸Í‹¸ÍŠ¢‹-˜]Š}˜b"À¢-˜]Š}˜M¸Â"Fò-Š˜ŠýŠÍ˜}ˆÂŠŠý˜}¸ÍˆÂ‹=‹˜]Š}¸Í˜~(Íªý‹Š}‹¸Â˜‚Š-‹-Š}Šý¸Â˜]Š}˜M¸Â"À¢-˜]‹˜m˜¸ÍŠ¢"Fò-˜]˜b˜‚ŠíŠýŠ}ˆÂ‹Mª‹ªý‹-Š}‹¸Â˜‚˜]‹=¸Í‹˜}Š}¸Â˜]‹˜m˜¸Â‹MŠí‹]¸Â ¢’—²†Æ"’Óà¢vÆ746&G°¢FW‡B†ÆföçEvV–v‡CÔföçEvV–v‡Bä&öÆBÆ6öÆ÷#ÔvöÆB¢FW‡B†"ÆföçE6—¦SÓ"ç7Æ6öÆ÷#ÔÖFW&–ÅF†VÖRæ6öÆ÷%66†VÖRæöå7W&f6Uf&–çB¢Ð¢Ð¢Ð¢Ð§Ð 