package fi.virnex.juhav.pic_validity_check.test;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import fi.virnex.juhav.pic_validity_check.PicValidityCheckApplication;

@WebAppConfiguration
@ContextConfiguration(classes = PicValidityCheckApplication.class)
@SpringBootTest
public class PicValidityCheckApplicationTest {

	@Test
	void contextLoads() {
		System.out.println("PicValidityCheckApplicationTest.contextLoads() executed");
	}

}


