package com.mattrobertson.greek.reader.mappers

object ParsingParser {
	/**
	 * Given a coded parsing form (e.g. "X-") return the human-readable form (e.g. "Particle")
	 */
	fun parse(codedParsing: String): String {
		val codedParsingParts: Array<String> = codedParsing.split(" ").toTypedArray()

		if (codedParsingParts.size < 2) return "[parsing unavailable]"

		val typeCode = codedParsingParts[0]
		val info = codedParsingParts[1]

		return when (typeCode) {
			"A-" -> "Adj-" + info[4] + info[6] + info[5]
			"C-" -> "Conj"
			"D-" -> "Adv"
			"N-" -> "Noun-" + info[4] + info[6] + info[5]
			"P-" -> "Prep"
			"RA" -> "Art " + info[4] + info[6] + info[5]
			"V-" -> {
				var tense = info[1].toString()
				if (tense == "P") tense = "Pres."
				if (tense == "I") tense = "Impf."
				if (tense == "F") tense = "Fut."
				if (tense == "A") tense = "Aor."
				if (tense == "X") tense = "Pf."
				if (tense == "Y") tense = "Plu."

				var voice = info[2].toString()
				if (voice == "A") voice = "Act."
				if (voice == "M") voice = "Mid."
				if (voice == "P") voice = "Pas."

				var mood = info[3].toString()
				when(mood) {
					"P" -> "Ptc. " + tense + " " + voice + " " + info[4] + info[6] + info[5]
					"N" -> "Infv. $tense $voice"
					else -> {
						when (mood) {
							"I" -> mood = "Ind."
							"D" -> mood = "Impv."
							"O" -> mood = "Opt."
						}
						"Vb. " + tense + " " + voice + " " + mood + " " + info[0] + info[5]
					}
				}
			}
			"X-", "I-" -> "Particle"
			else -> ""
		}
	}
}