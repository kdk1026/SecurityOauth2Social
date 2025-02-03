package com.kdk.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 2. 2. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/", "/login/**").permitAll()
					.requestMatchers("/js/**", "/css/**", "/upload/**").permitAll()
					.requestMatchers("/actuator/**").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2Login((oauth2Login) ->
				oauth2Login
					.loginPage("/login")
					.defaultSuccessUrl("/home", true)
					.failureUrl("/login?error")
					.userInfoEndpoint((userInfo) ->
						userInfo
							.userService(this.customOAuth2UserService())
					)
			)
			// 로그아웃은 formLogin이 아니라 그런지 404 에러, Controller에서 처리
			// sessionManagement 는 로그아웃을 했음에도 사라지지 않아서 제거함
			.headers((headers) ->
				headers
					.httpStrictTransportSecurity(this.hstsCustomizer())
					.frameOptions(this.frameOptionsCustomizer())
					.xssProtection(this.xssCustomizer())

			);

		return http.build();
	}

	@Bean
	OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
		return new DefaultOAuth2UserService() {

			@Override
			public OAuth2User loadUser(OAuth2UserRequest userRequest) {
				OAuth2User oAuth2User = null;

				try {
					oAuth2User = super.loadUser(userRequest);
					log.info("사용자 정보 불러오기 성공");

					String registrationId = userRequest.getClientRegistration().getRegistrationId();
					log.info("registrationId : {}", registrationId);

					Map<String, Object> attributes = oAuth2User.getAttributes();
					log.info("사용자 속성 : {}", attributes);

			        Map<String, Object> customAttributes = new HashMap<>();

	                if ( registrationId.equals("naver") ) {
	                    @SuppressWarnings("unchecked")
						Map<String, Object> response = (Map<String, Object>) attributes.get("response");
	                    customAttributes.put("name", response.get("name"));
	                    customAttributes.put("email", response.get("email"));
	                } else if ( registrationId.equals("kakao") ) {
	                    @SuppressWarnings("unchecked")
						Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
	                    customAttributes.put("name", kakaoAccount.get("profile_nickname"));
                    	customAttributes.put("email", kakaoAccount.get("email"));
	                } else if ( registrationId.equals("facebook") ) {
	                	customAttributes.put("name", attributes.get("name"));
                        customAttributes.put("email", attributes.get("email"));
	                } else if ( registrationId.equals("google") ) {
	                	customAttributes.put("name", attributes.get("name"));
                        customAttributes.put("email", attributes.get("email"));
	                }

	                return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), customAttributes, "email");

				} catch (OAuth2AuthenticationException e) {
					log.error("OAuth2AuthenticationException 발생: {}", e.getError().getErrorCode(), e);
					throw e;
				} catch (Exception e) {
					log.error("OAuth2 로그인 중 오류 발생: ", e);
					throw e;
				}
			}

		};
	}

	private Customizer<HeadersConfigurer<HttpSecurity>.HstsConfig> hstsCustomizer() {
		return hsts -> hsts
				.includeSubDomains(true)
				.maxAgeInSeconds(31536000); // 1년
	}

	private Customizer<HeadersConfigurer<HttpSecurity>.FrameOptionsConfig> frameOptionsCustomizer() {
		return frameOptions -> frameOptions.deny();
	}

	private Customizer<HeadersConfigurer<HttpSecurity>.XXssConfig> xssCustomizer() {
		return xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED);
	}

}
