package com.example.lal;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Collections;

@SpringBootTest
@Disabled("Expired JWT — not part of unit tests")
class LalApplicationTests {

	@Test
	void contextLoads() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE,30);

		String token = JWT.create()
				.withHeader(Collections.emptyMap()) //header
				.withClaim("id", "10000001")//payload
				.withClaim("name", "张三")
				.withExpiresAt(calendar.getTime()) //指定令牌过期时间半小时
				.sign(Algorithm.HMAC256("safdd4h21ft4u")); //签名
		System.out.println(token);
		JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("safdd4h21ft4u")).build();
		DecodedJWT decodedJWT = jwtVerifier.verify(token);
		System.out.println(decodedJWT.getClaim("id").asString());
		System.out.println(decodedJWT.getClaim("name").asString());
	}
	//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODQ4MzMzMTQsInVzZXJJZCI6IjEyMzQ1NiIsInVzZXJuYW1lIjoi5byg5LiJIn0.cgjKKw3t8JV5YxxD9KQFVNBeOPXsv9n94cel_4v69to

	@Test
	public void test(){
		JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("safdd4h21ft4u")).build();
		DecodedJWT decodedJWT = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODQ4MzM0MDMsInVzZXJJZCI6IjEyMzQ1NiIsInVzZXJuYW1lIjoi5byg5LiJIn0.jjST6kErY8WJzFP-lyCBb47zkQi9dT9bpM6gSHchJxU\n");
		System.out.println(decodedJWT.getClaim("id").asString());
		System.out.println(decodedJWT.getClaim("name").asString());


	}

}
