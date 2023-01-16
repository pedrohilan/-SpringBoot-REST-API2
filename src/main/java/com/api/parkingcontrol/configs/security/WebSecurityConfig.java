package com.api.parkingcontrol.configs.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig{

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS","PATCH"));
        
		http
        .authorizeHttpRequests((authz) -> authz
        	//.requestMatchers(HttpMethod.POST, "/user").hasRole("ADMIN")
            .anyRequest().authenticated()
        ).csrf().disable().cors().configurationSource(request -> corsConfiguration.applyPermitDefaultValues())
        .and().httpBasic();
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
