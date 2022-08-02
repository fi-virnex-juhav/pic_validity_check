package fi.virnex.juhav.pic_validity_check.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fi.virnex.juhav.pic_validity_check.service.PicValidityCheckService;

@RestController
public class PicValidityCheckController {

	/* Specified as:
		 POST validate_ssn
			Request:
				ssn as input parameter in JSON body
				country_code  // FI supported currently
			Response
				ssn_valid (true/false)
		error handling!
	 */

	// POSTMAN:
	// http://localhost:8080/validate_ssn
	/* body:

		{
			"country":"FIN",
			"ssn": "131052-308T"
		}

	 */

	@PostMapping("/validate_ssn")
	public boolean validatePic(@RequestBody Map<String, String> map) 
			throws Exception {

		return PicValidityCheckService.isPicValid(map);
	}

}
