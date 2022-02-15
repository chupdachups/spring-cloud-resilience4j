package com.example.resilience4j.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience4j.service.TestService;

import reactor.core.publisher.Mono;

@RestController
public class Resilience4jController {

private final Logger log = LoggerFactory.getLogger(Resilience4jController.class);
	
	@Autowired
	public TestService testService;
	
	
	@GetMapping("/fail")    
    public Mono<String> fail() {
		log.info(">>>> Call fail");
    	return testService.fail();

    }
	
	@GetMapping("/success")    
    public Mono<String> success() {
		log.info(">>>> Call success");
    	return testService.success();

    }
	
	@GetMapping("/data")
	public Mono<String> data() {
		log.info(">>>> Call data");
		return testService.getData();
	}
}
