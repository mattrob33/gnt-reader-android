package com.mattrobertson.greek.reader.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mattrobertson.greek.reader.SblGntApplication
import com.mattrobertson.greek.reader.data.models.VerseEntity

@Database(
	entities = [VerseEntity::class],
	version = 1,
	exportSchema = false
)
abstract class VerseDatabase: RoomDatabase() {

	abstract fun versesDao(): VersesDao

	companion object {
		private const val DATABASE_NAME = "gnt"
		const val VERSES_TABLE = "verses"

		@Volatile
		private var instance: VerseDatabase? = null

		fun getInstance(): VerseDatabase {
			return instance ?: synchronized(this) {
				instance ?: buildDatabase().also { instance = it }
			}
		}

		private fun buildDatabase(): VerseDatabase {
			return Room.databaseBuilder(SblGntApplication.context, VerseDatabase::class.java, DATABASE_NAME)
				.createFromAsset("db/gnt.db")
				.build()
		}
	}

}