package ir.mohammad.lifeos

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

data class SimpleItem(val id:String=UUID.randomUUID().toString(),val title:String,val group:String,val done:Boolean=false,val value:Double=0.0,val date:String=LocalDate.now().toString())
data class MoneyEntry(val id:String=UUID.randomUUID().toString(),val kind:String,val category:String,val amount:Long,val note:String="",val date:String=LocalDate.now().toString())
data class WeightEntry(val id:String=UUID.randomUUID().toString(),val weight:Double,val date:String=LocalDate.now().toString())
data class FoodEntry(val id:String=UUID.randomUUID().toString(),val name:String,val calories:Int,val protein:Int,val date:String=LocalDate.now().toString())
data class DebtEntry(val id:String=UUID.randomUUID().toString(),val title:String,val owner:String,val type:String,val total:Long,val paid:Long,val monthly:Long,val dueDay:Int)
data class JournalEntry(val id:String=UUID.randomUUID().toString(),val kind:String,val text:String,val date:String=LocalDate.now().toString())
data class CheckIn(val id:String=UUID.randomUUID().toString(),val mood:Int,val discipline:Int,val confidence:Int,val angerControl:Int,val loneliness:Int,val focus:Int,val date:String=LocalDate.now().toString())
data class RelationshipEntry(val id:String=UUID.randomUUID().toString(),val type:String,val note:String,val amount:Long,val emotion:Int,val date:String=LocalDate.now().toString())

class LifeStore(context:Context){
 private val p=context.getSharedPreferences("mohammad_life_os",Context.MODE_PRIVATE)
 var name:String get()=p.getString("profile_name","")?:"" set(v)=p.edit().putString("profile_name",v).apply()
 var email:String get()=p.getString("profile_email","")?:"" set(v)=p.edit().putString("profile_email",v).apply()
 var targetWeight:Double get()=java.lang.Double.longBitsToDouble(p.getLong("target_weight",java.lang.Double.doubleToLongBits(75.0))) set(v)=p.edit().putLong("target_weight",java.lang.Double.doubleToLongBits(v)).apply()
 var monthlyIncome:Long get()=p.getLong("monthly_income",0L) set(v)=p.edit().putLong("monthly_income",v).apply()
 fun profileReady()=name.isNotBlank()

 fun loadItems():List<SimpleItem> =parse("items"){o->SimpleItem(o.optString("id"),o.optString("title"),o.optString("group"),o.optBoolean("done"),o.optDouble("value"),o.optString("date"))}
 fun saveItems(x:List<SimpleItem>)=save("items",x.map{JSONObject().apply{put("id",it.id);put("title",it.title);put("group",it.group);put("done",it.done);put("value",it.value);put("date",it.date)}})
 fun addItem(x:SimpleItem)=saveItems(loadItems()+x)
 fun toggleItem(id:String)=saveItems(loadItems().map{if(it.id==id)it.copy(done=!it.done)else it})
 fun deleteItem(id:String)=saveItems(loadItems().filterNot{it.id==id})
 fun items(group:String)=loadItems().filter{it.group==group}
 fun seedDefaultsIfNeeded(){
  if(p.getBoolean("seeded",false))return
  saveItems(listOf(
   SimpleItem(title="بیدار شدن و مرتب کردن تخت",group="routine"),
   SimpleItem(title="آب + صبحانه متعادل",group="routine"),
   SimpleItem(title="سه کار اصلی امروز",group="routine"),
   SimpleItem(title="۱۰ دقیقه شکرگزاری/تجسم",group="routine"),
   SimpleItem(title="ورزش یا پیاده‌روی",group="routine"),
   SimpleItem(title="مرور مالی شبانه",group="routine"),
   SimpleItem(title="عبادت/ذکر/دعا",group="routine"),
   SimpleItem(title="مرور روز و برنامه فردا",group="routine")
  ));p.edit().putBoolean("seeded",true).apply()
 }

 fun loadMoney():List<MoneyEntry> =parse("money"){o->MoneyEntry(o.optString("id"),o.optString("kind"),o.optString("category"),o.optLong("amount"),o.optString("note"),o.optString("date"))}
 fun saveMoney(x:List<MoneyEntry>)=save("money",x.map{JSONObject().apply{put("id",it.id);put("kind",it.kind);put("category",it.category);put("amount",it.amount);put("note",it.note);put("date",it.date)}})
 fun addMoney(x:MoneyEntry)=saveMoney(loadMoney()+x);fun deleteMoney(id:String)=saveMoney(loadMoney().filterNot{it.id==id})

 fun loadWeights():List<WeightEntry> =parse("weights"){o->WeightEntry(o.optString("id"),o.optDouble("weight"),o.optString("date"))}
 fun saveWeights(x:List<WeightEntry>)=save("weights",x.map{JSONObject().apply{put("id",it.id);put("weight",it.weight);put("date",it.date)}})
 fun addWeight(w:Double)=saveWeights(loadWeights()+WeightEntry(weight=w))

 fun loadFoods():List<FoodEntry> =parse("foods"){o->FoodEntry(o.optString("id"),o.optString("name"),o.optInt("calories"),o.optInt("protein"),o.optString("date"))}
 fun saveFoods(x:List<FoodEntry>)=save("foods",x.map{JSONObject().apply{put("id",it.id);put("name",it.name);put("calories",it.calories);put("protein",it.protein);put("date",it.date)}})
 fun addFood(x:FoodEntry)=saveFoods(loadFoods()+x);fun deleteFood(id:String)=saveFoods(loadFoods().filterNot{it.id==id})

 fun loadDebts():List<DebtEntry> =parse("debts"){o->DebtEntry(o.optString("id"),o.optString("title"),o.optString("owner"),o.optString("type"),o.optLong("total"),o.optLong("paid"),o.optLong("monthly"),o.optInt("dueDay"))}
 fun saveDebts(x:List<DebtEntry>)=save("debts",x.map{JSONObject().apply{put("id",it.id);put("title",it.title);put("owner",it.owner);put("type",it.type);put("total",it.total);put("paid",it.paid);put("monthly",it.monthly);put("dueDay",it.dueDay)}})
 fun addDebt(x:DebtEntry)=saveDebts(loadDebts()+x);fun deleteDebt(id:String)=saveDebts(loadDebts().filterNot{it.id==id})

 fun loadJournals():List<JournalEntry> =parse("journals"){o->JournalEntry(o.optString("id"),o.optString("kind"),o.optString("text"),o.optString("date"))}
 fun saveJournals(x:List<JournalEntry>)=save("journals",x.map{JSONObject().apply{put("id",it.id);put("kind",it.kind);put("text",it.text);put("date",it.date)}})
 fun addJournal(x:JournalEntry)=saveJournals(loadJournals()+x);fun journals(kind:String)=loadJournals().filter{it.kind==kind}.reversed();fun deleteJournal(id:String)=saveJournals(loadJournals().filterNot{it.id==id})

 fun loadCheckIns():List<CheckIn> =parse("checkins"){o->CheckIn(o.optString("id"),o.optInt("mood"),o.optInt("discipline"),o.optInt("confidence"),o.optInt("angerControl"),o.optInt("loneliness"),o.optInt("focus"),o.optString("date"))}
 fun saveCheckIns(x:List<CheckIn>)=save("checkins",x.map{JSONObject().apply{put("id",it.id);put("mood",it.mood);put("discipline",it.discipline);put("confidence",it.confidence);put("angerControl",it.angerControl);put("loneliness",it.loneliness);put("focus",it.focus);put("date",it.date)}})
 fun addCheckIn(x:CheckIn)=saveCheckIns(loadCheckIns()+x)

 fun loadRelationship():List<RelationshipEntry> =parse("relationship"){o->RelationshipEntry(o.optString("id"),o.optString("type"),o.optString("note"),o.optLong("amount"),o.optInt("emotion"),o.optString("date"))}
 fun saveRelationship(x:List<RelationshipEntry>)=save("relationship",x.map{JSONObject().apply{put("id",it.id);put("type",it.type);put("note",it.note);put("amount",it.amount);put("emotion",it.emotion);put("date",it.date)}})
 fun addRelationship(x:RelationshipEntry)=saveRelationship(loadRelationship()+x);fun deleteRelationship(id:String)=saveRelationship(loadRelationship().filterNot{it.id==id})

 private fun save(key:String,objs:List<JSONObject>){val a=JSONArray();objs.forEach{a.put(it)};p.edit().putString(key,a.toString()).apply()}
 private fun <T> parse(key:String,f:(JSONObject)->T):List<T>{val raw=p.getString(key,"[]")?:"[]";return try{val a=JSONArray(raw);buildList{for(i in 0 until a.length())add(f(a.getJSONObject(i)))}}catch(_:Exception){emptyList()}}

 fun exportSummary():String{
  val m=loadMoney();val inc=m.filter{it.kind=="income"}.sumOf{it.amount};val exp=m.filter{it.kind=="expense"}.sumOf{it.amount};val inv=m.filter{it.kind=="investment"}.sumOf{it.amount};val debt=loadDebts().sumOf{(it.total-it.paid).coerceAtLeast(0)};val r=items("routine");val w=loadWeights().lastOrNull()?.weight?.toString()?:"-"
  return "درآمد ثبت‌شده: "+inc+"\nمخارج: "+exp+"\nسرمایه‌گذاری: "+inv+"\nبدهی باقی‌مانده: "+debt+"\nروتین: "+r.count{it.done}+"/"+r.size+"\nوزن آخر: "+w
 }
}
