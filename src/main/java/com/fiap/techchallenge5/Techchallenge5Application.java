package com.fiap.techchallenge5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Techchallenge5Application {

	public static void main(String[] args) {
		SpringApplication.run(Techchallenge5Application.class, args);
	}

}
