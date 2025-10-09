package de.ptb.dsi.dcc_backend.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // 1. PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. SecurityFilterChain – mit Authentifizierung + Autorisierung + CORS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // aktiviert corsConfigurationSource()
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/d-dcc/swagger-ui/**",
                                "/api/d-dcc/v3/api-docs/**",
                                "/api/d-dcc/swagger-resources/**"
                        ).permitAll()
                        .requestMatchers("/api/d-dcc/login").permitAll()
                        .requestMatchers("/api/d-dcc/dccPidList").permitAll()
                        .requestMatchers("/api/d-dcc/dccPublicPidList").permitAll()
                        .requestMatchers("/api/d-dcc/upload").authenticated()
                        .requestMatchers("/api/d-dcc/downloadXml", "/api/d-dcc/verify", "/api/d-dcc/delete", "/api/d-dcc/coordinatorListPidAndPublic").hasAnyRole("COORDINATOR", "ADMIN")
                        .requestMatchers("/api/d-dcc/publicAndCoordinatorDccList").hasRole("COORDINATOR")
                        .requestMatchers("/api/d-dcc/listAllDccPid", "/api/d-dcc/allDccList").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        return http.build();
    }

    // 3. AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    // 4. CORS-Konfiguration (zentral, wird automatisch durch .cors() verwendet)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:8085",
                "https://d-si.ptb.de"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Achtung: Nur setzen, wenn kein "*" in allowedOrigins!
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 5. Swagger Auth (optional, aber oft sinnvoll)
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components().addSecuritySchemes("basicAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
//    private final UserDetailsService userDetailsService;
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//@Bean
//public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//            .cors(Customizer.withDefaults())
//            .csrf().disable()
//            .authorizeHttpRequests(authz -> authz
//                            .requestMatchers("/api/d-dcc/swagger-ui/**", "/api/d-dcc/v3/api-docs/**", "/api/d-dcc/swagger-resources/**").permitAll()// Swagger freigeben
//                            .requestMatchers("/api/d-dcc/login").permitAll()
//                            .requestMatchers("/api/d-dcc/dccPidList").permitAll()
//                    .requestMatchers("/api/d-dcc/dccPublicPidList").permitAll()
//                            .requestMatchers("/api/d-dcc/downloadXml").hasAnyRole("COORDINATOR","ADMIN")
//                            .requestMatchers("/api/d-dcc/verify").hasAnyRole("COORDINATOR","ADMIN")
//                            .requestMatchers("/api/d-dcc/delete").hasAnyRole("COORDINATOR","ADMIN")
//                            .requestMatchers("/api/d-dcc/coordinatorListPidAndPublic").hasAnyRole("COORDINATOR","ADMIN")
//                    .requestMatchers("/api/d-dcc/listAllDccPid").hasRole("ADMIN")
////                            .requestMatchers("/api/d-dcc/coordinatorListPaged").authenticated()
//                       //     .requestMatchers("/api/d-dcc/users").authenticated()
//                    .requestMatchers("/api/d-dcc/publicAndCoordinatorDccList").hasRole("COORDINATOR")
//                            .requestMatchers("/api/d-dcc/upload").authenticated()
////                            .requestMatchers("/api/d-dcc/users").hasRole("ADMIN")
//                            .requestMatchers("/api/d-dcc/allDccList").hasRole("ADMIN")
////                    .requestMatchers("/api/d-dcc/coordinatorDccList").authenticated()
//                            .anyRequest().authenticated()
//            )
//           // .formLogin(Customizer.withDefaults()) // Login-Formular aktiv
//            .httpBasic(Customizer.withDefaults()) // Swagger mit Basic Auth möglich
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
//
//    return http.build();
//
//}
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
//                .components(new Components()
//                        .addSecuritySchemes("basicAuth",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("basic")));
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder())
//                .and()
//                .build();
//    }
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:4200"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
////    @Bean
////    public RestTemplate restTemplate() {
////        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
////
////        // Proxy setzen
////        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8085));
////        requestFactory.setProxy(proxy);
////
////        // Timeouts setzen (Millisekunden)
////        requestFactory.setConnectTimeout(15000);  // 15 Sekunden Verbindungsaufbau
////        requestFactory.setReadTimeout(30000);     // 30 Sekunden auf Antwort warten
////
////        return new RestTemplate(requestFactory);
////    }
//
//}
////    requestMatchers("/api/d-dcc/**").hasRole("ADMIN")
////
////        .requestMatchers("/api/d-dcc/verify").hasRole("COORDINATOR")
////        .requestMatchers("/api/d-dcc/upload").hasRole("COORDINATOR")
////        .requestMatchers("/api/d-dcc/addDcc").hasRole("COORDINATOR")
////        .requestMatchers("/api/d-dcc/coordinatorListPidAndPublic").hasRole("COORDINATOR")
////        .requestMatchers("/api/d-dcc/swagger-ui/index.html").authenticated()