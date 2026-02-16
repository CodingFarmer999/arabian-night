package com.boardgame.tales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TalesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalesApplication.class, args);
	}

}
