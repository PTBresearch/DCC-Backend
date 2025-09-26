package de.ptb.dsi.dcc_backend.config;



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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security Filter Chain konfigurieren
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF deaktivieren
                .authorizeHttpRequests(auth -> {
                    try {
                        auth
                                .requestMatchers("/api/d-dcc/dccPidList").permitAll()
                                .requestMatchers("/api/d-dcc/swagger-ui/index.html").permitAll()
                                .requestMatchers("/api/d-dcc/**").authenticated()
                             //   .requestMatchers("/api/d-dcc/upload").hasRole("COORDINATOR")
//                                .requestMatchers("/api/d-dcc/change-password").hasRole("ADMIN")
                                .anyRequest().hasRole("ADMIN");
                            //    .and()
                            //    .formLogin(Customizer.withDefaults()) // Aktiviert Login-Formular
////                                .requestMatchers("/api/d-dcc/**").permitAll()
////                                .requestMatchers("/api/d-dcc/userListPid").hasRole("COORDINATOR")
////                                .anyRequest().hasRole("ADMIN")
////                                .and()
////                                .formLogin(Customizer.withDefaults()) // Aktiviert Login-Formular
                              //  .logout()
//                                .logoutUrl("/logout") // <-- Angular URL
//                                .logoutSuccessHandler((request, response, authentication) -> {
//                                    response.setStatus(HttpServletResponse.SC_OK);
//                                })
                                //.invalidateHttpSession(true)
                              //  .deleteCookies("JSESSIONID"); // Session-Cookie löschen


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .httpBasic();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

//    @Bean
//    public RestTemplate restTemplate() {
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//
//        // Proxy setzen
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8085));
//        requestFactory.setProxy(proxy);
//
//        // Timeouts setzen (Millisekunden)
//        requestFactory.setConnectTimeout(15000);  // 15 Sekunden Verbindungsaufbau
//        requestFactory.setReadTimeout(30000);     // 30 Sekunden auf Antwort warten
//
//        return new RestTemplate(requestFactory);
//    }

}
//    requestMatchers("/api/d-dcc/**").hasRole("ADMIN")
//
//        .requestMatchers("/api/d-dcc/verify").hasRole("COORDINATOR")
//        .requestMatchers("/api/d-dcc/upload").hasRole("COORDINATOR")
//        .requestMatchers("/api/d-dcc/addDcc").hasRole("COORDINATOR")
//        .requestMatchers("/api/d-dcc/coordinatorListPidAndPublic").hasRole("COORDINATOR")
//        .requestMatchers("/api/d-dcc/swagger-ui/index.html").authenticated()