package com.djeno.backend_lab1.config;

import com.djeno.backend_lab1.models.User;
import com.djeno.backend_lab1.security.JwtAuthenticationFilter;
import com.djeno.backend_lab1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                // Своего рода отключение CORS (разрешение запросов со всех доменов)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                // Настройка доступа к конечным точкам
                .authorizeHttpRequests(request -> request
                        // Можно указать конкретный путь, * - 1 уровень вложенности, ** - любое количество уровней вложенности
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ws/**").authenticated() // Защита WebSocket
                        .requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**").permitAll()

                        // Эндпоинт подачи заявки (требуется только аутентификация)
                        .requestMatchers(HttpMethod.POST, "/admin/apply").authenticated()
                        // Эндпоинты для администраторов
                        .requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/admin/{requestId}/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/admin/{requestId}/reject").hasRole("ADMIN")

                        // Эндпоинты для работы с данными
                        .requestMatchers(HttpMethod.GET, "/study-groups/**","/persons/**", "/locations/**", "/coordinates/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/study-groups/**", "/persons/**", "/locations/**", "/coordinates/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/study-groups/**","/persons/**", "/locations/**", "/coordinates/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/study-groups/**","/persons/**", "/locations/**", "/coordinates/**").authenticated()


                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // не работает, используется только DaoAuthenticationProvider
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        var admin = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        // Добавляем оба механизма аутентификации
//        auth.authenticationProvider(authenticationProvider()); // для базы данных
//        auth.userDetailsService(inMemoryUserDetailsManager()); // для in-memory
//
//        return auth.build();
//    }


}