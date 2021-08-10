package com.mattrobertson.greek.reader.verseref

class WordParsing private constructor(
	val humanReadable: String
) {

	companion object {

		fun decode(codedParsing: String) = WordParsing (
			humanReadable = parse(codedParsing)
		)

		/**
		 * Given a coded parsing form (e.g. "X-") return the human-readable form (e.g. "Particle")
		 */
		private fun parse(codedParsing: String): String {
			val typeCode = codedParsing.subSequence(0, 2)
			return when (typeCode) {
				"A-" -> "Adj-" + codedParsing[6] + codedParsing[8] + codedParsing[7]
				"C-" -> "Conj"
				"D-" -> "Adv"
				"N-" -> "Noun-" + codedParsing[6] + codedParsing[8] + codedParsing[7]
				"P-" -> "Prep"
				"RA" -> "Art " + codedParsing[6] + codedParsing[8] + codedParsing[7]
				"V-" -> {
					var tense = codedParsing[3].toString()
					if (tense == "P") tense = "Pres."
					if (tense == "I") tense = "Impf."
					if (tense == "F") tense = "Fut."
					if (tense == "A") tense = "Aor."
					if (tense == "X") tense = "Pf."
					if (tense == "Y") tense = "Plu."

					var voice = codedParsing[4].toString()
					if (voice == "A") voice = "Act."
					if (voice == "M") voice = "Mid."
					if (voice == "P") voice = "Pas."

					var mood = codedParsing[5].toString()
					when(mood) {
						"P" -> "Ptc. " + tense + " " + voice + " " + codedParsing[6] + codedParsing[8] + codedParsing[7]
						"N" -> "Infv. $tense $voice"
						else -> {
							when (mood) {
								"I" -> mood = "Ind."
								"D" -> mood = "Impv."
								"O" -> mood = "Opt."
							}
							"Vb. " + tense + " " + voice + " " + mood + " " + codedParsing[2] + codedParsing[7]
						}
					}
				}
				"X-", "I-" -> "Particle"
				else -> "[parsing unavailable]"
			}
		}
	}
}