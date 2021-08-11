package com.mattrobertson.greek.reader.db.repo

import com.mattrobertson.greek.reader.db.dao.ConcordanceDao

class ConcordanceRepo(private val concordanceDao: ConcordanceDao) {

	suspend fun getConcordanceEntries(lex: String) = concordanceDao.getConcordanceEntries(lex)

}