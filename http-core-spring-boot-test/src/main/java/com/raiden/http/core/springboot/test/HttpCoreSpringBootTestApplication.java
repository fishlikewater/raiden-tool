package com.raiden.http.core.springboot.test;

import com.raiden.tool.http.core.springboot.annotaion.HttpScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;

@SpringBootApplication
@HttpScan("com.raiden.http.core.springboot.test.remote")
public class HttpCoreSpringBootTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpCoreSpringBootTestApplication.class, args);
	}

	@Bean
	public HttpClient httpClient(){
		return HttpClient.newHttpClient();
	}

}
