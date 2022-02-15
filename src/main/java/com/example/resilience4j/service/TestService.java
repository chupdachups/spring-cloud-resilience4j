package com.example.resilience4j.service;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.resilience4j.exception.CustomException;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import reactor.core.publisher.Mono;

@Service
public class TestService {
	
	private final Logger log = LoggerFactory.getLogger(TestService.class);
	
	private static final String TEST_CONFIG = "testConfig";

	@Bulkhead(name = TEST_CONFIG)
	@CircuitBreaker(name = TEST_CONFIG, fallbackMethod = "fallback")
    public Mono<String> fail() {
    	return Mono.error(new CustomException("CustomException"));
    }
	
	@RateLimiter(name = TEST_CONFIG)
	@Bulkhead(name = TEST_CONFIG)
	@CircuitBreaker(name = TEST_CONFIG, fallbackMethod = "fallback")
    public Mono<String> success() {
        return Mono.just(">>>>>>>>>>>> Success <<<<<<<<<<<<<");
    }
	
	@TimeLimiter(name = TEST_CONFIG)
	@Retry(name = TEST_CONFIG)
	@CircuitBreaker(name = TEST_CONFIG, fallbackMethod = "fallback")
	public Mono<String> getData() {
		WebClient webClient =  WebClient.builder()
				.baseUrl("http://localhost:8080")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
				.build();
		webClient.get();
		return webClient.method(HttpMethod.GET)
				.uri("/")
				.retrieve()
				.bodyToMono(String.class);
	}
	
	
	private Mono<String> fallback(CustomException e) {
        return Mono.just("fallback invoked! exception type : " + e.getClass());
    }
	private Mono<String> fallback(CallNotPermittedException e) {
        return Mono.just("fallback invoked! exception type : " + e.getClass());
    }
	private Mono<String> fallback(Throwable t) {
        return Mono.just("fallback invoked! exception type : " + t.getClass());
    }
}
