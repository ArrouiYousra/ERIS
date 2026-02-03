package Fourth_Argument.eris.api.security;

import Fourth_Argument.eris.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// On peut déclarer des Bean qui sont créés au démarrage et injectés dans les autres classes
@Configuration
// Active la sécurité web Spring, sans ça, les filtres et règles de sécurité ne seront pas appliquées
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService; // Filtre utilisateur pour vérifier les identifiants
    private final AuthEntryPointJwt unauthorizedHandler; // Requête non authentifiée (401)
    private final JwtUtil jwtUtil; // Validation du token JWT

    // On injecte les beans dans le constructeur
    public WebSecurityConfig(CustomUserDetailsService userDetailsService,
                             AuthEntryPointJwt unauthorizedHandler,
                             JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtil = jwtUtil;
    }

    // Filtre pour vérifier le token JWT
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtil, userDetailsService);
    }

    // Gestionnaire d'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Filtre de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactive la protection CSRF (Cross-Site Request Forgery)
                .csrf(csrf -> csrf.disable())
                // Désactive la gestion des CORS (Cross-Origin Resource Sharing)
                .cors(cors -> cors.disable())
                // Gère les erreurs d'authentification
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // Désactive la gestion des sessions stateless http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // On définit les règles d'authorisation (public/authentifié)
                .authorizeHttpRequests(auth -> auth
                        // pas besoin de token ici
                        .requestMatchers("/api/auth/**").permitAll()
                        // doit etre authentifier
                        .anyRequest().authenticated());
        // On ajoute le filtre JWT
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
