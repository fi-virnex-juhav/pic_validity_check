package fi.virnex.juhav.pic_validity_check.pic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Pic {
	private String country;
	private String pic;

	public Pic(Map<String,String> map) {
		country = map.get("country");
		if ( country.equalsIgnoreCase("FIN")) {
		  pic = map.get("ssn");
		} else {
			pic = map.get("pic");
		}
	}

	public boolean isValidPic(Pic pic) {
		System.out.println("--- : Bug : Pic.validatePic(Pic pic) has to be overwritten in a country specific subclass, because pic is country specific.");
		return false;
	}

	// Check if a string is a valid integer in format

	public static final boolean isValidIntegerFormat(String maybeInt ) {
		boolean valid = false;
		try {
			int validInt = Integer.parseInt(maybeInt);
			valid = true;
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}

	// Check both the dateAsString format and that the date exists in the Gregorian calendar.

	public static final boolean isValidDateString(String dateAsString, String format) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			
			// Do not roll over an invalid calendar date e.g. to the next month!
			// Otherwise the roll-over would happen e.g. if day of month value exceeds the number
			// of days in the month based on Cregorian calendar.
			simpleDateFormat.setLenient(false); 
			
			date = simpleDateFormat.parse(dateAsString);

			return true;
		} catch (Exception e) {
			System.out.println("--- INFO: Pic.isValidDateString: " + dateAsString + " : invalid format or non-existing date, " + format + " expected.");
			return false;
		}
	}
	
	// Just a leap year "karkausvuosi" related demo.
	// This test confirms we can validate also existence of a given date.
	// A date may not exist in the Gregorian/Cregorian calendar even the format is valid.
	public static final void checkValidityOf29FebOverSeveralYears() {
		String format = "yyyy-MM-dd";
		int validCases = 0;
		int invalidCases = 0;
		int totalCases = 0;
		boolean isValidDate;
		for ( int yyyy = 1777 ; yyyy <= 2777 ; yyyy++ ) {
			String dateString = yyyy + "-02-29";
			isValidDate = Pic.isValidDateString(dateString, format);
			if ( isValidDate ) {
				validCases++;
			} else {
				invalidCases++;
			}
			totalCases++;
			System.out.println("yyyy-mm-dd " + dateString + " exists: " + isValidDate);
		}
		System.out.println("    INFO : Tested existence of 29 Feb over " + totalCases + " years:");
		System.out.println("    INFO : valid dates: " + validCases + " invalid dates: " + invalidCases);		
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	@Override
	public String toString() {
		return "Pic [country=" + country + ", pic=" + pic + "]";
	}

}

