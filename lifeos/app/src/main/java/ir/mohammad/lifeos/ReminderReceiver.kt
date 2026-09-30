package ir.mohammad.lifeos

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Calendar

class ReminderReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  val title=intent.getStringExtra("title")?:"متد محمد"
  val text=intent.getStringExtra("text")?:"وقت رسیدگی به مسیر امروزته."
  val nm=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(NotificationChannel("lifeos","یادآورهای متد محمد",NotificationManager.IMPORTANCE_DEFAULT))
  val n=NotificationCompat.Builder(context,"lifeos").setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(text).setAutoCancel(true).build()
  nm.notify(intent.getIntExtra("id",100),n)
 }
 companion object{
  fun scheduleDaily(context:Context,id:Int,hour:Int,minute:Int,title:String,text:String){
   val i=Intent(context,ReminderReceiver::class.java).putExtra("id",id).putExtra("title",title).putExtra("text",text)
   val pi=PendingIntent.getBroadcast(context,id,i,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
   val c=Calendar.getInstance().apply{set(Calendar.HOUR_OF_DAY,hour);set(Calendar.MINUTE,minute);set(Calendar.SECOND,0);if(timeInMillis<System.currentTimeMillis())add(Calendar.DAY_OF_YEAR,1)}
   val am=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
   am.setInexactRepeating(AlarmManager.RTC_WAKEUP,c.timeInMillis,AlarmManager.INTERVAL_DAY,pi)
  }
 }
}
