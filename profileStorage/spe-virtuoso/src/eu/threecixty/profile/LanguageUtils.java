package eu.threecixty.profile;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a utility class for dealing with languages in the header of an HTTP request.
 * @author Cong-Kinh Nguyen
 *
 */
public class LanguageUtils {
	
	private static final String ENGLISH = "en";
	private static final String FRENCH = "fr";
	private static final String ITALIAN = "it";
	private static final String EMPTY = "empty";
	
	private static final String [] ALL_LANGUAGES = {FRENCH, ENGLISH, ITALIAN, EMPTY};
	private static final String [] ONLY_ENGLISH = {ENGLISH};
	private static final String [] ONLY_ITALIAN = {ITALIAN};
	private static final String [] ONLY_FRENCH = {FRENCH};
	
	public static int getNumberOfLanguagesSupported() {
		return ALL_LANGUAGES.length;
	}
	
	public static String[] getLanguages(String language) {
		if (language == null || language.equals("")) return ALL_LANGUAGES;
		if (language.contains(",")) {
			String [] tmpLanguages = language.split(",");
			boolean italianContained = false, englishContained = false, frenchContained = false;
			List <String> list = new LinkedList <String>();
			for (String tmpLanguage: tmpLanguages) {
				String tmp = tmpLanguage.trim();
				if (tmp.startsWith(ENGLISH)) {
					englishContained = true;
					list.add(ENGLISH);
				}
				if (tmp.startsWith(ITALIAN)) {
					italianContained = true;
					list.add(ITALIAN);
				}
				if (tmp.startsWith(FRENCH)) {
					frenchContained = true;
					list.add(FRENCH);
				}
			}
			if (italianContained && englishContained && frenchContained) return ALL_LANGUAGES;
			else return list.toArray(new String[list.size()]);
		} else {
			if (language.startsWith(ENGLISH)) return ONLY_ENGLISH;
			else if (language.startsWith(ITALIAN)) return ONLY_ITALIAN;
			else if (language.startsWith(FRENCH)) return ONLY_FRENCH;
			return ONLY_ENGLISH;
		}
	}

	private LanguageUtils() {
	}
}
