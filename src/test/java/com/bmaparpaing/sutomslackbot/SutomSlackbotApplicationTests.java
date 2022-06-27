package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SutomSlackbotApplicationTests {

	@Autowired
	private PodiumJourService podiumJourService;

	@Test
	void contextLoads() {
		assertThat(podiumJourService).isNotNull();
	}

}
