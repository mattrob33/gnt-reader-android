package com.mattrobertson.greek.reader.db.api.mappers

import com.mattrobertson.greek.reader.db.api.models.ConcordanceModel
import com.mattrobertson.greek.reader.db.api.models.GlossModel
import com.mattrobertson.greek.reader.db.api.models.VerseModel
import com.mattrobertson.greek.reader.db.internal.models.ConcordanceEntity
import com.mattrobertson.greek.reader.db.internal.models.GlossEntity
import com.mattrobertson.greek.reader.db.internal.models.VerseEntity

internal fun mapConcordanceEntity(entity: ConcordanceEntity) = ConcordanceModel(
    entity.id,
    entity.lex,
    entity.book,
    entity.chapter,
    entity.verse
)

internal fun mapGlossEntity(entity: GlossEntity) = GlossModel(
    entity.lex,
    entity.gloss,
    entity.occ
)

internal fun mapVerseEntity(entity: VerseEntity) = VerseModel(
    entity.book,
    entity.chapter,
    entity.verse,
    entity.encodedText
)