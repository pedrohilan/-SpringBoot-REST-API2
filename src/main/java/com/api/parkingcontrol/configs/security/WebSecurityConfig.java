package com.api.parkingcontrol.configs.security;

import java.util.List;

import com.api.parkingcontrol.configs.security.jwt.JWTAuthenticateFilter;
import com.api.parkingcontrol.configs.security.jwt.JWTLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig{

	private AuthenticationConfiguration authenticationConfiguration;

	public WebSecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS","PATCH"));
        
		http
        .authorizeHttpRequests((authz) -> {
			try {
				authz
					.requestMatchers(HttpMethod.POST, "/login").permitAll()
						//.requestMatchers("/index").permitAll()
					.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		})
				.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).disable()
				.cors().configurationSource(request -> corsConfiguration.applyPermitDefaultValues()).and()
				.addFilterAfter(new JWTLoginFilter("/login",
						authenticationConfiguration.getAuthenticationManager()),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JWTAuthenticateFilter(),
						UsernamePasswordAuthenticationFilter.class)
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.httpBasic();
    return http.build();
	}
    
     /*@Bean
     public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }*/
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
