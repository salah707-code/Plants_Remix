package com.example.plantencyclopedia.network

import com.example.plantencyclopedia.data.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

sealed class GeminiResult {
    data class Success(val response: String) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
    object Offline : GeminiResult()
}

object GeminiBotanicalService {

    /**
     * Consult Gemini AI regarding a plant's botanical and medicinal properties.
     * Includes clear educational notices and handles offline gracefully.
     */
    suspend fun consultBotanicalAssistant(
        plant: Plant,
        userQuestion: String,
        apiKey: String = ""
    ): GeminiResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext GeminiResult.Error(
                "ميزة الذكاء الاصطناعي اختيارية وتتطلب إضافة مفتاح Google Gemini API في إعدادات التطبيق أو ملف التكوين. كافة وظائف الموسوعة المحلية تعمل بشكل كامل دون اتصال."
            )
        }

        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 20000
                doOutput = true
            }

            val prompt = """
                أنت خبير نباتي ومستشار متخصص في الأعشاب الطبية وعلم العقاقير (Pharmacognosy).
                بيانات النبتة الحالية:
                - الاسم الشائع: ${plant.name}
                - الاسم العلمي: ${plant.scientific}
                - الاسم بالإنجليزية: ${plant.english}
                - الفصيلة: ${plant.family}
                - الاستخدام المسجل: ${plant.usage}
                - المركبات الفعالة: ${plant.chemicals.joinToString("، ")}
                - الموطن والبيئة: ${plant.habitat}
                
                سؤال المستخدم أو الاستفسار:
                $userQuestion
                
                يرجى الإجابة بدقة علمية وموجزة باللغة العربية مع التركيز على:
                1. الفوائد الموثقة علمياً وتراثياً.
                2. محاذير الاستخدام والجرعات الآمنة والتداخلات الدوائية المحتملة إن وجدت.
                3. التنبيه إلى ضرورة استشارة الطبيب أو الصيدلي المختص قبل الاستخدام العلاجي.
            """.trimIndent()

            val requestBody = JSONObject().apply {
                val contentsArr = JSONArray()
                val contentObj = JSONObject()
                val partsArr = JSONArray()
                val partObj = JSONObject().apply {
                    put("text", prompt)
                }
                partsArr.put(partObj)
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
                put("contents", contentsArr)
            }

            OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readText()
                }

                val jsonResponse = JSONObject(responseText)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""
                    if (text.isNotBlank()) {
                        return@withContext GeminiResult.Success(text)
                    }
                }
                return@withContext GeminiResult.Error("لم يتم استلام نص من خادم الذكاء الاصطناعي.")
            } else {
                val errorStream = connection.errorStream
                val errorMsg = if (errorStream != null) {
                    BufferedReader(InputStreamReader(errorStream, StandardCharsets.UTF_8)).use { it.readText() }
                } else "رمز الخطأ: $responseCode"
                return@withContext GeminiResult.Error("خطأ في الاتصال بالذكاء الاصطناعي ($responseCode). يرجى التحقق من مفتاح API والاتصال.")
            }
        } catch (_: java.net.UnknownHostException) {
            GeminiResult.Offline
        } catch (e: Exception) {
            GeminiResult.Error("تعذر الاتصال بخدمة الذكاء الاصطناعي: ${e.localizedMessage ?: "تأكد من اتصالك بالإنترنت"}")
        }
    }
}
