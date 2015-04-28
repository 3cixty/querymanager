package eu.threecixty.profile.elements;

/**
 * This is a utility class for dealing with languages in the header of an HTTP request.
 * @author Cong-Kinh Nguyen
 *
 */
public class LanguageUtils {
	
	private static final String ENGLISH = "en";
	private static final String FRENCH = "fr";
	private static final String ITALIAN = "it";
	//private static final String EMPTY = "empty";
	
	private static final String TRANSLATION_TAG = "-tr";
	
	//private static final String [] ALL_LANGUAGES = {FRENCH, ENGLISH, ITALIAN, EMPTY};
	private static final String [] ONLY_ENGLISH = {ENGLISH, ENGLISH + TRANSLATION_TAG};
	private static final String [] ONLY_ITALIAN = {ITALIAN, ITALIAN + TRANSLATION_TAG};
	private static final String [] ONLY_FRENCH = {FRENCH, FRENCH + TRANSLATION_TAG};
	
	private static final String [] LANGUAGES_DEFAULT = ONLY_ENGLISH;
	
	/*
	public static int getNumberOfLanguagesSupported() {
		return ALL_LANGUAGES.length;
	}
	
	public static String[] getAllLanguages() {
		return ALL_LANGUAGES;
	}
	*/
	
	public static String[] getLanguages(String language) {
		if (language == null || language.equals("")) return LANGUAGES_DEFAULT;
		if (language.contains(",")) {
			String [] tmpLanguages = language.split(",");
			for (String tmpLanguage: tmpLanguages) {
				String tmp = tmpLanguage.trim();
				if (tmp.startsWith(ENGLISH)) {
					return ONLY_ENGLISH;
				}
				if (tmp.startsWith(ITALIAN)) {
					return ONLY_ITALIAN;
				}
				if (tmp.startsWith(FRENCH)) {
					return ONLY_FRENCH;
				}
			}
			return LANGUAGES_DEFAULT;
		} else {
			if (language.startsWith(ENGLISH)) return ONLY_ENGLISH;
			else if (language.startsWith(ITALIAN)) return ONLY_ITALIAN;
			else if (language.startsWith(FRENCH)) return ONLY_FRENCH;
			return LANGUAGES_DEFAULT;
		}
	}

	private LanguageUtils() {
	}
}
