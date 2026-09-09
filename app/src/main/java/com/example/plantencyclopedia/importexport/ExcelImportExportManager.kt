package com.example.plantencyclopedia.importexport

import android.content.Context
import android.net.Uri
import android.util.Xml
import com.example.plantencyclopedia.data.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

enum class ImportMode {
    UPDATE_EXISTING, // دمج وتحديث السجلات الموجودة
    ADD_NEW,         // إضافة كسجلات جديدة
    SKIP_DUPLICATES  // تجاهل السجلات المكررة
}

data class ImportPreviewResult(
    val totalRows: Int,
    val validPlants: List<Plant>,
    val duplicateNames: List<String>,
    val errors: List<String>
)

object ExcelImportExportManager {

    // CSV with UTF-8 BOM for 100% native Arabic Excel compatibility
    private const val UTF8_BOM = "\uFEFF"
    val CSV_HEADERS = listOf(
        "الاسم بالعربية",
        "الاسم بالإنجليزية",
        "الاسم العلمي",
        "الفصيلة",
        "الاستخدام",
        "المواد الفعالة",
        "الوصف والملاحظات",
        "الموطن والبيئة",
        "الأجزاء المستعملة",
        "طريقة التحضير",
        "محاذير الاستخدام",
        "طبيعة النمو"
    )

    suspend fun exportToExcelCsv(context: Context, destinationUri: Uri, plants: List<Plant>): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    // Write BOM so Excel opens UTF-8 Arabic correctly
                    writer.write(UTF8_BOM)

                    // Write Headers
                    writer.write(CSV_HEADERS.joinToString(",") { escapeCsv(it) })
                    writer.write("\r\n")

                    // Write rows
                    for (plant in plants) {
                        val row = listOf(
                            plant.name,
                            plant.english,
                            plant.scientific,
                            plant.family,
                            plant.usage,
                            plant.chemicals.joinToString("; "),
                            plant.note,
                            plant.habitat,
                            plant.partsUsed,
                            plant.preparation,
                            plant.precautions,
                            plant.growthForm
                        )
                        writer.write(row.joinToString(",") { escapeCsv(it) })
                        writer.write("\r\n")
                    }
                    writer.flush()
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun previewExcel(context: Context, sourceUri: Uri, existingPlants: List<Plant>): ImportPreviewResult = withContext(Dispatchers.IO) {
        try {
            val bytes = context.contentResolver.openInputStream(sourceUri)?.use { it.readBytes() }
                ?: return@withContext ImportPreviewResult(0, emptyList(), emptyList(), listOf("تعذر فتح ملف الإكسل المحدد"))

            // Check if file is a ZIP archive (.xlsx file format)
            if (isZipFile(bytes)) {
                return@withContext parseXlsxBytes(bytes, existingPlants)
            } else {
                return@withContext parseDelimitedText(String(bytes, StandardCharsets.UTF_8), existingPlants)
            }
        } catch (e: Exception) {
            ImportPreviewResult(0, emptyList(), emptyList(), listOf("خطأ أثناء قراءة ملف إكسل: ${e.localizedMessage}"))
        }
    }

    // Direct text import preview (e.g. from pasted Excel clipboard rows)
    fun previewPastedText(text: String, existingPlants: List<Plant>): ImportPreviewResult {
        return parseDelimitedText(text, existingPlants)
    }

    // Alias for backward compatibility
    suspend fun previewExcelCsv(context: Context, sourceUri: Uri, existingPlants: List<Plant>): ImportPreviewResult {
        return previewExcel(context, sourceUri, existingPlants)
    }

    private fun isZipFile(bytes: ByteArray): Boolean {
        if (bytes.size < 4) return false
        // ZIP magic bytes: 'P', 'K', 0x03, 0x04
        return bytes[0] == 'P'.code.toByte() && bytes[1] == 'K'.code.toByte() &&
                bytes[2] == 3.toByte() && bytes[3] == 4.toByte()
    }

    private fun parseXlsxBytes(bytes: ByteArray, existingPlants: List<Plant>): ImportPreviewResult {
        val sharedStrings = mutableListOf<String>()
        var sheetBytes: ByteArray? = null

        // First pass: extract shared strings and find first worksheet
        ZipInputStream(ByteArrayInputStream(bytes)).use { zis ->
            var entry: ZipEntry?
            while (zis.nextEntry.also { entry = it } != null) {
                val name = entry?.name ?: ""
                when {
                    name.equals("xl/sharedStrings.xml", ignoreCase = true) -> {
                        sharedStrings.addAll(parseSharedStrings(zis))
                    }
                    name.startsWith("xl/worksheets/sheet", ignoreCase = true) && sheetBytes == null -> {
                        sheetBytes = zis.readBytes()
                    }
                }
            }
        }

        if (sheetBytes == null) {
            return ImportPreviewResult(0, emptyList(), emptyList(), listOf("لم يتم العثور على أوراق بيانات (Worksheets) في ملف Excel"))
        }

        // Second pass: parse sheet rows
        val rawRows = parseSheetXml(ByteArrayInputStream(sheetBytes), sharedStrings)
        return processParsedRows(rawRows, existingPlants)
    }

    private fun parseSharedStrings(stream: InputStream): List<String> {
        val list = mutableListOf<String>()
        try {
            val parser = Xml.newPullParser()
            parser.setInput(stream, "UTF-8")
            var eventType = parser.eventType
            var inT = false
            val currentText = StringBuilder()

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name.equals("t", ignoreCase = true)) {
                            inT = true
                            currentText.clear()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (inT) {
                            currentText.append(parser.text)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name.equals("t", ignoreCase = true)) {
                            inT = false
                        } else if (parser.name.equals("si", ignoreCase = true)) {
                            list.add(currentText.toString())
                            currentText.clear()
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (_: Exception) {}
        return list
    }

    private fun parseSheetXml(stream: InputStream, sharedStrings: List<String>): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        try {
            val parser = Xml.newPullParser()
            parser.setInput(stream, "UTF-8")
            var eventType = parser.eventType

            var currentRow = mutableMapOf<Int, String>()
            var currentCellCol = -1
            var cellType = ""
            var inV = false
            var inInlineT = false
            var cellValue = StringBuilder()

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name.lowercase()) {
                            "row" -> {
                                currentRow = mutableMapOf()
                            }
                            "c" -> {
                                val cellRef = parser.getAttributeValue(null, "r") ?: ""
                                currentCellCol = columnRefToIndex(cellRef)
                                cellType = parser.getAttributeValue(null, "t") ?: ""
                                cellValue.clear()
                            }
                            "v" -> inV = true
                            "t" -> inInlineT = true
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (inV || inInlineT) {
                            cellValue.append(parser.text)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        when (parser.name.lowercase()) {
                            "v" -> inV = false
                            "t" -> inInlineT = false
                            "c" -> {
                                val text = if (cellType == "s") {
                                    val idx = cellValue.toString().toIntOrNull()
                                    if (idx != null && idx in sharedStrings.indices) sharedStrings[idx] else ""
                                } else {
                                    cellValue.toString()
                                }
                                if (currentCellCol >= 0) {
                                    currentRow[currentCellCol] = text
                                }
                            }
                            "row" -> {
                                if (currentRow.isNotEmpty()) {
                                    val maxCol = currentRow.keys.maxOrNull() ?: 0
                                    val rowList = (0..maxCol).map { colIdx -> currentRow[colIdx] ?: "" }
                                    rows.add(rowList)
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (_: Exception) {}
        return rows
    }

    private fun columnRefToIndex(ref: String): Int {
        var col = 0
        var foundLetters = false
        for (c in ref.uppercase()) {
            if (c in 'A'..'Z') {
                foundLetters = true
                col = col * 26 + (c - 'A' + 1)
            } else {
                break
            }
        }
        return if (foundLetters) col - 1 else -1
    }

    private fun parseDelimitedText(text: String, existingPlants: List<Plant>): ImportPreviewResult {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return ImportPreviewResult(0, emptyList(), emptyList(), listOf("النص المدخل فارغ"))
        }

        val firstLine = lines.first().removePrefix(UTF8_BOM)
        val delimiter = detectDelimiter(firstLine)

        val rows = lines.map { line ->
            parseLineWithDelimiter(line.removePrefix(UTF8_BOM), delimiter)
        }

        return processParsedRows(rows, existingPlants)
    }

    private fun detectDelimiter(line: String): Char {
        val tabs = line.count { it == '\t' }
        val semicolons = line.count { it == ';' }
        val commas = line.count { it == ',' }

        return when {
            tabs >= semicolons && tabs >= commas && tabs > 0 -> '\t'
            semicolons >= commas && semicolons > 0 -> ';'
            else -> ','
        }
    }

    private fun parseLineWithDelimiter(line: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    sb.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == delimiter && !inQuotes) {
                result.add(sb.toString().trim())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        result.add(sb.toString().trim())
        return result
    }

    private fun processParsedRows(rows: List<List<String>>, existingPlants: List<Plant>): ImportPreviewResult {
        val validPlants = mutableListOf<Plant>()
        val duplicates = mutableListOf<String>()
        val errors = mutableListOf<String>()
        var totalRows = 0

        val existingNames = existingPlants.map { it.name.trim().lowercase() }.toSet()

        if (rows.isEmpty()) {
            return ImportPreviewResult(0, emptyList(), emptyList(), listOf("لم يتم العثور على أسطر صالحة في الملف"))
        }

        val headerMap = mapHeaders(rows.first())
        val dataRows = rows.drop(1)

        for ((index, columns) in dataRows.withIndex()) {
            val lineNum = index + 2
            if (columns.all { it.isBlank() }) continue

            totalRows++
            try {
                val name = getColumnValue(columns, headerMap, "الاسم بالعربية", 0)
                if (name.isBlank()) {
                    errors.add("سطر $lineNum: تم تخطيه لعدم وجود اسم النبتة")
                    continue
                }

                val english = getColumnValue(columns, headerMap, "الاسم بالإنجليزية", 1)
                val scientific = getColumnValue(columns, headerMap, "الاسم العلمي", 2)
                val family = getColumnValue(columns, headerMap, "الفصيلة", 3).ifBlank { "عامة" }
                val usage = getColumnValue(columns, headerMap, "الاستخدام", 4).ifBlank { "علاجية" }
                val chemicalsRaw = getColumnValue(columns, headerMap, "المواد الفعالة", 5)
                val note = getColumnValue(columns, headerMap, "الوصف والملاحظات", 6)
                val habitat = getColumnValue(columns, headerMap, "الموطن والبيئة", 7)
                val partsUsed = getColumnValue(columns, headerMap, "الأجزاء المستعملة", 8)
                val preparation = getColumnValue(columns, headerMap, "طريقة التحضير", 9)
                val precautions = getColumnValue(columns, headerMap, "محاذير الاستخدام", 10)
                val growthForm = getColumnValue(columns, headerMap, "طبيعة النمو", 11)

                val chemicals = if (chemicalsRaw.isNotBlank()) {
                    chemicalsRaw.split(";", "،", ",").map { it.trim() }.filter { it.isNotEmpty() }
                } else emptyList()

                if (existingNames.contains(name.lowercase())) {
                    duplicates.add(name)
                }

                val plant = Plant(
                    name = name,
                    english = english,
                    scientific = scientific,
                    family = family,
                    usage = usage,
                    chemicals = chemicals,
                    note = note,
                    image = "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85",
                    habitat = habitat,
                    partsUsed = partsUsed,
                    preparation = preparation,
                    precautions = precautions,
                    growthForm = growthForm
                )
                validPlants.add(plant)
            } catch (e: Exception) {
                errors.add("سطر $lineNum: خطأ أثناء معالجة البيانات (${e.message})")
            }
        }

        return ImportPreviewResult(
            totalRows = totalRows,
            validPlants = validPlants,
            duplicateNames = duplicates,
            errors = errors
        )
    }

    private fun mapHeaders(headers: List<String>): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        headers.forEachIndexed { index, header ->
            val h = header.trim().lowercase()
            when {
                h.contains("اسم") && !h.contains("علمي") && !h.contains("إنجليز") && !h.contains("انجليز") -> map["الاسم بالعربية"] = index
                h.contains("إنجليز") || h.contains("انجليز") || h.contains("english") -> map["الاسم بالإنجليزية"] = index
                h.contains("علمي") || h.contains("scientific") -> map["الاسم العلمي"] = index
                h.contains("فصيل") || h.contains("family") -> map["الفصيلة"] = index
                h.contains("استخدام") || h.contains("usage") -> map["الاستخدام"] = index
                h.contains("ماد") || h.contains("مواد") || h.contains("مركب") || h.contains("chemical") -> map["المواد الفعالة"] = index
                h.contains("وصف") || h.contains("ملاحظ") || h.contains("note") -> map["الوصف والملاحظات"] = index
                h.contains("موطن") || h.contains("بيئ") || h.contains("habitat") -> map["الموطن والبيئة"] = index
                h.contains("أجزاء") || h.contains("اجزاء") || h.contains("part") -> map["الأجزاء المستعملة"] = index
                h.contains("تحضير") || h.contains("preparation") -> map["طريقة التحضير"] = index
                h.contains("محاذير") || h.contains("precaution") -> map["محاذير الاستخدام"] = index
                h.contains("نمو") || h.contains("growth") -> map["طبيعة النمو"] = index
            }
        }
        return map
    }

    private fun getColumnValue(columns: List<String>, headerMap: Map<String, Int>, headerName: String, fallbackIndex: Int): String {
        val mappedIndex = headerMap[headerName] ?: fallbackIndex
        return if (mappedIndex in columns.indices) {
            columns[mappedIndex].trim()
        } else ""
    }

    private fun escapeCsv(value: String): String {
        val containsSpecial = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")
        return if (containsSpecial) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}

