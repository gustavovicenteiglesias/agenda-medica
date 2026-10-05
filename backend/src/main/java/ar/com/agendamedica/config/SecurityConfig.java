package ar.com.agendamedica.config;

import ar.com.agendamedica.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers("/api/auditoria/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/agenda/plantillas",
                                "/api/agenda/generar-franjas",
                                "/api/agenda/excepciones",
                                "/api/agenda/bloqueos")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/turnos/*/atencion",
                                "/api/turnos/*/ausencia")
                        .hasAnyRole("ADMIN", "MEDICO")

                        .requestMatchers(HttpMethod.POST,
                                "/api/turnos",
                                "/api/turnos/*/cancelacion",
                                "/api/turnos/*/reprogramacion")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .requestMatchers(HttpMethod.POST, "/api/pacientes")
                        .hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**")
                        .hasAnyRole("ADMIN", "RECEPCION")
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**")
                        .hasAnyRole("ADMIN", "RECEPCION")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
