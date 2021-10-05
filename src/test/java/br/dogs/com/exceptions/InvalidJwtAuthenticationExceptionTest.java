package br.dogs.com.exceptions;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class InvalidJwtAuthenticationExceptionTest {

	@InjectMocks
	private InvalidJwtAuthenticationException invalidJwtAuthenticationException;

	@BeforeEach
	public void setUp() {

		standaloneSetup(invalidJwtAuthenticationException);

	}

	@Test
	public void invalidJwtAuthenticationException() {
		Assertions.assertThrows(InvalidJwtAuthenticationException.class, () -> {
			try {
				Jwts.parserBuilder().setSigningKey("key").build().parseClaimsJws("header.payload.signature");
			} catch (MalformedJwtException e) {
				throw new InvalidJwtAuthenticationException("Token informado não segue o formato correspondente.");
			}
		});
	}

}
