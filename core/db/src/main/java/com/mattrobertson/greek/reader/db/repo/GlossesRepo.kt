package com.mattrobertson.greek.reader.db.repo

import com.mattrobertson.greek.reader.db.dao.GlossesDao

class GlossesRepo(private val glossesDao: GlossesDao) {

	suspend fun getGloss(lex: String) = glossesDao.getGloss(lex)

}