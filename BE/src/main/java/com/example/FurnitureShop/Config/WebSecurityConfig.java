package com.example.FurnitureShop.Config;

import com.example.FurnitureShop.Filter.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtFilter  jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                // === CORS ===
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // === CSRF ===
                .csrf(csrf -> csrf.disable())

                // === AUTHORIZATION ===
                .authorizeHttpRequests(auth -> auth
                        //public endpoint
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/rates/**").permitAll()
                        //hasRole endpoint
                          /// User
                        .requestMatchers(HttpMethod.GET,"/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{userId}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}/customer-detail").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}/shipper-detail").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}/inventory-log-staff-detail").hasRole("ADMIN")
                          /// Product
                        .requestMatchers(HttpMethod.POST, "/api/v1/product").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/product/variant/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/product/**").hasRole("ADMIN")
                          /// Category
                        .requestMatchers(HttpMethod.POST, "/api/v1/category").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/category/**").hasRole("ADMIN")
                          /// InventoryLog
                        .requestMatchers(HttpMethod.POST, "/api/v1/inventory-logs").hasRole("Ware House Manager")
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventory-logs").hasRole("ADMIN")
                          /// Order
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/user/**").hasAnyRole("ADMIN", "CUSTOMER", "STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/update/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/cancelled/**").hasAnyRole("STAFF", "CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole("ADMIN")
                          /// Promotion
                        .requestMatchers( "/api/v1/promotions/**").hasAnyRole("ADMIN", "STAFF")
                          /// Rate(any.authenticated)
                        .anyRequest().permitAll()
                )

                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(
                                (req, res, e) -> {
                                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    res.getWriter().write("Forbidden: " + e.getMessage());
                                }
                        )
                        .authenticationEntryPoint(
                                (req, res, e) -> {
                                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    res.getWriter().write("Unauthorized: " + e.getMessage());
                                }
                        )
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // === CORS CONFIG ===
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
