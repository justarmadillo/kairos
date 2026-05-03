package com.kairos.features.cases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Html
import com.kairos.core.model.Case
import com.kairos.core.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CasePdfExporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun export(case: Case): File = withContext(Dispatchers.IO) {
        val outDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val patientName = case.patient?.name?.sanitizeFileName()?.ifBlank { "patient" } ?: "patient"
        val file = File(outDir, "${patientName}_case_${case.id}.pdf")

        val document = PdfDocument()
        try {
            val writer = PdfWriter(document)
            writer.start()
            writer.drawReport(case)
            writer.finish()

            FileOutputStream(file).use { output ->
                document.writeTo(output)
            }
        } finally {
            document.close()
        }

        file
    }
}

private class PdfWriter(
    private val document: PdfDocument,
) {
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 40f
    private val contentWidth = pageWidth - margin * 2
    private var pageNumber = 0
    private lateinit var page: PdfDocument.Page
    private lateinit var canvas: Canvas
    private var y = margin

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(20, 20, 19)
        textSize = 23f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }
    private val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(204, 120, 92)
        textSize = 12.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(108, 106, 100)
        textSize = 10.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(37, 37, 35)
        textSize = 10.5f
    }
    private val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(108, 106, 100)
        textSize = 9.5f
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(230, 223, 216)
        strokeWidth = 1f
    }
    private val imageBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(230, 223, 216)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    fun start() {
        newPage()
    }

    fun finish() {
        drawFooter()
        document.finishPage(page)
    }

    fun drawReport(case: Case) {
        val patient = case.patient
        drawTitle(patient?.name?.takeIf { it.isNotBlank() } ?: "Patient Case")
        drawSmallText("Generated ${dateTime(Date().time)}")
        space(18f)

        drawSection("Patient")
        drawKeyValue("Name", patient?.name ?: "Unknown patient")
        drawKeyValue("Age", patient?.age?.toString() ?: "Not recorded")
        drawKeyValue(
            label = "Phone",
            value = patient?.phones?.takeIf { it.isNotEmpty() }
                ?.joinToString("\n") { phone ->
                    phone.number + if (!phone.label.isNullOrBlank()) " (${phone.label})" else ""
                }
                ?: "Not recorded",
        )

        drawSection("Case")
        drawKeyValue("Case date", fullDate(case.caseDate))
        drawKeyValue(
            label = "Diagnoses",
            value = case.diagnoses.takeIf { it.isNotEmpty() }
                ?.joinToString(", ") { it.name }
                ?: "Not recorded",
        )
        drawKeyValue("Mechanism", case.mechanism?.takeIf { it.isNotBlank() } ?: "Not recorded")

        val notes = case.notesHtml?.toPlainText()?.takeIf { it.isNotBlank() }
        if (notes != null) {
            drawSection("Notes")
            drawWrapped(notes, bodyPaint, contentWidth)
            space(12f)
        }

        val photos = case.media.filter { it.mediaType == MediaType.IMAGE }
        if (photos.isNotEmpty()) {
            drawSection("Photos")
            photos.forEachIndexed { index, media ->
                drawPhoto(File(media.filePath), "Photo ${index + 1}")
            }
        }
    }

    private fun drawTitle(text: String) {
        ensureSpace(38f)
        canvas.drawText(text, margin, y + titlePaint.textSize, titlePaint)
        y += 36f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 12f
    }

    private fun drawSection(text: String) {
        ensureSpace(34f)
        if (y > margin + 60f) {
            space(8f)
        }
        canvas.drawText(text.uppercase(Locale.getDefault()), margin, y + sectionPaint.textSize, sectionPaint)
        y += 24f
    }

    private fun drawKeyValue(label: String, value: String) {
        val labelWidth = 82f
        val valueWidth = contentWidth - labelWidth
        val lines = value.lines().flatMap { it.wrap(bodyPaint, valueWidth) }
        val height = (lines.size.coerceAtLeast(1) * 14f) + 8f
        ensureSpace(height)

        canvas.drawText(label, margin, y + labelPaint.textSize, labelPaint)
        if (lines.isEmpty()) {
            canvas.drawText("", margin + labelWidth, y + bodyPaint.textSize, bodyPaint)
        } else {
            lines.forEachIndexed { index, line ->
                canvas.drawText(line, margin + labelWidth, y + bodyPaint.textSize + (index * 14f), bodyPaint)
            }
        }
        y += height
    }

    private fun drawWrapped(text: String, paint: Paint, width: Float) {
        text.split('\n').forEach { paragraph ->
            val lines = paragraph.wrap(paint, width)
            val height = (lines.size.coerceAtLeast(1) * 14f) + 4f
            ensureSpace(height)
            if (lines.isEmpty()) {
                y += 10f
            } else {
                lines.forEach { line ->
                    canvas.drawText(line, margin, y + paint.textSize, paint)
                    y += 14f
                }
                y += 4f
            }
        }
    }

    private fun drawPhoto(file: File, caption: String) {
        if (!file.exists()) return
        val bitmap = file.decodeSampledBitmap(maxDimension = 1300) ?: return
        try {
            val maxImageHeight = 250f
            val scale = minOf(contentWidth / bitmap.width, maxImageHeight / bitmap.height)
            val drawWidth = bitmap.width * scale
            val drawHeight = bitmap.height * scale
            ensureSpace(drawHeight + 30f)

            val left = margin + (contentWidth - drawWidth) / 2f
            val top = y
            val rect = RectF(left, top, left + drawWidth, top + drawHeight)
            canvas.drawBitmap(bitmap, null, rect, null)
            canvas.drawRect(rect, imageBorderPaint)
            y += drawHeight + 14f
            canvas.drawText(caption, margin, y + mutedPaint.textSize, mutedPaint)
            y += 18f
        } finally {
            bitmap.recycle()
        }
    }

    private fun drawSmallText(text: String) {
        ensureSpace(18f)
        canvas.drawText(text, margin, y + mutedPaint.textSize, mutedPaint)
        y += 18f
    }

    private fun space(amount: Float) {
        ensureSpace(amount)
        y += amount
    }

    private fun ensureSpace(required: Float) {
        if (y + required <= pageHeight - margin - 24f) return
        drawFooter()
        document.finishPage(page)
        newPage()
    }

    private fun newPage() {
        pageNumber += 1
        page = document.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        )
        canvas = page.canvas
        canvas.drawColor(Color.rgb(250, 249, 245))
        y = margin
    }

    private fun drawFooter() {
        canvas.drawLine(margin, pageHeight - 34f, pageWidth - margin, pageHeight - 34f, linePaint)
        canvas.drawText("Page $pageNumber", pageWidth - margin - 42f, pageHeight - 18f, mutedPaint)
    }
}

private fun String.wrap(paint: Paint, width: Float): List<String> {
    if (isBlank()) return emptyList()
    val lines = mutableListOf<String>()
    var remaining = trim()
    while (remaining.isNotEmpty()) {
        val count = paint.breakText(remaining, true, width, null)
        if (count >= remaining.length) {
            lines += remaining
            break
        }

        val candidate = remaining.take(count)
        val breakAt = candidate.lastIndexOf(' ').takeIf { it > 0 } ?: count
        lines += remaining.take(breakAt).trimEnd()
        remaining = remaining.drop(breakAt).trimStart()
    }
    return lines
}

private fun File.decodeSampledBitmap(maxDimension: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(absolutePath, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sampleSize = 1
    while (bounds.outWidth / sampleSize > maxDimension || bounds.outHeight / sampleSize > maxDimension) {
        sampleSize *= 2
    }

    return BitmapFactory.decodeFile(
        absolutePath,
        BitmapFactory.Options().apply { inSampleSize = sampleSize },
    )
}

private fun String.toPlainText(): String =
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString().trim()

private fun String.sanitizeFileName(): String =
    replace(Regex("[^A-Za-z0-9._-]+"), "_").trim('_')

private fun fullDate(ms: Long): String =
    SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date(ms))

private fun dateTime(ms: Long): String =
    SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(ms))
