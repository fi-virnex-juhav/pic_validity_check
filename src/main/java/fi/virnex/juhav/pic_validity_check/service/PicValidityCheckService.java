package fi.virnex.juhav.pic_validity_check.service;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.virnex.juhav.pic_validity_check.pic.Pic;
import fi.virnex.juhav.pic_validity_check.pic.PicFin;

public class PicValidityCheckService {

	public static boolean isPicValid( Map<String,String> map) {
		String country = map.get("country").toUpperCase();
		if ( country.equals("FIN") ) {
			PicFin picFin = new PicFin(map);
			return picFin.isValidPic(picFin);
		}
		System.out.println("--- ERROR : At the moment PIC validity check supports only country FIN");
		return false;
	}
	
}
