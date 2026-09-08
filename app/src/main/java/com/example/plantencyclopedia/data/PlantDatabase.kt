package com.example.plantencyclopedia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Plant::class, PlantHistory::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to plants table safely
                db.execSQL("ALTER TABLE plants ADD COLUMN images TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN habitat TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN partsUsed TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN preparation TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN precautions TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN growthForm TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE plants ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE plants ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE plants ADD COLUMN viewCount INTEGER NOT NULL DEFAULT 0")

                // Create history table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS plant_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        plantId INTEGER NOT NULL,
                        viewedAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // Sync existing images column with initial single image
                try {
                    db.execSQL("UPDATE plants SET images = image WHERE (images IS NULL OR images = '') AND image != ''")
                } catch (_: Exception) {}
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_encyclopedia_database"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(PlantDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class PlantDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        database.plantDao().insertAll(InitialPlantData.defaultPlants)
                    }
                }
            }
        }
    }
}

