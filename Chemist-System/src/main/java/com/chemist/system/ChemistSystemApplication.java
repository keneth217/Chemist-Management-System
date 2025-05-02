package com.chemist.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ChemistSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChemistSystemApplication.class, args);
	}

}
