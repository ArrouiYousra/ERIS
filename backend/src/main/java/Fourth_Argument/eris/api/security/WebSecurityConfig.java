package Fourth_Argument.eris.api.security;

import Fourth_Argument.eris.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Beans can be declared here to be created at startup and injected into other classes
@Configuration
// Enables Spring Web security, without this security filters and rules will not be applied
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService; // User filter to check credentials
    private final AuthEntryPointJwt unauthorizedHandler; // Unauthenticated request (401)
    private final JwtUtil jwtUtil; // JWT token validation

    // Beans are injected into the constructor
    public WebSecurityConfig(CustomUserDetailsService userDetailsService,
                             AuthEntryPointJwt unauthorizedHandler,
                             JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtil = jwtUtil;
    }

    // Filter to check the JWT token
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtil, userDetailsService);
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection
                .csrf(csrf -> csrf.disable())
                // Enable CORS for the frontend (CorsConfigurationSource bean)
                .cors(Customizer.withDefaults())
                // Handles authentication errors
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // Disable session management for stateless http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define authorization rules (public/authenticated)
                .authorizeHttpRequests(auth -> auth
                        // No token needed here
                        .requestMatchers("/api/auth/**").permitAll()
                        // Swagger / OpenAPI (public access in dev)
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Must be authenticated
                        .anyRequest().authenticated());
        // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
