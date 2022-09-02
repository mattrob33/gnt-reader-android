package com.mattrobertson.greek.reader.db.api.repo

import com.mattrobertson.greek.reader.db.internal.dao.GlossesDao
import javax.inject.Inject

class GlossesRepo @Inject constructor(private val glossesDao: GlossesDao) {

	suspend fun getGloss(lex: String) = glossesDao.getGloss(lex)

}