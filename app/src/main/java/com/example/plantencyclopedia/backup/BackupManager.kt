package com.example.plantencyclopedia.backup

import android.content.Context
import android.net.Uri
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.images.ImageStorageManager
import com.example.plantencyclopedia.settings.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class BackupRestoreSummary(
    val plantCount: Int,
    val imagesCount: Int,
    val dateEpoch: Long = System.currentTimeMillis()
)

object BackupManager {

    suspend fun createFullBackup(
        context: Context,
        destinationUri: Uri,
        plants: List<Plant>,
        preferencesManager: PreferencesManager
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val outputStream = context.contentResolver.openOutputStream(destinationUri) ?: return@withContext false

            ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                // 1. Write Plants JSON
                val plantsJson = plantsToJson(plants)
                val plantEntry = ZipEntry("plants_backup.json")
                zipOut.putNextEntry(plantEntry)
                zipOut.write(plantsJson.toByteArray(StandardCharsets.UTF_8))
                zipOut.closeEntry()

                // 2. Write Settings JSON
                val settingsJson = preferencesManager.getExportableSettingsJson()
                val settingsEntry = ZipEntry("settings_backup.json")
                zipOut.putNextEntry(settingsEntry)
                zipOut.write(settingsJson.toByteArray(StandardCharsets.UTF_8))
                zipOut.closeEntry()

                // 3. Write Local Image Files
                val localImages = ImageStorageManager.getAllLocalImageFiles(context)
                for (imageFile in localImages) {
                    try {
                        val imgEntry = ZipEntry("images/${imageFile.name}")
                        zipOut.putNextEntry(imgEntry)
                        FileInputStream(imageFile).use { fis ->
                            fis.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                    } catch (_: Exception) {}
                }
                zipOut.flush()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun restoreFullBackup(
        context: Context,
        sourceUri: Uri,
        preferencesManager: PreferencesManager
    ): Pair<List<Plant>, BackupRestoreSummary>? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
            val restoredPlants = mutableListOf<Plant>()
            var restoredImagesCount = 0
            val imagesDir = ImageStorageManager.getImagesDirectory(context)

            ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                var entry: ZipEntry?
                while (zipIn.nextEntry.also { entry = it } != null) {
                    val currentEntry = entry ?: break
                    val entryName = currentEntry.name

                    when {
                        entryName == "plants_backup.json" -> {
                            val baos = ByteArrayOutputStream()
                            zipIn.copyTo(baos)
                            val jsonString = baos.toString(StandardCharsets.UTF_8.name())
                            restoredPlants.addAll(jsonToPlants(jsonString))
                        }
                        entryName == "settings_backup.json" -> {
                            val baos = ByteArrayOutputStream()
                            zipIn.copyTo(baos)
                            val jsonString = baos.toString(StandardCharsets.UTF_8.name())
                            preferencesManager.importSettingsFromJson(jsonString)
                        }
                        entryName.startsWith("images/") && !currentEntry.isDirectory -> {
                            val fileName = File(entryName).name
                            val destFile = File(imagesDir, fileName)
                            FileOutputStream(destFile).use { fos ->
                                zipIn.copyTo(fos)
                            }
                            restoredImagesCount++
                        }
                    }
                    zipIn.closeEntry()
                }
            }

            if (restoredPlants.isEmpty()) {
                return@withContext null
            }

            val summary = BackupRestoreSummary(
                plantCount = restoredPlants.size,
                imagesCount = restoredImagesCount
            )
            Pair(restoredPlants, summary)
        } catch (_: Exception) {
            null
        }
    }

    private fun plantsToJson(plants: List<Plant>): String {
        val root = JSONArray()
        for (plant in plants) {
            val obj = JSONObject()
            obj.put("id", plant.id)
            obj.put("name", plant.name)
            obj.put("english", plant.english)
            obj.put("scientific", plant.scientific)
            obj.put("family", plant.family)
            obj.put("usage", plant.usage)
            obj.put("note", plant.note)
            obj.put("image", plant.image)
            obj.put("habitat", plant.habitat)
            obj.put("partsUsed", plant.partsUsed)
            obj.put("preparation", plant.preparation)
            obj.put("precautions", plant.precautions)
            obj.put("growthForm", plant.growthForm)
            obj.put("isFavorite", plant.isFavorite)
            obj.put("createdAt", plant.createdAt)
            obj.put("updatedAt", plant.updatedAt)
            obj.put("viewCount", plant.viewCount)

            val chemArr = JSONArray()
            plant.chemicals.forEach { chemArr.put(it) }
            obj.put("chemicals", chemArr)

            val imgArr = JSONArray()
            plant.images.forEach { imgArr.put(it) }
            obj.put("images", imgArr)

            root.put(obj)
        }
        return root.toString(2)
    }

    private fun jsonToPlants(json: String): List<Plant> {
        val list = mutableListOf<Plant>()
        val root = JSONArray(json)
        for (i in 0 until root.length()) {
            val obj = root.getJSONObject(i)

            val chemList = mutableListOf<String>()
            val chemArr = obj.optJSONArray("chemicals")
            if (chemArr != null) {
                for (j in 0 until chemArr.length()) {
                    chemList.add(chemArr.getString(j))
                }
            }

            val imgList = mutableListOf<String>()
            val imgArr = obj.optJSONArray("images")
            if (imgArr != null) {
                for (k in 0 until imgArr.length()) {
                    imgList.add(imgArr.getString(k))
                }
            }

            val plant = Plant(
                id = obj.optInt("id", 0),
                name = obj.optString("name", ""),
                english = obj.optString("english", ""),
                scientific = obj.optString("scientific", ""),
                family = obj.optString("family", ""),
                usage = obj.optString("usage", ""),
                chemicals = chemList,
                note = obj.optString("note", ""),
                image = obj.optString("image", ""),
                images = imgList,
                habitat = obj.optString("habitat", ""),
                partsUsed = obj.optString("partsUsed", ""),
                preparation = obj.optString("preparation", ""),
                precautions = obj.optString("precautions", ""),
                growthForm = obj.optString("growthForm", ""),
                isFavorite = obj.optBoolean("isFavorite", false),
                createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                viewCount = obj.optInt("viewCount", 0)
            )
            list.add(plant)
        }
        return list
    }
}
