package pe.edu.upeu.clinica.shared.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] ROLES = {"ADMINISTRADOR", "RECEPCIONISTA", "ODONTOLOGO", "CAJERO"};

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            Object roles = realmAccess == null ? null : realmAccess.get("roles");
            if (!(roles instanceof Collection<?> roleNames)) {
                return List.<GrantedAuthority>of();
            }
            return roleNames.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pacientes/**", "/api/v1/citas/**")
                        .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pacientes/**", "/api/v1/citas/**")
                        .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pacientes/**", "/api/v1/citas/**")
                        .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pacientes/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/especialidades/**", "/api/v1/procedimientos/**",
                                "/api/v1/odontologos/**", "/api/v1/metodos-pago/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/odontologos/**")
                        .hasAnyRole("ADMINISTRADOR", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/metodos-pago/**")
                        .hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/especialidades/**", "/api/v1/procedimientos/**",
                                "/api/v1/odontologos/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/especialidades/**", "/api/v1/procedimientos/**",
                                "/api/v1/odontologos/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/v1/atenciones/**")
                        .hasAnyRole("ADMINISTRADOR", "ODONTOLOGO")
                        .requestMatchers("/api/v1/tratamientos/**")
                        .hasAnyRole("ADMINISTRADOR", "ODONTOLOGO")
                        .requestMatchers("/api/v1/pagos/**")
                        .hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers("/api/v1/reportes/**")
                        .hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/**")
                        .hasAnyRole(ROLES)
                        .requestMatchers("/api/**").hasAnyRole(ROLES)
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }
}