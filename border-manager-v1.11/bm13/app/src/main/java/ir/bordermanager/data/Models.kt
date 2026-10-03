package ir.bordermanager.data


enum class DeclarationType(val dbValue: String, val titleFa: String) {
    COTTAGE("COTTAGE", "کوتاژ"),
    COMPANY("COMPANY", "اظهار شرکت");

    companion object {
        fun fromDb(value: String) = entries.firstOrNull { it.dbValue == value } ?: COTTAGE
    }
}

enum class CargoStatus(val dbValue: String, val titleFa: String) {
    PARKING("PARKING", "در پارکینگ قرار دارد"),
    WAITING_UNLOAD("WAITING_UNLOAD", "در انتظار برای تخلیه"),
    UNLOADED("UNLOADED", "تخلیه شد");

    companion object {
        fun fromDb(value: String) = entries.firstOrNull { it.dbValue == value } ?: PARKING
    }
}

data class Border(
    val id: Long,
    val name: String,
    val createdAt: Long
)

data class Owner(
    val id: Long,
    val borderId: Long,
    val name: String,
    val note: String,
    val colorSeed: Int,
    val createdAt: Long,
    val cargoCount: Int = 0,
    val truckCount: Int = 0
)

data class CargoType(
    val id: Long,
    val ownerId: Long,
    val title: String,
    val origin: String,
    val iraqiOwner: String,
    val iraqiBroker: String,
    val createdAt: Long,
    val truckCount: Int = 0
)

data class Truck(
    val id: Long,
    val cargoTypeId: Long,
    val ownerId: Long,
    val borderId: Long,
    val ownerName: String,
    val cargoTitle: String,
    val origin: String,
    val iraqiOwner: String,
    val iraqiBroker: String,
    val plate: String,
    val weightKg: Double,
    val size: String,
    val bundleCount: Int,
    val phone: String,
    val driverName: String,
    /** Driver's settlement destination: IBAN (شبا) or bank card number. */
    val driverPaymentAccount: String,
    val parkingQueue: Int,
    val declarationType: DeclarationType,
    val cottageNumber: String,
    val status: CargoStatus,
    val createdAt: Long,
    val calledAt: Long?,
    val completedAt: Long?
)

data class Accounting(
    val id: Long = 0,
    val ownerId: Long,
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val clearanceFee: Long = 0,
    val declarationFee: Long = 0,
    val freightFee: Long = 0,
    val craneFee: Long = 0,
    val miscFee: Long = 0,
    val paidAmount: Long = 0,
    val note: String = "",
    val invoiceNo: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val total: Long get() = clearanceFee + declarationFee + freightFee + craneFee + miscFee
    val remaining: Long get() = total - paidAmount
}

/**
 * هزینه‌های مستقل هر ماشین در یک ماه.
 * مبلغ‌ها همگی تومان هستند و اطلاعات پرداخت کرایه کنار همان پلاک نگهداری می‌شود.
 */
data class TruckAccounting(
    val id: Long = 0,
    val truckId: Long,
    val jalaliYear: Int,
    val jalaliMonth: Int,
    val clearanceFee: Long = 0,
    val declarationFee: Long = 0,
    val freightFee: Long = 0,
    val craneFee: Long = 0,
    val miscFee: Long = 0,
    val paidAmount: Long = 0,
    val expenseNote: String = "",
    val freightStatus: String = "SETTLED",
    val accountHolderName: String = "",
    val accountHolderInfo: String = "",
    val cardNumber: String = "",
    val iban: String = "",
    val accountNumber: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val total: Long get() = clearanceFee + declarationFee + freightFee + craneFee + miscFee
    val remaining: Long get() = total - paidAmount
}

data class MonthSummary(
    val truckCount: Int,
    val totalWeightKg: Double,
    val ownerCount: Int,
    val parkingCount: Int,
    val waitingCount: Int
)

data class OwnerMonthSummary(
    val owner: Owner,
    val truckCount: Int,
    val totalWeightKg: Double,
    val accounting: Accounting?
)

data class DashboardStats(
    val totalActive: Int,
    val parking: Int,
    val waiting: Int,
    val owners: Int
)

data class CallBatch(
    val id: Long,
    val borderId: Long?,
    val threshold: Int,
    val createdAt: Long,
    val entryCount: Int
)

data class CallEntry(
    val id: Long,
    val batchId: Long,
    val truckId: Long?,
    val plate: String,
    val parkingQueue: Int,
    val ownerName: String,
    val missed: Boolean,
    val enteredAt: Long?,
    val currentStatus: CargoStatus?
)
