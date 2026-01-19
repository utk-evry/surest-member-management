package com.tietoevry.surest_member_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableCaching
@SpringBootApplication
public class SurestMemberManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurestMemberManagementApplication.class, args);
	}

}
