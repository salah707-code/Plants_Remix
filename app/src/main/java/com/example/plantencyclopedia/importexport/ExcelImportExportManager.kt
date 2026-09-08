package com.example.plantencyclopedia.importexport

import android.content.Context
import android.net.Uri
import com.example.plantencyclopedia.data.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

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
    private val CSV_HEADERS = listOf(
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

    suspend fun previewExcelCsv(context: Context, sourceUri: Uri, existingPlants: List<Plant>): ImportPreviewResult = withContext(Dispatchers.IO) {
        val validPlants = mutableListOf<Plant>()
        val duplicates = mutableListOf<String>()
        val errors = mutableListOf<String>()
        var totalRows = 0

        val existingNames = existingPlants.map { it.name.trim().lowercase() }.toSet()

        try {
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    var lineIndex = 0
                    var line: String?

                    // Check for headers on first line
                    var headerIndices: Map<String, Int> = emptyMap()

                    while (reader.readLine().also { line = it } != null) {
                        val rawLine = line?.trim() ?: continue
                        if (rawLine.isBlank()) continue

                        val cleanedLine = if (lineIndex == 0 && rawLine.startsWith(UTF8_BOM)) {
                            rawLine.removePrefix(UTF8_BOM)
                        } else {
                            rawLine
                        }

                        val columns = parseCsvLine(cleanedLine)
                        if (lineIndex == 0) {
                            // Identify header map or fallback
                            headerIndices = mapHeaders(columns)
                            lineIndex++
                            continue
                        }

                        totalRows++
                        lineIndex++

                        try {
                            val name = getColumnValue(columns, headerIndices, "الاسم بالعربية", 0)
                            if (name.isBlank()) {
                                errors.add("سطر $lineIndex: تم تخطيه لعدم وجود اسم النبتة")
                                continue
                            }

                            val english = getColumnValue(columns, headerIndices, "الاسم بالإنجليزية", 1)
                            val scientific = getColumnValue(columns, headerIndices, "الاسم العلمي", 2)
                            val family = getColumnValue(columns, headerIndices, "الفصيلة", 3).ifBlank { "عامة" }
                            val usage = getColumnValue(columns, headerIndices, "الاستخدام", 4).ifBlank { "علاجية" }
                            val chemicalsRaw = getColumnValue(columns, headerIndices, "المواد الفعالة", 5)
                            val note = getColumnValue(columns, headerIndices, "الوصف والملاحظات", 6)
                            val habitat = getColumnValue(columns, headerIndices, "الموطن والبيئة", 7)
                            val partsUsed = getColumnValue(columns, headerIndices, "الأجزاء المستعملة", 8)
                            val preparation = getColumnValue(columns, headerIndices, "طريقة التحضير", 9)
                            val precautions = getColumnValue(columns, headerIndices, "محاذير الاستخدام", 10)
                            val growthForm = getColumnValue(columns, headerIndices, "طبيعة النمو", 11)

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
                            errors.add("سطر $lineIndex: خطأ أثناء تحليل البيانات (${e.message})")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            errors.add("فشل قراءة الملف: ${e.localizedMessage}")
        }

        ImportPreviewResult(
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

    private fun parseCsvLine(line: String): List<String> {
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
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }
}
