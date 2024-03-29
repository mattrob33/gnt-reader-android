package com.mattrobertson.greek.reader.db.api.repo

import com.mattrobertson.greek.reader.db.api.mappers.mapConcordanceEntity
import com.mattrobertson.greek.reader.db.api.models.ConcordanceModel
import com.mattrobertson.greek.reader.db.internal.dao.ConcordanceDao
import javax.inject.Inject

class ConcordanceRepo @Inject constructor(private val concordanceDao: ConcordanceDao) {

	suspend fun getConcordanceEntries(lex: String): List<ConcordanceModel> {
		return concordanceDao.getConcordanceEntries(lex).map {
			mapConcordanceEntity(it)
		}
	}


}