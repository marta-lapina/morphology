package lv.semti.Vardnicas;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import lv.semti.morphology.analyzer.Analyzer;
import lv.semti.morphology.attributes.AttributeNames;
import lv.semti.morphology.lexicon.Ending;

public class GFExport {	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PrintWriter izeja = new PrintWriter(new PrintStream(System.out, true, "UTF8"));
		
		Analyzer analizators = new Analyzer("C:\\Projects\\Lingvistika\\java\\Morphology\\src\\main\\resources\\Lexicon.xml");
		izeja.println("-- Start autogenerated code");
		izeja.printf("-- Generated at %s from lexicon %s revision %s\n", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()),analizators.getFilename(), analizators.getRevision());
				
		describe_paradigm(15, "-- 15. paradigma - 1. konjugācija", "mkVerb_C1", izeja, analizators);
		describe_paradigm(16, "-- 16. paradigma - 2. konjugācija", "mkVerb_C2", izeja, analizators);  		
		describe_paradigm(17, "-- 17. paradigma - 3. konjugācija", "mkVerb_C3", izeja, analizators);

		describe_paradigm(18, "-- 18. paradigma - 1. konjugācija atgriezensikie", "mkVerb_C1_r", izeja, analizators);
		describe_paradigm(19, "-- 19. paradigma - 2. konjugācija atgriezeniskie", "mkVerb_C2_r", izeja, analizators);  		
		describe_paradigm(20, "-- 20. paradigma - 3. konjugācija atgriezeniskie", "mkVerb_C3_r", izeja, analizators);
		
		izeja.println("-- End autogenerated code");					
		izeja.flush();
	}

	private static void describe_paradigm(int paradigm, String comment, String method, PrintWriter izeja,
			Analyzer analizators) {
		
		izeja.println(comment);		
		Boolean b = (paradigm == 15) || (paradigm == 18);
		izeja.printf("%s: Str -> %sVerb_tmp = \\lemma%s ->\n", method, b ? "Str -> Str -> " : "", b ? ",lemma2,lemma3" : "");
		izeja.println("\tlet stem : Str = Predef.tk " + analizators.paradigmByID(paradigm).getLemmaEnding().getEnding().length() + " lemma");
		if (paradigm == 15) { // 1. konj
			izeja.println("\t; stem2 : Str = Predef.tk 1 lemma2");
			izeja.println("\t; stem3 : Str = Predef.tk 1 lemma3");
		}
		if (paradigm == 18) { // 1. konj atgr
			izeja.println("\t; stem2 : Str = Predef.tk 2 lemma2");
			izeja.println("\t; stem3 : Str = Predef.tk 2 lemma3");
		}
		izeja.println("\tin {");
		izeja.println("\t\ts = table {");
		
		HashMap<String, Rindiņa> kategorijas = new HashMap<String, Rindiņa>();
		for (Ending ending : analizators.paradigmByID(paradigm).endings) {
			Rindiņa description = encode(ending);
			if (description != null) {
				Rindiņa esošā = kategorijas.get(description.category);
				if (esošā != null) esošā.append(description.value);
					else kategorijas.put(description.category, description); 
			}
			else {
				//izeja.printf("\tNesapratu galotne %s, mija %d\n", ending.getEnding(), ending.getMija());			
				//ending.describe(izeja);
			}
		}		
		
		LinkedList<String> rindiņas = new LinkedList<String>();
		
		for (Rindiņa rindiņa:kategorijas.values()) rindiņas.add(rindiņa.rezultāts());
		
		Collections.sort(rindiņas);
		
		if (analizators.paradigmByID(paradigm).isMatchingStrong(AttributeNames.i_Reflexive, AttributeNames.v_Yes)) {
			rindiņas.add("\t\t\tParticiple Masc Sg Gen => []");
			rindiņas.add("\t\t\tParticiple Masc Sg Dat => []");
			rindiņas.add("\t\t\tParticiple Masc Sg Loc => []");
			rindiņas.add("\t\t\tParticiple Masc Pl Dat => []");
			rindiņas.add("\t\t\tParticiple Masc Pl Loc => []");
			rindiņas.add("\t\t\tParticiple Fem Sg Dat => []");
			rindiņas.add("\t\t\tParticiple Fem Sg Loc => []");
			rindiņas.add("\t\t\tParticiple Fem Pl Dat => []");
			rindiņas.add("\t\t\tParticiple Fem Pl Loc => []");
		}
		rindiņas.add("\t\t\tRelative Past => []");
		rindiņas.add("\t\t\tRelative Cond => []");
		
		izeja.println(join(rindiņas,";\n"));
		izeja.println("\t\t} ;");	  
		izeja.println("\t} ;");
	}
		
	private static Rindiņa encode(Ending ending) {
		if (ending.getID() == 1201) return null; //blacklist
		
		String stem = null;
		switch (ending.getMija()) {
		case 0: stem = "stem"; break;
		case 2: stem = "mija2 stem"; break;
		case 4: stem = "mija4 stem"; break;
		case 5: stem = "mija5 stem"; break;
		case 6: stem = "mija6 stem3 stem"; break;
		case 7: stem = "mija7 stem3 stem"; break;
		case 8: stem = "mija8 stem"; break;
		case 9: stem = "mija9 stem"; break;
		case 11: stem = "mija11 stem"; break;
		case 12: stem = "mija12 stem"; break;
		case 14: stem = "mija14 stem"; break;
		}
		if (stem == null) return null; // Mijas nesapratām
		if (ending.stemID != 1) stem = stem+ending.stemID;
		
		String vārdšķira = ending.getValue(AttributeNames.i_PartOfSpeech);
		if (vārdšķira == null) return null;
		
		String verbForm = null;
		if (vārdšķira.equalsIgnoreCase(AttributeNames.v_Verb)) {
			String izteiksme = ending.getValue(AttributeNames.i_Izteiksme);
			if (izteiksme == null) return null;
			if (izteiksme.equalsIgnoreCase(AttributeNames.v_Nenoteiksme)) verbForm = "\t\t\tInfinitive";
			if (izteiksme.equalsIgnoreCase(AttributeNames.v_Iisteniibas)) {												
				verbForm = "\t\t\tIndicative ";
				if (ending.isMatchingStrong(AttributeNames.i_Person, "1")) verbForm += "P1";
				if (ending.isMatchingStrong(AttributeNames.i_Person, "2")) verbForm += "P2";
				if (ending.isMatchingStrong(AttributeNames.i_Person, "3")) verbForm += "P3";
				verbForm += " ";
				if (ending.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Plural)) verbForm += "Pl";
				if (ending.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular)) verbForm += "Sg";
				if (ending.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_NA)) verbForm += "_";
				verbForm += " ";
				if (ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Tagadne)) verbForm += "Pres";
				if (ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Naakotne)) verbForm += "Fut";
				if (ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne)) verbForm += "Past";
			}
			if (izteiksme.equalsIgnoreCase(AttributeNames.v_Atstaastiijuma)) {												
				verbForm = "\t\t\tRelative ";
				if (ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Tagadne)) verbForm += "Pres";
				if (ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Naakotne)) verbForm += "Fut";
				if (ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne)) verbForm += "Past";
			}
			if (izteiksme.equalsIgnoreCase(AttributeNames.v_Veeleejuma)) {												
				verbForm = "\t\t\tIndicative _ _ Cond ";
			}
			if (izteiksme.equalsIgnoreCase(AttributeNames.v_Vajadziibas)) {
				if (ending.getEnding().endsWith("ot") || ending.getEnding().endsWith("oties"))
					verbForm = "\t\t\tDebitiveRelative ";
				else verbForm = "\t\t\tDebitive ";
			}
			if (izteiksme.equalsIgnoreCase(AttributeNames.v_Participle)) {
				if (!ending.isMatchingStrong(AttributeNames.i_Voice, AttributeNames.v_Active)) return null; //TODO - ciešamā kārta citur vajadzīga būs 
				if (!ending.isMatchingStrong(AttributeNames.i_Lokaamiiba, AttributeNames.v_Lokaams)) return null; //TODO - nelokāmie citur vajadzīgi būs
				if (!ending.isMatchingStrong(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne)) return null; //TODO - citi citur vajadzīgi būs
				if (!ending.isMatchingStrong(AttributeNames.i_Definiteness, AttributeNames.v_Indefinite)) return null; //TODO - citi citur vajadzīgi būs
				
				verbForm = "\t\t\tParticiple ";
				if (ending.isMatchingStrong(AttributeNames.i_Gender, AttributeNames.v_Masculine)) verbForm += "Masc";
				if (ending.isMatchingStrong(AttributeNames.i_Gender, AttributeNames.v_Feminine)) verbForm += "Fem";
				if (ending.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_NA)) verbForm += "_";
				verbForm += " ";
				if (ending.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Plural)) verbForm += "Pl";
				if (ending.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular)) verbForm += "Sg";				
				verbForm += " ";
				if (ending.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Nominative)) verbForm += "Nom";
				if (ending.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Genitive)) verbForm += "Gen";
				if (ending.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Dative)) verbForm += "Dat";
				if (ending.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Accusative)) verbForm += "Acc";
				if (ending.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Locative)) verbForm += "Loc";
			}
		}
		if (verbForm == null) return null;
		String endingText = String.format("\"%s\"", ending.getEnding());
		if (ending.getMija() == 2 && ending.getEnding().startsWith("a")) endingText = "mija2a stem " + endingText; // exception galotnei sacīt -> sakām, nevis sakam
		
		Rindiņa rezults = new Rindiņa(verbForm, String.format("%s + %s", stem, endingText)); 
		return rezults;
	}
	
	
	 public static String join(Collection<String> s, String delimiter) {
	        StringBuffer buffer = new StringBuffer();
	        Iterator<String> iter = s.iterator();
	        while (iter.hasNext()) {
	            buffer.append(iter.next());
	            if (iter.hasNext()) {
	                buffer.append(delimiter);
	            }
	        }
	        return buffer.toString();
	 }	
}

class Rindiņa {
	String category;
	String value;
	
	public Rindiņa(String _category, String _value) {
		this.category = _category;
		this.value = _value;
	}
	
	public String rezultāts() {
		return String.format("%s => %s", this.category, this.value);
	}
	
	public void append(String new_value) {
		this.value = this.value + " | " + new_value;
	}
}
