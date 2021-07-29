package com.mattrobertson.greek.reader.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mattrobertson.greek.reader.data.dao.ConcordanceDao
import com.mattrobertson.greek.reader.data.dao.GlossesDao
import com.mattrobertson.greek.reader.data.dao.VersesDao
import com.mattrobertson.greek.reader.data.models.ConcordanceEntity
import com.mattrobertson.greek.reader.data.models.GlossEntity
import com.mattrobertson.greek.reader.data.models.VerseEntity

@Database(
	entities = [
		VerseEntity::class,
		GlossEntity::class,
		ConcordanceEntity::class
   ],
	version = 1,
	exportSchema = false
)
abstract class VerseDatabase: RoomDatabase() {

	abstract fun versesDao(): VersesDao
	abstract fun glossesDao(): GlossesDao
	abstract fun concordanceDao(): ConcordanceDao

	companion object {
		private const val DATABASE_NAME = "gnt-app"

		const val VERSES_TABLE = "verses"
		const val GLOSSES_TABLE = "glosses"
		const val CONCORDANCE_TABLE = "concordance"

		@Volatile
		private var instance: VerseDatabase? = null

		fun getInstance(context: Context): VerseDatabase {
			return instance ?: synchronized(this) {
				instance ?: buildDatabase(context).also { instance = it }
			}
		}

		private fun buildDatabase(context: Context): VerseDatabase {
			return Room.databaseBuilder(context, VerseDatabase::class.java, DATABASE_NAME)
				.createFromAsset("db/gnt-app.db")
				.build()
		}
	}

}