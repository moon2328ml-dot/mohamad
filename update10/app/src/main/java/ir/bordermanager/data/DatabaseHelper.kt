package ir.bordermanager.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ir.bordermanager.util.Jalali
import org.json.JSONArray
import org.json.JSONObject

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "border_manager.db"
        private const val DB_VERSION = 5
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE borders(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                created_at INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE owners(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                border_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                note TEXT NOT NULL DEFAULT '',
                color_seed INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(border_id) REFERENCES borders(id) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE cargo_types(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                origin TEXT NOT NULL DEFAULT '',
                iraqi_owner TEXT NOT NULL DEFAULT '',
                iraqi_broker TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL,
                FOREIGN KEY(owner_id) REFERENCES owners(id) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE trucks(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cargo_type_id INTEGER NOT NULL,
                plate TEXT NOT NULL,
                weight_kg REAL NOT NULL DEFAULT 0,
                size TEXT NOT NULL DEFAULT '',
                bundle_count INTEGER NOT NULL DEFAULT 0,
                phone TEXT NOT NULL DEFAULT '',
                driver_name TEXT NOT NULL DEFAULT '',
                driver_payment_account TEXT NOT NULL DEFAULT '',
                parking_queue INTEGER NOT NULL,
                declaration_type TEXT NOT NULL DEFAULT 'COTTAGE',
                cottage_number TEXT NOT NULL DEFAULT '',
                status TEXT NOT NULL DEFAULT 'PARKING',
                created_at INTEGER NOT NULL,
                called_at INTEGER,
                completed_at INTEGER,
                FOREIGN KEY(cargo_type_id) REFERENCES cargo_types(id) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX idx_truck_queue ON trucks(parking_queue)")
        db.execSQL("CREATE INDEX idx_truck_created ON trucks(created_at)")
        db.execSQL("CREATE INDEX idx_truck_status ON trucks(status)")
        db.execSQL("""
            CREATE TABLE call_batches(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                border_id INTEGER,
                threshold INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(border_id) REFERENCES borders(id) ON DELETE SET NULL
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE call_entries(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                call_batch_id INTEGER NOT NULL,
                truck_id INTEGER,
                plate_snapshot TEXT NOT NULL,
                queue_snapshot INTEGER NOT NULL,
                owner_snapshot TEXT NOT NULL DEFAULT '',
                missed INTEGER NOT NULL DEFAULT 0,
                entered_at INTEGER,
                FOREIGN KEY(call_batch_id) REFERENCES call_batches(id) ON DELETE CASCADE,
                FOREIGN KEY(truck_id) REFERENCES trucks(id) ON DELETE SET NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX idx_call_batches_created ON call_batches(created_at)")
        db.execSQL("CREATE INDEX idx_call_entries_batch ON call_entries(call_batch_id)")
        db.execSQL("""
            CREATE TABLE accounting(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_id INTEGER NOT NULL,
                jalali_year INTEGER NOT NULL,
                jalali_month INTEGER NOT NULL,
                clearance_fee INTEGER NOT NULL DEFAULT 0,
                declaration_fee INTEGER NOT NULL DEFAULT 0,
                freight_fee INTEGER NOT NULL DEFAULT 0,
                crane_fee INTEGER NOT NULL DEFAULT 0,
                misc_fee INTEGER NOT NULL DEFAULT 0,
                paid_amount INTEGER NOT NULL DEFAULT 0,
                note TEXT NOT NULL DEFAULT '',
                invoice_no TEXT NOT NULL DEFAULT '',
                updated_at INTEGER NOT NULL,
                UNIQUE(owner_id, jalali_year, jalali_month),
                FOREIGN KEY(owner_id) REFERENCES owners(id) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE truck_accounting(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                truck_id INTEGER NOT NULL,
                jalali_year INTEGER NOT NULL,
                jalali_month INTEGER NOT NULL,
                clearance_fee INTEGER NOT NULL DEFAULT 0,
                declaration_fee INTEGER NOT NULL DEFAULT 0,
                freight_fee INTEGER NOT NULL DEFAULT 0,
                crane_fee INTEGER NOT NULL DEFAULT 0,
                misc_fee INTEGER NOT NULL DEFAULT 0,
                paid_amount INTEGER NOT NULL DEFAULT 0,
                note TEXT NOT NULL DEFAULT '',
                updated_at INTEGER NOT NULL,
                UNIQUE(truck_id, jalali_year, jalali_month),
                FOREIGN KEY(truck_id) REFERENCES trucks(id) ON DELETE CASCADE
            )
        """.trimIndent())
        seed(db)
    }

    private fun seed(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        db.insert("borders", null, ContentValues().apply {
            put("name", "مرز خسروی")
            put("created_at", now)
        })
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("UPDATE trucks SET status='UNLOADED' WHERE completed_at IS NOT NULL")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE trucks ADD COLUMN declaration_type TEXT NOT NULL DEFAULT 'COTTAGE'")
            db.execSQL("ALTER TABLE trucks ADD COLUMN cottage_number TEXT NOT NULL DEFAULT ''")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS call_batches(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    border_id INTEGER,
                    threshold INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    FOREIGN KEY(border_id) REFERENCES borders(id) ON DELETE SET NULL
                )
            """.trimIndent())
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS call_entries(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    call_batch_id INTEGER NOT NULL,
                    truck_id INTEGER,
                    plate_snapshot TEXT NOT NULL,
                    queue_snapshot INTEGER NOT NULL,
                    owner_snapshot TEXT NOT NULL DEFAULT '',
                    missed INTEGER NOT NULL DEFAULT 0,
                    entered_at INTEGER,
                    FOREIGN KEY(call_batch_id) REFERENCES call_batches(id) ON DELETE CASCADE,
                    FOREIGN KEY(truck_id) REFERENCES trucks(id) ON DELETE SET NULL
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_call_batches_created ON call_batches(created_at)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_call_entries_batch ON call_entries(call_batch_id)")
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE trucks ADD COLUMN driver_name TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE trucks ADD COLUMN driver_payment_account TEXT NOT NULL DEFAULT ''")
        }
        if (oldVersion < 5) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS truck_accounting(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    truck_id INTEGER NOT NULL,
                    jalali_year INTEGER NOT NULL,
                    jalali_month INTEGER NOT NULL,
                    clearance_fee INTEGER NOT NULL DEFAULT 0,
                    declaration_fee INTEGER NOT NULL DEFAULT 0,
                    freight_fee INTEGER NOT NULL DEFAULT 0,
                    crane_fee INTEGER NOT NULL DEFAULT 0,
                    misc_fee INTEGER NOT NULL DEFAULT 0,
                    paid_amount INTEGER NOT NULL DEFAULT 0,
                    note TEXT NOT NULL DEFAULT '',
                    updated_at INTEGER NOT NULL,
                    UNIQUE(truck_id, jalali_year, jalali_month),
                    FOREIGN KEY(truck_id) REFERENCES trucks(id) ON DELETE CASCADE
                )
            """.trimIndent())
        }
    }


    fun exportBackup(): JSONObject {
        val root = JSONObject().put("schema_version", 5)
        val data = JSONObject()
        val tables = listOf("borders", "owners", "cargo_types", "trucks", "call_batches", "call_entries", "accounting", "truck_accounting")
        tables.forEach { table ->
            val rows = JSONArray()
            readableDatabase.rawQuery("SELECT * FROM $table ORDER BY id", null).use { c ->
                while (c.moveToNext()) {
                    val row = JSONObject()
                    for (i in 0 until c.columnCount) {
                        val name = c.getColumnName(i)
                        when (c.getType(i)) {
                            Cursor.FIELD_TYPE_NULL -> row.put(name, JSONObject.NULL)
                            Cursor.FIELD_TYPE_INTEGER -> row.put(name, c.getLong(i))
                            Cursor.FIELD_TYPE_FLOAT -> row.put(name, c.getDouble(i))
                            Cursor.FIELD_TYPE_STRING -> row.put(name, c.getString(i))
                            Cursor.FIELD_TYPE_BLOB -> row.put(name, android.util.Base64.encodeToString(c.getBlob(i), android.util.Base64.NO_WRAP))
                        }
                    }
                    rows.put(row)
                }
            }
            data.put(table, rows)
        }
        root.put("data", data)
        return root
    }

    fun importBackup(root: JSONObject) {
        val data = root.optJSONObject("data") ?: return
        val db = writableDatabase
        val deleteOrder = listOf("truck_accounting", "call_entries", "call_batches", "trucks", "accounting", "cargo_types", "owners", "borders")
        val insertOrder = listOf("borders", "owners", "cargo_types", "trucks", "call_batches", "call_entries", "accounting", "truck_accounting")
        db.beginTransaction()
        try {
            deleteOrder.forEach { db.delete(it, null, null) }
            insertOrder.forEach { table ->
                val rows = data.optJSONArray(table) ?: JSONArray()
                for (i in 0 until rows.length()) {
                    val row = rows.getJSONObject(i)
                    val cv = ContentValues()
                    val keys = row.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (row.isNull(key)) { cv.putNull(key); continue }
                        when (val value = row.get(key)) {
                            is Int -> cv.put(key, value)
                            is Long -> cv.put(key, value)
                            is Double -> cv.put(key, value)
                            is Boolean -> cv.put(key, if (value) 1 else 0)
                            else -> cv.put(key, value.toString())
                        }
                    }
                    db.insertOrThrow(table, null, cv)
                }
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun listBorders(): List<Border> = readableDatabase.rawQuery(
        "SELECT id,name,created_at FROM borders ORDER BY name COLLATE NOCASE", null
    ).use { c -> buildList { while (c.moveToNext()) add(Border(c.long("id"), c.str("name"), c.long("created_at"))) } }

    fun addBorder(name: String): Long = writableDatabase.insertOrThrow("borders", null, ContentValues().apply {
        put("name", name.trim())
        put("created_at", System.currentTimeMillis())
    })

    fun updateBorder(id: Long, name: String) = writableDatabase.update("borders", ContentValues().apply { put("name", name.trim()) }, "id=?", arrayOf(id.toString()))

    fun deleteBorder(id: Long) = writableDatabase.delete("borders", "id=?", arrayOf(id.toString()))

    fun listOwners(borderId: Long?): List<Owner> {
        val where = if (borderId == null) "" else "WHERE o.border_id=?"
        val args = if (borderId == null) null else arrayOf(borderId.toString())
        val sql = """
            SELECT o.id,o.border_id,o.name,o.note,o.color_seed,o.created_at,
                   COUNT(DISTINCT c.id) cargo_count,
                   COUNT(DISTINCT t.id) truck_count
            FROM owners o
            LEFT JOIN cargo_types c ON c.owner_id=o.id
            LEFT JOIN trucks t ON t.cargo_type_id=c.id
            $where
            GROUP BY o.id
            ORDER BY o.created_at DESC
        """.trimIndent()
        return readableDatabase.rawQuery(sql, args).use { c -> buildList { while (c.moveToNext()) add(c.toOwner()) } }
    }

    fun getOwner(id: Long): Owner? = readableDatabase.rawQuery("""
        SELECT o.id,o.border_id,o.name,o.note,o.color_seed,o.created_at,
               COUNT(DISTINCT c.id) cargo_count,
               COUNT(DISTINCT t.id) truck_count
        FROM owners o LEFT JOIN cargo_types c ON c.owner_id=o.id
        LEFT JOIN trucks t ON t.cargo_type_id=c.id WHERE o.id=? GROUP BY o.id
    """.trimIndent(), arrayOf(id.toString())).use { c -> if (c.moveToFirst()) c.toOwner() else null }

    fun addOwner(borderId: Long, name: String, note: String = ""): Long = writableDatabase.insertOrThrow("owners", null, ContentValues().apply {
        put("border_id", borderId); put("name", name.trim()); put("note", note.trim())
        put("color_seed", ((name.hashCode().toLong() and 0x7fffffff) % 8).toInt()); put("created_at", System.currentTimeMillis())
    })

    fun updateOwner(id: Long, name: String, note: String) = writableDatabase.update("owners", ContentValues().apply {
        put("name", name.trim()); put("note", note.trim())
    }, "id=?", arrayOf(id.toString()))

    fun deleteOwner(id: Long) = writableDatabase.delete("owners", "id=?", arrayOf(id.toString()))

    fun listCargoTypes(ownerId: Long): List<CargoType> = readableDatabase.rawQuery("""
        SELECT c.id,c.owner_id,c.title,c.origin,c.iraqi_owner,c.iraqi_broker,c.created_at,
               COUNT(t.id) truck_count
        FROM cargo_types c LEFT JOIN trucks t ON t.cargo_type_id=c.id
        WHERE c.owner_id=? GROUP BY c.id ORDER BY c.created_at DESC
    """.trimIndent(), arrayOf(ownerId.toString())).use { c -> buildList { while (c.moveToNext()) add(c.toCargo()) } }

    fun getCargoType(id: Long): CargoType? = readableDatabase.rawQuery("""
        SELECT c.id,c.owner_id,c.title,c.origin,c.iraqi_owner,c.iraqi_broker,c.created_at,
               COUNT(t.id) truck_count
        FROM cargo_types c LEFT JOIN trucks t ON t.cargo_type_id=c.id
        WHERE c.id=? GROUP BY c.id
    """.trimIndent(), arrayOf(id.toString())).use { c -> if (c.moveToFirst()) c.toCargo() else null }

    fun addCargoType(ownerId: Long, title: String, origin: String, iraqiOwner: String, iraqiBroker: String): Long =
        writableDatabase.insertOrThrow("cargo_types", null, ContentValues().apply {
            put("owner_id", ownerId); put("title", title.trim()); put("origin", origin.trim())
            put("iraqi_owner", iraqiOwner.trim()); put("iraqi_broker", iraqiBroker.trim()); put("created_at", System.currentTimeMillis())
        })

    fun updateCargoType(id: Long, title: String, origin: String, iraqiOwner: String, iraqiBroker: String) =
        writableDatabase.update("cargo_types", ContentValues().apply {
            put("title", title.trim()); put("origin", origin.trim()); put("iraqi_owner", iraqiOwner.trim()); put("iraqi_broker", iraqiBroker.trim())
        }, "id=?", arrayOf(id.toString()))

    fun deleteCargoType(id: Long) = writableDatabase.delete("cargo_types", "id=?", arrayOf(id.toString()))

    fun queueExists(borderId: Long, queue: Int, exceptTruckId: Long? = null): Truck? {
        val except = if (exceptTruckId == null) "" else "AND t.id<>?"
        val args = mutableListOf(borderId.toString(), queue.toString()).apply { if (exceptTruckId != null) add(exceptTruckId.toString()) }.toTypedArray()
        return readableDatabase.rawQuery(truckSelect + " WHERE o.border_id=? AND t.parking_queue=? AND t.completed_at IS NULL $except LIMIT 1", args)
            .use { c -> if (c.moveToFirst()) c.toTruck() else null }
    }

    fun addTruck(
        cargoTypeId: Long, plate: String, weightKg: Double, size: String, bundleCount: Int,
        phone: String, driverName: String, driverPaymentAccount: String,
        parkingQueue: Int, declarationType: DeclarationType, cottageNumber: String
    ): Long = writableDatabase.insertOrThrow("trucks", null, ContentValues().apply {
        put("cargo_type_id", cargoTypeId); put("plate", plate.trim()); put("weight_kg", weightKg); put("size", size.trim())
        put("bundle_count", bundleCount); put("phone", phone.trim()); put("driver_name", driverName.trim())
        put("driver_payment_account", driverPaymentAccount.trim()); put("parking_queue", parkingQueue)
        put("declaration_type", declarationType.dbValue)
        put("cottage_number", if (declarationType == DeclarationType.COTTAGE) cottageNumber.trim() else "")
        put("status", CargoStatus.PARKING.dbValue); put("created_at", System.currentTimeMillis())
    })

    fun updateTruck(
        id: Long, plate: String, weightKg: Double, size: String, bundleCount: Int,
        phone: String, driverName: String, driverPaymentAccount: String,
        parkingQueue: Int, declarationType: DeclarationType, cottageNumber: String
    ) = writableDatabase.update("trucks", ContentValues().apply {
        put("plate", plate.trim()); put("weight_kg", weightKg); put("size", size.trim()); put("bundle_count", bundleCount)
        put("phone", phone.trim()); put("driver_name", driverName.trim()); put("driver_payment_account", driverPaymentAccount.trim())
        put("parking_queue", parkingQueue)
        put("declaration_type", declarationType.dbValue)
        put("cottage_number", if (declarationType == DeclarationType.COTTAGE) cottageNumber.trim() else "")
    }, "id=?", arrayOf(id.toString()))

    fun deleteTruck(id: Long) = writableDatabase.delete("trucks", "id=?", arrayOf(id.toString()))

    fun getTruck(id: Long): Truck? = readableDatabase.rawQuery(truckSelect + " WHERE t.id=?", arrayOf(id.toString())).use { c -> if (c.moveToFirst()) c.toTruck() else null }

    fun listTrucksForCargo(cargoTypeId: Long, includeCompleted: Boolean = true): List<Truck> {
        val completed = if (includeCompleted) "" else "AND t.status<>'UNLOADED'"
        return readableDatabase.rawQuery(truckSelect + " WHERE t.cargo_type_id=? $completed ORDER BY t.created_at DESC, CASE WHEN t.parking_queue>0 THEN 0 ELSE 1 END, t.parking_queue ASC", arrayOf(cargoTypeId.toString()))
            .use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }
    }

    fun listTrucksForOwner(ownerId: Long): List<Truck> = readableDatabase.rawQuery(
        truckSelect + " WHERE o.id=? ORDER BY t.created_at DESC, CASE WHEN t.parking_queue>0 THEN 0 ELSE 1 END, t.parking_queue ASC",
        arrayOf(ownerId.toString())
    ).use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }

    fun listPendingQueueTrucks(borderId: Long?): List<Truck> {
        val clauses = mutableListOf("t.parking_queue<=0", "t.status='PARKING'")
        val args = mutableListOf<String>()
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        return readableDatabase.rawQuery(truckSelect + " WHERE ${clauses.joinToString(" AND ")} ORDER BY t.created_at ASC", args.toTypedArray())
            .use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }
    }

    fun listActiveTrucks(borderId: Long?, status: CargoStatus? = null): List<Truck> {
        val clauses = mutableListOf<String>()
        val args = mutableListOf<String>()
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        if (status == null) clauses += "t.status<>'UNLOADED' AND t.parking_queue>0"
        else {
            clauses += "t.status=?"; args += status.dbValue
            if (status != CargoStatus.UNLOADED) clauses += "t.parking_queue>0"
        }
        val where = if (clauses.isEmpty()) "1=1" else clauses.joinToString(" AND ")
        return readableDatabase.rawQuery(truckSelect + " WHERE $where ORDER BY CASE WHEN t.parking_queue>0 THEN 0 ELSE 1 END, t.parking_queue ASC, o.name ASC", args.toTypedArray())
            .use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }
    }

    fun listStatusTrucks(borderId: Long?, status: CargoStatus? = null): List<Truck> {
        val clauses = mutableListOf<String>()
        val args = mutableListOf<String>()
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        if (status != null) { clauses += "t.status=?"; args += status.dbValue } else clauses += "(t.parking_queue>0 OR t.status='UNLOADED')"
        if (status == CargoStatus.PARKING || status == CargoStatus.WAITING_UNLOAD) clauses += "t.parking_queue>0"
        val where = if (clauses.isEmpty()) "1=1" else clauses.joinToString(" AND ")
        return readableDatabase.rawQuery(truckSelect + " WHERE $where ORDER BY CASE WHEN t.status='UNLOADED' THEN 1 ELSE 0 END, CASE WHEN t.parking_queue>0 THEN 0 ELSE 1 END, t.parking_queue ASC, o.name ASC", args.toTypedArray())
            .use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }
    }

    fun setTruckStatus(id: Long, status: CargoStatus) {
        val now = System.currentTimeMillis()
        when (status) {
            CargoStatus.PARKING -> writableDatabase.execSQL("UPDATE trucks SET status='PARKING', completed_at=NULL WHERE id=?", arrayOf(id))
            CargoStatus.WAITING_UNLOAD -> writableDatabase.execSQL("UPDATE trucks SET status='WAITING_UNLOAD', called_at=COALESCE(called_at, ?), completed_at=NULL WHERE id=? AND parking_queue>0", arrayOf(now, id))
            CargoStatus.UNLOADED -> writableDatabase.execSQL("UPDATE trucks SET status='UNLOADED', completed_at=COALESCE(completed_at, ?) WHERE id=? AND parking_queue>0", arrayOf(now, id))
        }
    }

    fun completeTruck(id: Long) = setTruckStatus(id, CargoStatus.UNLOADED)

    fun previewCallCount(borderId: Long?, threshold: Int): Int {
        val clauses = mutableListOf("t.status='PARKING'", "t.parking_queue>0", "t.parking_queue<=?")
        val args = mutableListOf(threshold.toString())
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        val sql = """
            SELECT COUNT(*) FROM trucks t JOIN cargo_types c ON c.id=t.cargo_type_id JOIN owners o ON o.id=c.owner_id
            WHERE ${clauses.joinToString(" AND ")}
        """.trimIndent()
        return readableDatabase.rawQuery(sql, args.toTypedArray()).use { c -> c.moveToFirst(); c.getInt(0) }
    }

    fun callUpTo(borderId: Long?, threshold: Int): Int {
        val db = writableDatabase
        val trucks = mutableListOf<Truck>()
        val clauses = mutableListOf("t.status='PARKING'", "t.parking_queue>0", "t.parking_queue<=?")
        val args = mutableListOf(threshold.toString())
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        db.rawQuery(
            truckSelect + " WHERE ${clauses.joinToString(" AND ")} ORDER BY t.parking_queue",
            args.toTypedArray()
        ).use { c -> while (c.moveToNext()) trucks += c.toTruck() }

        val now = System.currentTimeMillis()
        db.beginTransaction()
        try {
            val batchId = db.insertOrThrow("call_batches", null, ContentValues().apply {
                if (borderId == null) putNull("border_id") else put("border_id", borderId)
                put("threshold", threshold)
                put("created_at", now)
            })
            trucks.forEach { truck ->
                db.execSQL(
                    "UPDATE trucks SET status='WAITING_UNLOAD', called_at=COALESCE(called_at, ?), completed_at=NULL WHERE id=?",
                    arrayOf(now, truck.id)
                )
                db.insertOrThrow("call_entries", null, ContentValues().apply {
                    put("call_batch_id", batchId)
                    put("truck_id", truck.id)
                    put("plate_snapshot", truck.plate)
                    put("queue_snapshot", truck.parkingQueue)
                    put("owner_snapshot", truck.ownerName)
                    put("missed", 0)
                    put("entered_at", now)
                })
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
        return trucks.size
    }

    fun listCallBatches(borderId: Long?): List<CallBatch> {
        val where = if (borderId == null) "" else "WHERE b.border_id=?"
        val args = if (borderId == null) null else arrayOf(borderId.toString())
        return readableDatabase.rawQuery(
            """
                SELECT b.id,b.border_id,b.threshold,b.created_at,COUNT(e.id) entry_count
                FROM call_batches b
                LEFT JOIN call_entries e ON e.call_batch_id=b.id
                $where
                GROUP BY b.id
                ORDER BY b.created_at DESC
            """.trimIndent(), args
        ).use { c ->
            buildList {
                while (c.moveToNext()) add(
                    CallBatch(
                        id = c.long("id"),
                        borderId = c.nullableLong("border_id"),
                        threshold = c.int("threshold"),
                        createdAt = c.long("created_at"),
                        entryCount = c.int("entry_count")
                    )
                )
            }
        }
    }

    fun latestCallBatch(borderId: Long?): CallBatch? = listCallBatches(borderId).firstOrNull()

    fun listCallEntries(batchId: Long): List<CallEntry> = readableDatabase.rawQuery(
        """
            SELECT e.id,e.call_batch_id,e.truck_id,e.plate_snapshot,e.queue_snapshot,e.owner_snapshot,e.missed,e.entered_at,
                   t.status current_status
            FROM call_entries e
            LEFT JOIN trucks t ON t.id=e.truck_id
            WHERE e.call_batch_id=?
            ORDER BY e.queue_snapshot ASC,e.id ASC
        """.trimIndent(),
        arrayOf(batchId.toString())
    ).use { c ->
        buildList {
            while (c.moveToNext()) add(
                CallEntry(
                    id = c.long("id"),
                    batchId = c.long("call_batch_id"),
                    truckId = c.nullableLong("truck_id"),
                    plate = c.str("plate_snapshot"),
                    parkingQueue = c.int("queue_snapshot"),
                    ownerName = c.str("owner_snapshot"),
                    missed = c.int("missed") == 1,
                    enteredAt = c.nullableLong("entered_at"),
                    currentStatus = if (c.isNull(c.idx("current_status"))) null else CargoStatus.fromDb(c.str("current_status"))
                )
            )
        }
    }

    fun markCallEntryMissed(entryId: Long) {
        val db = writableDatabase
        val truckId = db.rawQuery("SELECT truck_id FROM call_entries WHERE id=?", arrayOf(entryId.toString()))
            .use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getLong(0) else null }
        db.beginTransaction()
        try {
            db.execSQL("UPDATE call_entries SET missed=1, entered_at=NULL WHERE id=?", arrayOf(entryId))
            if (truckId != null) {
                db.execSQL(
                    "UPDATE trucks SET status='PARKING', called_at=NULL, completed_at=NULL WHERE id=? AND status<>'UNLOADED'",
                    arrayOf(truckId)
                )
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun markCallEntryEntered(entryId: Long) {
        val db = writableDatabase
        val truckId = db.rawQuery("SELECT truck_id FROM call_entries WHERE id=?", arrayOf(entryId.toString()))
            .use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getLong(0) else null }
        val now = System.currentTimeMillis()
        db.beginTransaction()
        try {
            db.execSQL("UPDATE call_entries SET missed=0, entered_at=? WHERE id=?", arrayOf(now, entryId))
            if (truckId != null) {
                db.execSQL(
                    "UPDATE trucks SET status='WAITING_UNLOAD', called_at=COALESCE(called_at, ?), completed_at=NULL WHERE id=? AND parking_queue>0",
                    arrayOf(now, truckId)
                )
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun dashboardStats(borderId: Long?): DashboardStats {
        val trucks = listActiveTrucks(borderId)
        return DashboardStats(trucks.size, trucks.count { it.status == CargoStatus.PARKING }, trucks.count { it.status == CargoStatus.WAITING_UNLOAD }, listOwners(borderId).size)
    }

    fun monthlyTrucks(borderId: Long?, year: Int, month: Int, ownerId: Long? = null): List<Truck> {
        val range = Jalali.monthRangeEpoch(year, month)
        val clauses = mutableListOf("t.created_at>=?", "t.created_at<?")
        val args = mutableListOf(range.first.toString(), (range.last + 1).toString())
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        if (ownerId != null) { clauses += "o.id=?"; args += ownerId.toString() }
        return readableDatabase.rawQuery(truckSelect + " WHERE ${clauses.joinToString(" AND ")} ORDER BY o.name, t.parking_queue", args.toTypedArray())
            .use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }
    }

    fun trucksOnJalaliDay(borderId: Long?, year: Int, month: Int, day: Int): List<Truck> {
        val range = Jalali.dayRangeEpoch(year, month, day)
        val clauses = mutableListOf("(t.created_at>=? AND t.created_at<? OR t.called_at>=? AND t.called_at<? OR t.completed_at>=? AND t.completed_at<?)")
        val args = mutableListOf(range.first.toString(), (range.last + 1).toString(), range.first.toString(), (range.last + 1).toString(), range.first.toString(), (range.last + 1).toString())
        if (borderId != null) { clauses += "o.border_id=?"; args += borderId.toString() }
        return readableDatabase.rawQuery(truckSelect + " WHERE ${clauses.joinToString(" AND ")} ORDER BY t.parking_queue", args.toTypedArray())
            .use { c -> buildList { while (c.moveToNext()) add(c.toTruck()) } }
    }

    fun monthSummary(borderId: Long?, year: Int, month: Int): MonthSummary {
        val trucks = monthlyTrucks(borderId, year, month)
        return MonthSummary(
            truckCount = trucks.size,
            totalWeightKg = trucks.sumOf { it.weightKg },
            ownerCount = trucks.map { it.ownerId }.distinct().size,
            parkingCount = trucks.count { it.status == CargoStatus.PARKING && it.parkingQueue > 0 },
            waitingCount = trucks.count { it.status == CargoStatus.WAITING_UNLOAD }
        )
    }

    fun ownerMonthSummaries(borderId: Long?, year: Int, month: Int): List<OwnerMonthSummary> {
        val trucks = monthlyTrucks(borderId, year, month)
        val owners = listOwners(borderId).associateBy { it.id }
        return trucks.groupBy { it.ownerId }.mapNotNull { (ownerId, list) ->
            owners[ownerId]?.let { OwnerMonthSummary(it, list.size, list.sumOf { t -> t.weightKg }, effectiveAccounting(ownerId, list, year, month)) }
        }.sortedBy { it.owner.name }
    }

    fun getAccounting(ownerId: Long, year: Int, month: Int): Accounting? = readableDatabase.rawQuery("""
        SELECT * FROM accounting WHERE owner_id=? AND jalali_year=? AND jalali_month=? LIMIT 1
    """.trimIndent(), arrayOf(ownerId.toString(), year.toString(), month.toString())).use { c -> if (c.moveToFirst()) c.toAccounting() else null }

    fun saveAccounting(a: Accounting): Long {
        val cv = ContentValues().apply {
            put("owner_id", a.ownerId); put("jalali_year", a.jalaliYear); put("jalali_month", a.jalaliMonth)
            put("clearance_fee", a.clearanceFee); put("declaration_fee", a.declarationFee); put("freight_fee", a.freightFee)
            put("crane_fee", a.craneFee); put("misc_fee", a.miscFee); put("paid_amount", a.paidAmount)
            put("note", a.note); put("invoice_no", a.invoiceNo); put("updated_at", System.currentTimeMillis())
        }
        val existing = getAccounting(a.ownerId, a.jalaliYear, a.jalaliMonth)
        return if (existing == null) writableDatabase.insertOrThrow("accounting", null, cv)
        else { writableDatabase.update("accounting", cv, "id=?", arrayOf(existing.id.toString())); existing.id }
    }

    fun monthlyAccounting(borderId: Long?, year: Int, month: Int): List<Pair<Owner, Accounting>> {
        val trucksByOwner = monthlyTrucks(borderId, year, month).groupBy { it.ownerId }
        return listOwners(borderId).mapNotNull { owner ->
            effectiveAccounting(owner.id, trucksByOwner[owner.id].orEmpty(), year, month)?.let { owner to it }
        }
    }

    private fun effectiveAccounting(ownerId: Long, trucks: List<Truck>, year: Int, month: Int): Accounting? {
        val legacy = getAccounting(ownerId, year, month)
        val costs = truckAccountingFor(trucks, year, month).values
        if (costs.isEmpty()) return legacy
        return Accounting(
            id = legacy?.id ?: 0,
            ownerId = ownerId,
            jalaliYear = year,
            jalaliMonth = month,
            clearanceFee = costs.sumOf { it.clearanceFee },
            declarationFee = costs.sumOf { it.declarationFee },
            freightFee = costs.sumOf { it.freightFee },
            craneFee = costs.sumOf { it.craneFee },
            miscFee = costs.sumOf { it.miscFee },
            paidAmount = costs.sumOf { it.paidAmount },
            note = legacy?.note ?: "",
            invoiceNo = legacy?.invoiceNo ?: "",
            updatedAt = costs.maxOfOrNull { it.updatedAt } ?: legacy?.updatedAt ?: System.currentTimeMillis()
        )
    }

    fun getTruckAccounting(truckId: Long, year: Int, month: Int): TruckAccounting? = readableDatabase.rawQuery(
        "SELECT * FROM truck_accounting WHERE truck_id=? AND jalali_year=? AND jalali_month=? LIMIT 1",
        arrayOf(truckId.toString(), year.toString(), month.toString())
    ).use { c -> if (c.moveToFirst()) c.toTruckAccounting() else null }

    fun truckAccountingFor(trucks: List<Truck>, year: Int, month: Int): Map<Long, TruckAccounting> =
        trucks.mapNotNull { truck -> getTruckAccounting(truck.id, year, month)?.let { truck.id to it } }.toMap()

    fun saveTruckAccounting(a: TruckAccounting): Long {
        val cv = ContentValues().apply {
            put("truck_id", a.truckId); put("jalali_year", a.jalaliYear); put("jalali_month", a.jalaliMonth)
            put("clearance_fee", a.clearanceFee); put("declaration_fee", a.declarationFee); put("freight_fee", a.freightFee)
            put("crane_fee", a.craneFee); put("misc_fee", a.miscFee); put("paid_amount", a.paidAmount)
            put("note", a.note); put("updated_at", System.currentTimeMillis())
        }
        val existing = getTruckAccounting(a.truckId, a.jalaliYear, a.jalaliMonth)
        return if (existing == null) writableDatabase.insertOrThrow("truck_accounting", null, cv)
        else {
            writableDatabase.update("truck_accounting", cv, "id=?", arrayOf(existing.id.toString()))
            existing.id
        }
    }

    private val truckSelect = """
        SELECT t.id,t.cargo_type_id,t.plate,t.weight_kg,t.size,t.bundle_count,t.phone,t.driver_name,t.driver_payment_account,t.parking_queue,t.declaration_type,t.cottage_number,t.status,t.created_at,t.called_at,t.completed_at,
               c.owner_id,c.title cargo_title,c.origin,c.iraqi_owner,c.iraqi_broker,
               o.border_id,o.name owner_name
        FROM trucks t JOIN cargo_types c ON c.id=t.cargo_type_id JOIN owners o ON o.id=c.owner_id
    """.trimIndent()

    private fun Cursor.toOwner() = Owner(long("id"), long("border_id"), str("name"), str("note"), int("color_seed"), long("created_at"), int("cargo_count"), int("truck_count"))
    private fun Cursor.toCargo() = CargoType(long("id"), long("owner_id"), str("title"), str("origin"), str("iraqi_owner"), str("iraqi_broker"), long("created_at"), int("truck_count"))
    private fun Cursor.toTruck() = Truck(
        long("id"), long("cargo_type_id"), long("owner_id"), long("border_id"), str("owner_name"), str("cargo_title"), str("origin"), str("iraqi_owner"), str("iraqi_broker"),
        str("plate"), dbl("weight_kg"), str("size"), int("bundle_count"), str("phone"), str("driver_name"), str("driver_payment_account"), int("parking_queue"),
        DeclarationType.fromDb(str("declaration_type")), str("cottage_number"), CargoStatus.fromDb(str("status")), long("created_at"),
        nullableLong("called_at"), nullableLong("completed_at")
    )
    private fun Cursor.toAccounting() = Accounting(
        id = long("id"), ownerId = long("owner_id"), jalaliYear = int("jalali_year"), jalaliMonth = int("jalali_month"),
        clearanceFee = long("clearance_fee"), declarationFee = long("declaration_fee"), freightFee = long("freight_fee"), craneFee = long("crane_fee"),
        miscFee = long("misc_fee"), paidAmount = long("paid_amount"), note = str("note"), invoiceNo = str("invoice_no"), updatedAt = long("updated_at")
    )
    private fun Cursor.toTruckAccounting() = TruckAccounting(
        id = long("id"), truckId = long("truck_id"), jalaliYear = int("jalali_year"), jalaliMonth = int("jalali_month"),
        clearanceFee = long("clearance_fee"), declarationFee = long("declaration_fee"), freightFee = long("freight_fee"),
        craneFee = long("crane_fee"), miscFee = long("misc_fee"), paidAmount = long("paid_amount"), note = str("note"), updatedAt = long("updated_at")
    )

    private fun Cursor.idx(name: String) = getColumnIndexOrThrow(name)
    private fun Cursor.str(name: String) = getString(idx(name)) ?: ""
    private fun Cursor.long(name: String) = getLong(idx(name))
    private fun Cursor.int(name: String) = getInt(idx(name))
    private fun Cursor.dbl(name: String) = getDouble(idx(name))
    private fun Cursor.nullableLong(name: String): Long? = if (isNull(idx(name))) null else getLong(idx(name))
}
