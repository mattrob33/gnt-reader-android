package com.mattrobertson.greek.reader.objects;

public class Word {
	private String text,refTag,lex;
	private String parsing;
	int id;

	public Word(int id, String x, String r, String l, String p) {
		this.id = id;
		text = x;
		refTag = r;
		lex = l;
		parsing = p;
	}

	public String getParsing() {
		String typeCode, info;

		String[] arr = parsing.split(" ");

		if (arr.length < 2)
			return "";

		typeCode = arr[0];
		info = arr[1];

		if (typeCode.equals("A-")) {
			return "Adj-"+info.charAt(4)+info.charAt(6)+info.charAt(5);
		}
		else if (typeCode.equals("C-")) {
			return "Conj";
		}
		else if (typeCode.equals("D-")) {
			return "Adv";
		}
		else if (typeCode.equals("N-")) {
			return "Noun-"+info.charAt(4)+info.charAt(6)+info.charAt(5);
		}
		else if (typeCode.equals("P-")) {
			return "Prep";
		}
		else if (typeCode.equals("RA")) {
			return "Art "+info.charAt(4)+info.charAt(6)+info.charAt(5);
		}
		else if (typeCode.equals("V-")) {
			String tense = ""+info.charAt(1);
			if (tense.equals("P")) tense = "Pres.";
			if (tense.equals("I")) tense = "Impf.";
			if (tense.equals("F")) tense = "Fut.";
			if (tense.equals("A")) tense = "Aor.";
			if (tense.equals("X")) tense = "Pf.";
			if (tense.equals("Y")) tense = "Plu.";

			String voice = ""+info.charAt(2);
			if (voice.equals("A")) voice = "Act.";
			if (voice.equals("M")) voice = "Mid.";
			if (voice.equals("P")) voice = "Pas.";

			// Determine mood
			if (info.charAt(3) == 'P') { //Ptc
				return "Ptc. "+tense+" "+voice+" "+info.charAt(4)+info.charAt(6)+info.charAt(5);
			}
			else if (info.charAt(3) == 'N') { //Infv
				return "Infv. "+tense+" "+voice;
			}
			else { //Verb
				String mood = ""+info.charAt(3);
				if (mood.equals("I")) mood = "Ind.";
				if (mood.equals("D")) mood = "Impv.";
				if (mood.equals("O")) mood = "Opt.";

				return "Vb. "+tense+" "+voice+" "+mood+" "+info.charAt(0)+info.charAt(5);
			}
		}
		else if (info.equals("X-") || typeCode.equals("I-")) {
			return "Particle";
		}

		return "";
	}
	
	public String getLex() {
		return lex;
	}

	@Override
	public String toString()
	{
		return text;
	}
}
