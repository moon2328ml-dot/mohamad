package ir.mohammad.lifeos

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.view.WindowCompat
import android.graphics.Color as AndroidColor

enum class Screen {
 HOME, ROUTINE, DAILY_GOALS, YEARLY_GOALS, LONG_GOALS, FINANCE, DEBTS, BODY,
 GRATITUDE, GROWTH, MEMORIES, MISTAKES, RELATIONSHIP, ATTRACTION, POWER,
 SPIRITUAL, ANALYSIS, REPORTS, REMINDERS, ACCOUNT, INVESTMENT, LIBRARY
}

class MainActivity:ComponentActivity(){
 private val notificationPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){}
 override fun onCreate(savedInstanceState:Bundle?){
  super.onCreate(savedInstanceState)
  if(Build.VERSION.SDK_INT>=33)notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
  setContent{
   CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl){
    val store=remember{LifeStore(this)}
    LaunchedEffect(Unit){store.seedDefaultsIfNeeded()}
    var version by remember{mutableIntStateOf(0)}
    var dark by remember{mutableStateOf(store.darkMode)}
    var screen by remember{mutableStateOf(if(store.profileReady())Screen.HOME else Screen.ACCOUNT)}
    val back={screen=Screen.HOME}
    LaunchedEffect(dark){
     window.statusBarColor=if(dark) AndroidColor.rgb(5,7,12) else AndroidColor.WHITE
     window.navigationBarColor=if(dark) AndroidColor.rgb(5,7,12) else AndroidColor.WHITE
     WindowCompat.getInsetsController(window,window.decorView).isAppearanceLightStatusBars=!dark
     WindowCompat.getInsetsController(window,window.decorView).isAppearanceLightNavigationBars=!dark
    }
    LifeTheme(dark=dark){
     when(screen){
      Screen.HOME->HomeScreen(
       store=store,
       version=version,
       dark=dark,
       onToggleDark={dark=!dark;store.darkMode=dark},
       onOpen={screen=it}
      )
      Screen.ROUTINE->RoutineScreen(store,back){version++}
      Screen.DAILY_GOALS->ChecklistScreen(store,"اهداف امروز","daily_goal",back){version++}
      Screen.YEARLY_GOALS->ChecklistScreen(store,"اهداف سالانه","year_goal",back){version++}
      Screen.LONG_GOALS->ChecklistScreen(store,"اهداف بلندمدت","long_goal",back){version++}
      Screen.FINANCE->FinanceScreen(store,back){version++}
      Screen.DEBTS->DebtScreen(store,back){version++}
      Screen.BODY->BodyScreen(store,back){version++}
      Screen.GRATITUDE->JournalScreen(store,"دفتر شکرگزاری","gratitude","امروز بابت چه چیزهایی شکرگزارم؟",back){version++}
      Screen.GROWTH->GrowthHubScreen(store,back){version++}
      Screen.MEMORIES->JournalScreen(store,"خاطرات","memory","خاطره امروز یا اتفاق مهم را بنویس…",back){version++}
      Screen.MISTAKES->JournalScreen(store,"آزمایشگاه رشد","mistake","چه اتفاقی افتاد، من چه واکنشی داشتم و دفعه بعد چه بهتر می‌کنم؟",back){version++}
      Screen.RELATIONSHIP->RelationshipScreen(store,back){version++}
      Screen.ATTRACTION->AttractionScreen(store,back){version++}
      Screen.POWER->PowerScreen(store,back){version++}
      Screen.SPIRITUAL->SpiritualScreen(store,back){version++}
      Screen.ANALYSIS->AnalysisScreen(store,back){version++}
      Screen.REPORTS->ReportsScreen(store,back)
      Screen.REMINDERS->ReminderScreen(this,back)
      Screen.ACCOUNT->AccountScreen(store,dark,{dark=!dark;store.darkMode=dark},onDone={version++;screen=Screen.HOME},onBack=if(store.profileReady())back else null)
      Screen.INVESTMENT->InvestmentScreen(store,back){version++}
      Screen.LIBRARY->LibraryScreen(store,back){version++}
     }
    }
   }
  }
 }
}
