package de.ptb.dsi.dcc_backend.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
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

                        .requestMatchers("/api/d-dcc/downloadXml", "/api/d-dcc/verify","/api/d-dcc/listAllDccPid").permitAll()
                        .requestMatchers("/api/d-dcc/dcc/{pid}").permitAll()
                        .requestMatchers("/api/d-dcc/upload").authenticated()
                        .requestMatchers( "/api/d-dcc/delete", "/api/d-dcc/coordinatorListPidAndPublic").hasAnyRole("COORDINATOR", "ADMIN")
                        .requestMatchers("/api/d-dcc/publicAndCoordinatorDccList").authenticated()
                        .requestMatchers( "/api/d-dcc/allDccList", "/api/d-dcc/users/{id}","/api/d-dcc/edit/users/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                 .invalidSessionUrl("/login?error=session")
                .maximumSessions(1) // Maximale Anzahl von Sessions
                .expiredUrl("/login?expired=true")
                .and()
                .and()
                .logout()
                .invalidateHttpSession(true)  // Sicherstellen, dass die Session gelöscht wird
                .clearAuthentication(true)    // Entfernt alle Authentifizierungsdaten
                .logoutUrl("/api/d-dcc/logout") // Setzt die Logout-URL fest
                .logoutSuccessUrl("/login?logout=true"); // Weiterleitung nach dem Logout
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
