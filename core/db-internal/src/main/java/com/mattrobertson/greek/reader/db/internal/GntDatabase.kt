package com.mattrobertson.greek.reader.db.internal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mattrobertson.greek.reader.db.internal.dao.ConcordanceDao
import com.mattrobertson.greek.reader.db.internal.dao.GlossesDao
import com.mattrobertson.greek.reader.db.internal.dao.VersesDao
import com.mattrobertson.greek.reader.db.internal.dao.VocabDao
import com.mattrobertson.greek.reader.db.internal.models.ConcordanceEntity
import com.mattrobertson.greek.reader.db.internal.models.GlossEntity
import com.mattrobertson.greek.reader.db.internal.models.VerseEntity

@Database(
	entities = [
		VerseEntity::class,
		GlossEntity::class,
		ConcordanceEntity::class
   ],
	version = 1,
	exportSchema = false
)
abstract class GntDatabase: RoomDatabase() {

	abstract fun versesDao(): VersesDao
	abstract fun glossesDao(): GlossesDao
	abstract fun concordanceDao(): ConcordanceDao
	abstract fun vocabDao(): VocabDao

	companion object {
		private const val DATABASE_NAME = "gnt-app"

		const val VERSES_TABLE = "verses"
		const val GLOSSES_TABLE = "glosses"
		const val CONCORDANCE_TABLE = "concordance"

		@Volatile
		private var instance: GntDatabase? = null

		fun getInstance(context: Context): GntDatabase {
			return instance ?: synchronized(this) {
				instance ?: buildDatabase(context).also { instance = it }
			}
		}

		private fun buildDatabase(context: Context): GntDatabase {
			return Room.databaseBuilder(context, GntDatabase::class.java, DATABASE_NAME)
				.createFromAsset("db/gnt-app.db")
				.build()
		}
	}

}