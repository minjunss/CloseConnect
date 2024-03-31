package com.CloseConnect.closeconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CloseConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloseConnectApplication.class, args);
	}

}
