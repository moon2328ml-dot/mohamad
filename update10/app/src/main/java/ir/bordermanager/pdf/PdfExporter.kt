package ir.bordermanager.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import androidx.core.content.FileProvider
import ir.bordermanager.data.Accounting
import ir.bordermanager.data.AppPreferences
import ir.bordermanager.data.Owner
import ir.bordermanager.data.Truck
import ir.bordermanager.data.TruckAccounting
import ir.bordermanager.data.DeclarationType
import ir.bordermanager.util.Formatters
import ir.bordermanager.util.Jalali
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 34f

    fun monthlyReport(
        context: Context,
        year: Int,
        month: Int,
        borderTitle: String,
        trucks: List<Truck>,
        accounting: List<Pair<Owner, Accounting>>,
        reportTitle: String? = null,
        truckAccounting: Map<Long, TruckAccounting> = emptyMap()
    ): File {
        val pdf = PdfDocument()
        val prefs = AppPreferences(context)
        val title = reportTitle ?: "گزارش حسابداری ماه ${Jalali.Date(year, month, 1).monthTitle()}"
        var pageNo = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
        var y = drawHeader(page.canvas, prefs.companyName, title, borderTitle)

        val uniqueOwners = trucks.map { it.ownerId }.distinct().size
        val totalWeight = trucks.sumOf { it.weightKg }
        y = drawRtl(page.canvas, "تعداد کل ماشین‌ها: ${Formatters.number(trucks.size)}     تعداد صاحبان کالا: ${Formatters.number(uniqueOwners)}", y, 13f, true)
        y = drawRtl(page.canvas, "مجموع وزن: ${Formatters.weightKg(totalWeight)}", y, 13f, false) + 8f
        y = drawDivider(page.canvas, y)

        trucks.forEachIndexed { index, t ->
            if (y > 760f) {
                pdf.finishPage(page); pageNo++
                page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
                y = drawHeader(page.canvas, prefs.companyName, title, borderTitle)
            }
            val created = Jalali.fromEpoch(t.createdAt).format()
            val called = t.calledAt?.let { Jalali.fromEpoch(it).format() } ?: "-"
            y = drawRtl(page.canvas, "ردیف ${index + 1} | ${t.ownerName} | ${t.cargoTitle} | پلاک ${t.plate} | نوبت ${if(t.parkingQueue>0)t.parkingQueue else "ثبت نشده"}", y, 11.5f, true)
            y = drawRtl(page.canvas, "وزن ${Formatters.weightKg(t.weightKg)} | سایز ${t.size.ifBlank { "-" }} | بندل ${t.bundleCount}", y, 10.5f, false)
            y = drawRtl(page.canvas, "راننده: ${t.driverName.ifBlank { "-" }} | تماس: ${t.phone.ifBlank { "-" }} | شبا/کارت: ${t.driverPaymentAccount.ifBlank { "-" }}", y, 10.5f, false)
            truckAccounting[t.id]?.let { a ->
                y = drawRtl(page.canvas, "هزینه‌ها: ترخیص ${Formatters.toman(a.clearanceFee)} | اظهار ${Formatters.toman(a.declarationFee)} | کرایه ${Formatters.toman(a.freightFee)}", y, 9.5f, false)
                y = drawRtl(page.canvas, "جرثقیل ${Formatters.toman(a.craneFee)} | متفرقه ${Formatters.toman(a.miscFee)} | جمع این ماشین ${Formatters.toman(a.total)} | پرداخت ${Formatters.toman(a.paidAmount)}", y, 9.5f, true)
            }
            val declaration = if(t.declarationType==DeclarationType.COMPANY) "اظهار شرکت" else "کوتاژ ${t.cottageNumber.ifBlank{"-"}}"
            y = drawRtl(page.canvas, "اظهار: $declaration | ثبت: $created | ورود/فراخوان: $called | وضعیت: ${t.status.titleFa}", y, 10f, false)
            y = drawDivider(page.canvas, y + 2f)
        }

        // monthlyAccounting() already returns the per-truck aggregate when
        // row-level costs exist, and falls back to the legacy owner total.
        val costs = accounting.map { it.second }
        val totalClearance = costs.sumOf { it.clearanceFee }
        val totalDeclaration = costs.sumOf { it.declarationFee }
        val totalFreight = costs.sumOf { it.freightFee }
        val totalCrane = costs.sumOf { it.craneFee }
        val totalMisc = costs.sumOf { it.miscFee }
        val totalPaid = costs.sumOf { it.paidAmount }
        val total = costs.sumOf { it.total }
        if (y > 650f) {
            pdf.finishPage(page); pageNo++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
            y = drawHeader(page.canvas, prefs.companyName, if (reportTitle?.contains("روزانه") == true) "جمع‌بندی گزارش روزانه" else "جمع‌بندی حسابداری ماه", borderTitle)
        }
        y += 10
        y = drawRtl(page.canvas, "جمع‌بندی هزینه‌های ثبت‌شده", y, 14f, true)
        listOf(
            "حق ترخیص" to totalClearance,
            "هزینه اظهار" to totalDeclaration,
            "مبلغ کرایه" to totalFreight,
            "هزینه جرثقیل" to totalCrane,
            "هزینه‌های متفرقه" to totalMisc,
            "مبلغ پرداخت‌شده" to totalPaid,
            "جمع کل هزینه‌ها" to total,
            "مانده کل" to (total - totalPaid)
        ).forEach { (label, value) -> y = drawRtl(page.canvas, "$label: ${Formatters.toman(value)}", y, 11.5f, label == "جمع کل هزینه‌ها" || label == "مانده کل") }
        pdf.finishPage(page)

        return write(context, pdf, "گزارش-کلی-$year-${month.toString().padStart(2,'0')}.pdf")
    }

    fun dailyReport(
        context: Context,
        year: Int,
        month: Int,
        day: Int,
        borderTitle: String,
        trucks: List<Truck>,
        accounting: List<Pair<Owner, Accounting>>,
        truckAccounting: Map<Long, TruckAccounting> = emptyMap()
    ): File {
        val dailyAccounting = accounting.map { (owner, legacy) ->
            val rows = trucks.filter { it.ownerId == owner.id }.mapNotNull { truckAccounting[it.id] }
            if (rows.isEmpty()) owner to legacy else owner to legacy.copy(
                clearanceFee = rows.sumOf { it.clearanceFee },
                declarationFee = rows.sumOf { it.declarationFee },
                freightFee = rows.sumOf { it.freightFee },
                craneFee = rows.sumOf { it.craneFee },
                miscFee = rows.sumOf { it.miscFee },
                paidAmount = rows.sumOf { it.paidAmount },
                updatedAt = rows.maxOfOrNull { it.updatedAt } ?: legacy.updatedAt
            )
        }
        return monthlyReport(
            context = context,
            year = year,
            month = month,
            borderTitle = borderTitle,
            trucks = trucks,
            accounting = dailyAccounting,
            reportTitle = "گزارش روزانه وضعیت بار ${Jalali.Date(year, month, day).format()}",
            truckAccounting = truckAccounting
        )
    }

    fun ownerInvoice(
        context: Context,
        owner: Owner,
        year: Int,
        month: Int,
        borderTitle: String,
        trucks: List<Truck>,
        accounting: Accounting,
        truckAccounting: Map<Long, TruckAccounting> = emptyMap()
    ): File {
        val pdf = PdfDocument()
        val prefs = AppPreferences(context)
        var pageNo = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
        var y = drawHeader(page.canvas, prefs.companyName, "صورت‌حساب اختصاصی ${owner.name}", borderTitle)
        y = drawRtl(page.canvas, "ماه حساب: ${Jalali.Date(year, month, 1).monthTitle()}     شماره صورت‌حساب: ${accounting.invoiceNo.ifBlank { "-" }}", y, 12f, true)
        y = drawRtl(page.canvas, "تاریخ صدور: ${Jalali.now().format()}     تعداد ماشین: ${Formatters.number(trucks.size)}", y, 11f, false)
        y = drawRtl(page.canvas, "مجموع وزن: ${Formatters.weightKg(trucks.sumOf { it.weightKg })}", y, 11f, false)
        y = drawDivider(page.canvas, y + 4f)

        trucks.forEachIndexed { index, t ->
            if (y > 720f) {
                pdf.finishPage(page); pageNo++
                page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
                y = drawHeader(page.canvas, prefs.companyName, "ادامه صورت‌حساب ${owner.name}", borderTitle)
            }
            val created = Jalali.fromEpoch(t.createdAt).format()
            val called = t.calledAt?.let { Jalali.fromEpoch(it).format() } ?: "-"
            y = drawRtl(page.canvas, "ردیف ${index + 1} | ${t.cargoTitle} | پلاک ${t.plate} | نوبت ${if(t.parkingQueue>0)t.parkingQueue else "ثبت نشده"} | ${Formatters.weightKg(t.weightKg)}", y, 11f, true)
            y = drawRtl(page.canvas, "سایز: ${t.size.ifBlank { "-" }} | بندل: ${t.bundleCount}", y, 10f, false)
            y = drawRtl(page.canvas, "راننده: ${t.driverName.ifBlank { "-" }} | تماس: ${t.phone.ifBlank { "-" }} | شبا/کارت: ${t.driverPaymentAccount.ifBlank { "-" }}", y, 10f, false)
            truckAccounting[t.id]?.let { a ->
                y = drawRtl(page.canvas, "هزینه‌ها: ترخیص ${Formatters.toman(a.clearanceFee)} | اظهار ${Formatters.toman(a.declarationFee)} | کرایه ${Formatters.toman(a.freightFee)}", y, 9f, false)
                y = drawRtl(page.canvas, "جرثقیل ${Formatters.toman(a.craneFee)} | متفرقه ${Formatters.toman(a.miscFee)} | جمع این ماشین ${Formatters.toman(a.total)}", y, 9f, true)
            }
            val declaration = if(t.declarationType==DeclarationType.COMPANY) "اظهار شرکت" else "کوتاژ ${t.cottageNumber.ifBlank{"-"}}"
            y = drawRtl(page.canvas, "اظهار: $declaration | ثبت: $created | ورود/فراخوان: $called", y, 9.5f, false)
            y = drawDivider(page.canvas, y + 2f)
        }

        if (y > 590f) {
            pdf.finishPage(page); pageNo++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
            y = drawHeader(page.canvas, prefs.companyName, "ریز هزینه‌های ${owner.name}", borderTitle)
        }
        y += 8
        y = drawRtl(page.canvas, "ریز هزینه‌ها", y, 14f, true)
        val invoiceCosts = if (truckAccounting.isNotEmpty()) truckAccounting.values.toList() else listOf(accounting)
        listOf(
            "حق ترخیص" to invoiceCosts.sumOf { it.clearanceFee },
            "هزینه اظهار" to invoiceCosts.sumOf { it.declarationFee },
            "مبلغ کرایه" to invoiceCosts.sumOf { it.freightFee },
            "هزینه جرثقیل" to invoiceCosts.sumOf { it.craneFee },
            "هزینه‌های متفرقه" to invoiceCosts.sumOf { it.miscFee }
        ).forEachIndexed { index, (label, value) -> y = moneyRow(page.canvas, y, "${index + 1}. $label", value) }
        y += 5
        val invoiceTotal = invoiceCosts.sumOf { it.total }
        val invoicePaid = invoiceCosts.sumOf { it.paidAmount }
        val invoiceRemaining = invoiceTotal - invoicePaid
        y = moneyRow(page.canvas, y, "جمع کل هزینه‌ها", invoiceTotal, true)
        y = moneyRow(page.canvas, y, "مبلغ پرداخت‌شده", invoicePaid)
        y = moneyRow(page.canvas, y, "مانده حساب", invoiceRemaining, true)
        y = drawRtl(page.canvas, "وضعیت حساب: ${if (invoiceRemaining <= 0) "تسویه‌شده" else "بدهکار"}", y + 4, 12f, true)
        if (accounting.note.isNotBlank()) y = drawRtl(page.canvas, "توضیحات: ${accounting.note}", y + 4, 10.5f, false)
        if (prefs.companyPhone.isNotBlank()) y = drawRtl(page.canvas, "تماس: ${prefs.companyPhone}", y + 6, 9.5f, false)
        if (prefs.companyAddress.isNotBlank()) drawRtl(page.canvas, "نشانی: ${prefs.companyAddress}", y, 9.5f, false)
        pdf.finishPage(page)

        val safeName = owner.name.replace(Regex("[^\u0600-\u06FFa-zA-Z0-9_-]+"), "-")
        return write(context, pdf, "صورت-حساب-$safeName-$year-${month.toString().padStart(2,'0')}.pdf")
    }

    private fun drawHeader(canvas: android.graphics.Canvas, company: String, title: String, border: String): Float {
        canvas.drawColor(Color.WHITE)
        var y = 34f
        y = drawRtl(canvas, company.ifBlank { "مدیریت مرز" }, y, 17f, true)
        y = drawRtl(canvas, title, y, 16f, true)
        y = drawRtl(canvas, "مرز: $border", y, 10.5f, false)
        return drawDivider(canvas, y + 4f) + 6f
    }

    private fun moneyRow(canvas: android.graphics.Canvas, y: Float, label: String, value: Long, bold: Boolean = false): Float {
        return drawRtl(canvas, "$label: ${Formatters.toman(value)}", y, if (bold) 12.5f else 11.5f, bold)
    }

    private fun drawDivider(canvas: android.graphics.Canvas, y: Float): Float {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(224, 218, 237); strokeWidth = 1f }
        canvas.drawLine(MARGIN, y, PAGE_W - MARGIN, y, p)
        return y + 8f
    }

    private fun drawRtl(canvas: android.graphics.Canvas, text: String, y: Float, size: Float, bold: Boolean): Float {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(28, 20, 76)
            textSize = size
            typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
        }
        val width = (PAGE_W - 2 * MARGIN).toInt()
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_OPPOSITE)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setLineSpacing(0f, 1.12f)
            .build()
        canvas.save(); canvas.translate(MARGIN, y); layout.draw(canvas); canvas.restore()
        return y + layout.height + 5f
    }

    private fun write(context: Context, pdf: PdfDocument, filename: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "reports").apply { mkdirs() }
        val file = File(dir, filename)
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    fun share(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, "ارسال فایل PDF"))
    }
}
