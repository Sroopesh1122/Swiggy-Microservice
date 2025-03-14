package com.shivu.swiggy_app.config;


import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.shivu.swiggy_app.service.CustomerUserDetailsService;
import com.shivu.swiggy_app.util.JwtRequestFilter;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


@Configuration
public class SecurityConfig {
	
    private final JwtRequestFilter jwtRequestFilter;
    private final CustomerUserDetailsService customUserDetailsService;
    
    public SecurityConfig(JwtRequestFilter jwtRequestFilter ,CustomerUserDetailsService customUserDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customUserDetailsService= customUserDetailsService;
    }

    @Bean    
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
	{
		httpSecurity.csrf(csrf->csrf.disable())
//		.cors(cors->cors.configurationSource(corsConfigurationSource()))
//		.authorizeHttpRequests(request->request.
//				requestMatchers(
//	                   "/delivery/email/**",
//	                   "/delivery/create",
//	                   "/delivery/forgotPassword",
//	                   "/delivery/resetPassword",
//	                   "/deivery/partnerId/**"
//	                ).permitAll()
//				.requestMatchers(
//	                    "/v3/api-docs/**",
//	                    "/swagger-ui/**",
//	                    "/swagger-ui.html"   
//	                ).permitAll().
//				anyRequest().authenticated())
		.authorizeHttpRequests(request->request.anyRequest()
				.permitAll())
		.httpBasic(Customizer.withDefaults());
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		
		return httpSecurity.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
    	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    	daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
    	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    	
    	return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
//        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
//        corsConfiguration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return source;
//    }
	
	
	@Bean
    public OpenAPI customOpenAPI() 
	{
        return new OpenAPI()
                .info(new Info()
                        .title("Food Ordering API")
                        .version("1.0")
                        .description("API documentation for Food Ordering System"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))); // Enables Bearer Token Authentication
    }
	
	
}

