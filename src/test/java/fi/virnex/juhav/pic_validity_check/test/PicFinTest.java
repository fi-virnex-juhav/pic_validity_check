package fi.virnex.juhav.pic_validity_check.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import fi.virnex.juhav.pic_validity_check.pic.PicFin;


class PicFinTest {
	
	@Test
	void testIsOfValidFormatPositive() 
			throws Exception {
		
		if ( PicFin.validSsnList.size() == 0 ) {
			fail("PicFinTest.testIsOfValidFormatPositive() requires >= 1 valid ssn in PicFin.validSsnList");
		}
		
		int countOfValidPics = 0;
		
		for( String ssn : PicFin.validSsnList ) {
		
			if ( PicFin.isOfValidFinSsnFormat(ssn) ) {
				 countOfValidPics++;
			}	
		}
		
		assertTrue( countOfValidPics == PicFin.validSsnList.size() );	
	}
	
	@Test
	void testIsOfValidFormatNegative() 
			throws Exception {
		
		if ( PicFin.invalidSsnList.size() == 0 ) {
			fail("PicFinTest.testIsOfValidFormatNegative() requires >= 1 invalid ssn in PicFin.invalidSsnList");
		}
				
		int countOfInvalidPics = 0;
		
		for ( String ssn : PicFin.invalidSsnList) {
			if ( PicFin.isOfValidFinSsnFormat(ssn) == false ) {
				countOfInvalidPics++;
			}	
		}
		
		assertTrue( countOfInvalidPics == PicFin.invalidSsnList.size() );	
	}

	
}
