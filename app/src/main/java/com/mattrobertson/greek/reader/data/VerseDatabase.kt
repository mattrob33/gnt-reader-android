package com.mattrobertson.greek.reader.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

		fun getInstance(context: Context): VerseDatabase {
			return instance ?: synchronized(this) {
				instance ?: buildDatabase(context).also { instance = it }
			}
		}

		private fun buildDatabase(context: Context): VerseDatabase {
			return Room.databaseBuilder(context, VerseDatabase::class.java, DATABASE_NAME)
				.createFromAsset("db/gnt.db")
				.build()
		}
	}

}