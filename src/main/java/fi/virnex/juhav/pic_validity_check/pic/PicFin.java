package fi.virnex.juhav.pic_validity_check.pic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// import java.util.function.Function;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import fi.virnex.juhav.pic_validity_check.pic.Pic; 

/*
personal identity code "pic" used to support internationalization,
instead of Finland specific "ssn" (social security number).

pic=ssn format case Finland (FIN):
ddMMyyYijkC
// Y (capital) indicates Century: < 1900, [1900,...,1999], >= 2000 
// ( Century cannot be denoted by C to avoid duplicate with Control below )
// ijk typically in ["002",...,"899"] in Finland, odd:man, even:female
// i,j,k all in [0,...,9]
// C reserved for Control. Therefore Century above denoted by Y to avoid duplicate.

ddMMyy : DoB without Century indication

ddMMyyYijkC
-----------
ddMMyy+ijkC		170797+007V			// '+' -> YoB < 1900 				for	Mr Usko Suomalainen
ddMMyy-ijkC		170797-007V			// '-' -> YoB in [1900,...,1999]	for	Mr Esko Suomalainen 
ddMMyyAijkC		170707A007M			// 'A' -> YoB > 2000	 			for Mr Junnu Suomalainen

				131052-308T			// '-' -> YoB in [1900,...,1999]	for	Ms Anna Suomalainen
-> DoB of Anna: 13.10.1952 (1952-10-13, Oct 13 1952)

C?
n = ddMMyyijk, n = 131052308 for Ms Anna Suomalainen
C = tab[ n mod 31 ], where (n mod 31) in [0...30] as in table tab below:
for Anna C = tab[ 131052308 mod 31 ] = tab[25] = "T"
tab:	{'0','1','2','3','4','5','6','7','8','9',
		 'A','B','C','D','E','F',
		 'H',
		 'J','K','L','M','N',
		 'P',
		 'R','S','T','U','V','W','X','Y'}

	// pic = ssn = 131052-308T
	//	1	3	1	0	5	2	-	3	0	8	T		// separated
	//	0	1	2	3	4	5	6	7	8	9	10		// index
 */


// @Component
public class PicFin extends Pic {

	/**
	 * case: country = Finland = FIN = Fin
	 * pic = personal identity code = ssn = social security number
	 */

	// VALID EXAMPLES OF SSN {

	public static final String VALID_SSN_CASE1 = "131052-308T"; // Ms Anna Suomalainen, 	DoB in [1900,1999]

	public static final String VALID_SSN_CASE2 = "170797+007V";	// Mr Arvo  Suomalainen, 	DoB year < 1900
	public static final String VALID_SSN_CASE3 = "170797-007V";	// Mr Teemu Suomalainen, 	DoB year in [1900,1999]
	public static final String VALID_SSN_CASE4 = "170707A007M"; // Mr Junnu Suomalainen, 	DoB year >= 2000

	// for test
	public static final List<String> validSsnList = List.of( VALID_SSN_CASE1, VALID_SSN_CASE2, VALID_SSN_CASE3, VALID_SSN_CASE4 );

	// VALID EXAMPLES OF SSN }
	
	
	// INVALID EXAMPLES OF SSN {

	public static final List<String> invalidSsnList = List.of(
			// ddMMyy notation in comments for DoB
			"131052-308K",	// wrong control character K, mismatch, T expected for Anna Suomalainen
			"131052-308Z",	// wrong control character, 'Z' out of valid control character set
			"170797*007V",	// invalid century *, valid set [+,-,A]
			"170797-007VZ",	// too long
			"170797-007",	// too short
			"320797-007V",	// dd out of range, never such a dd in date
			"310697-007V",	// dd out of range, never such a dd in date
			"290297-3089",	// dd out of Range, no such date Feb 29 that year 1997!!!
			"171797-007V",	// MM out of range, never such a MM in date
			"abcdef-007V",	// garbage in DoB
			"131052-3XYT"	// garbage in individual number, XY
			);

	// INVALID EXAMPLES OF SSN }

	// CONSTANTS FOR PARSING SSN {

	// a valid pic: 131052-308T	of Ms Anna Suomalainen
	// 
	//	1	3	1	0	5	2	-	3	0	8	T		// separated
	//	0	1	2	3	4	5	6	7	8	9	10		// index

	private static final int DDMMYY_FIRST_CHAR_INDEX = 0;
	private static final int DDMMYY_LAST_CHAR_INDEX  = 5;

	private static final int CENTURY_CHAR_INDEX =  6;

	private static final int INDIVIDUAL_NUMBER_FIRST_CHAR_INDEX = 7;
	private static final int INDIVIDUAL_NUMBER_LAST_CHAR_INDEX  = 9;

	private static final int CONTROL_CHAR_INDEX = 10;

	private static final int VALID_SSN_LENGTH = 11;


	// CONSTANTS FOR PARSING SSN }

	// CONSTANTS FOR VALIDATING SSN {

	// Result = Dividend / Divisor
	// Remainder = Modulus = Mod	which is an integer, a whole number.

	private static final int CONTROL_CHAR_VALIDATION_DIVISOR = 31;

	// CONSTANTS FOR VALIDATING SSN {

	private static final String validCenturiesAsString = "+-A";
	private static final Set<Character> validCenturies = new HashSet<>(); 

	private static final char[] validControlChars = { '0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F',
			'H',
			'J','K','L','M','N',
			'P',
			'R','S','T','U','V','W','X','Y'};

	private static final String validControlCharactersAsString = new String(validControlChars); 
	private static final Set<Character> validControlCharacters = new HashSet<>();
	
	private static boolean initDone = false;

	// CONSTANTS FOR VALIDATING SSN }

// trying if removing Functions would make this project runnable again in the new to me but very old Eclipse version.

	private static final String getExtractedIntegersFromSsnAsString(String ssn) {
		String integerAsString = ssn
				.substring(DDMMYY_FIRST_CHAR_INDEX, DDMMYY_LAST_CHAR_INDEX + 1) 
				.concat( ssn.substring(INDIVIDUAL_NUMBER_FIRST_CHAR_INDEX, INDIVIDUAL_NUMBER_LAST_CHAR_INDEX + 1) );
		return integerAsString;
	}
	
	private static final char getValidControlCharacter(String ssn) {
			String validationControlSeedAsString = PicFin.getExtractedIntegersFromSsnAsString(ssn);
			if ( Pic.isValidIntegerFormat(validationControlSeedAsString) == false ) {
				System.out.println("--- Error1 : failed finding extracting integers from ssn in PicFin.getValidControlCharacter for ssn: " + ssn);
				return '?';
			}
			int validationControlSeed;
			try {
				validationControlSeed = Integer.parseInt(validationControlSeedAsString);
			} catch (Exception e) {
				System.out.println("--- Error2 : failed parsing integers from ssn in PicFin.getValidControlCharacter for ssn: " + ssn);
				return '?';
			}
			int validationCharIndex =  validationControlSeed % CONTROL_CHAR_VALIDATION_DIVISOR;
			char validControlChar = validControlChars[ validationCharIndex ];
			return validControlChar;
	}
	
	/*
	// Functions {

	// 3 Functions were used as a demo on defining functions

	private static final Function<String, String> ssnToIntegerAsString = (String ssn) -> {
		String integerAsString = ssn
				.substring(DDMMYY_FIRST_CHAR_INDEX, DDMMYY_LAST_CHAR_INDEX + 1) 
				.concat( ssn.substring(INDIVIDUAL_NUMBER_FIRST_CHAR_INDEX, INDIVIDUAL_NUMBER_LAST_CHAR_INDEX + 1) );
		return integerAsString;
	};

	private static final Function<String, Integer> ssnIntegerStringToInteger = (String ssn) -> 
	Integer.valueOf( ssnToIntegerAsString.apply(ssn) );

	private static final Function<String, Character> getValidControlCharacter = (String ssn) -> {
		int validationControlSeed = PicFin.ssnIntegerStringToInteger.apply(ssn);
		int validatedIndex =  validationControlSeed % CONTROL_CHAR_VALIDATION_DIVISOR;
		char validControlChar = validControlChars[ validatedIndex ];
		return validControlChar;
	};


	// Functions }
*/
	
	// Initialize sets of valid characters {
	
	static {
		validCenturiesAsString
		.chars()
		.forEach( c -> validCenturies.add((char) c));
	}

	/*
	private static final void initValidCenturies() {
		validCenturiesAsString
		.chars()
		.forEach( c -> validCenturies.add((char) c));
	}
	*/
	
	static {
		validControlCharactersAsString
		.chars()
		.forEach( c -> validControlCharacters.add((char) c));
	}

	/*
	private static final void initValidControlCharacters() {
		validControlCharactersAsString
		.chars()
		.forEach( c -> validControlCharacters.add((char) c));
	}
	*/

	// Initialize sets of valid characters }

	// Constructors  {

	public PicFin( Map<String,String> map ) 
	{
		super(map);
		
		/*
		if ( initDone == false ) {
			initValidCenturies();
			initValidControlCharacters();
			initDone = true;
		}
		*/

		// Tests within constructor covered now by JUnit test PicFinTest.java
	}

	public  List<String> getUnexpectedTestResultSsns( List<String> testSsns , boolean validExpected)
			throws Exception {
		List<String> unexpectedTestResultSsns = new ArrayList<>(); 
		if ( testSsns == null ) {
			String error1 = "PicFin.testAllSsns could not execute with a null list of ssns";
			System.out.println(error1);
			throw new Exception(error1);
		}

		if ( testSsns.isEmpty() ) {
			String error2 = "PicFin.testAllSsns could not execute with an empty list of ssns";
			System.out.println(error2);
			throw new Exception(error2);
		}

		int unexpectedResults = 0;

		for ( String ssn : testSsns) {
			if ( isOfValidFinSsnFormat(ssn) ) {
				if ( validExpected != true ) {
					unexpectedTestResultSsns.add(ssn);
					unexpectedResults++;
				}
			} else {
				if ( validExpected != false ) {
					unexpectedTestResultSsns.add(ssn);
					unexpectedResults++;
				}
			}
		}

		String testType = " positive";
		if ( validExpected == false ) {
			testType = " negative ";
		}

		System.out.println("PicFin.testAllSsns checked " + testSsns.size() + testType + " test cases. Unexpected results: " + unexpectedResults);

		return unexpectedTestResultSsns;
	}

	// Constructors  }
	
	// used also to test validity of other ssn than the real one belonging to this PicFin.
	
	@Override
	public boolean isValidPic(Pic pic) {
		
		String country = pic.getCountry();
		
		// System.out.println("--- INFO: PicFin.isPicValid(Pic pic) called with pic " + pic.toString());
		
		if ( country.equalsIgnoreCase("FIN") == false ) {
			System.out.println("--- Error in PicFin.isOfValidSsnFormat(Pic pic) : only country FIN currently supported while " + country + " received");
			return false;
		}
		
		return isOfValidFinSsnFormat( pic.getPic() );
		
	}

	// The beef of the burger {

	// used also to test validity of other ssn than the real one belonging to this PicFin.
	public static boolean isOfValidFinSsnFormat(String ssn)
	{
		if ( ssn == null ) {
			System.out.println("--- Bug : PicFin.isOfValidSsnFormat(ssn) called with a null ssn");
			return false;
		}

		// System.out.println();
		// System.out.println("ssn to be validated : " + ssn );

		// Stream<String> ssnFlow = Stream.of(ssn)

		// This stream contains only 1 element at a time.
		// The filters help to verify ssn validity step by step

		Optional<String> validSsn = Stream.of(ssn)
				.filter( str -> str.length() == VALID_SSN_LENGTH )
				.filter( str -> validCenturies.contains( str.charAt(CENTURY_CHAR_INDEX) ) )
				.filter( str -> {
					String ddMMyy = str.substring(DDMMYY_FIRST_CHAR_INDEX, DDMMYY_LAST_CHAR_INDEX + 1 );
					if ( Pic.isValidIntegerFormat( ddMMyy ) == false ) {
						return false;
					}
					return Pic.isValidDateString(ddMMyy, "ddMMyy");
				})
				.filter( str -> {
					String individualNumberAsString = str.substring(INDIVIDUAL_NUMBER_FIRST_CHAR_INDEX, INDIVIDUAL_NUMBER_LAST_CHAR_INDEX + 1);
					if ( Pic.isValidIntegerFormat( individualNumberAsString ) == false ) {
						return false;
					}
					return true;
				})
				.filter( str -> {
					char controlChar = str.charAt(CONTROL_CHAR_INDEX);
					
					// !!! Example Functions not used !!!
					// char validControlChar	= PicFin.getValidControlCharacter.apply(str).charValue();
					
					char validControlChar = PicFin.getValidControlCharacter(ssn);
					return ( controlChar == validControlChar );
				})
				.findFirst();

		// System.out.println("ssn " + ssn + " is valid? " +  validSsn.isPresent() );
		// System.out.println();

		return validSsn.isPresent();
	}

	// The beef of the burger }
}
