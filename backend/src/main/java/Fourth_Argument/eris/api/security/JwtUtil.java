package Fourth_Argument.eris.api.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
// Instanciation de la classe JwtUtil au démarrage de l'application
public class JwtUtil {

    // Lecture de la clé secrète JWT depuis le fichier application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Lecture de la durée de validité du token JWT depuis le fichier application.properties
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Clé secrète pour signer les tokens JWT
    private SecretKey key;

    // PostConstruct est une annotation qui permet de exécuter une méthode après la construction de l'objet
    @PostConstruct
    public void init() {
        // Calcul de la clé secrète pour signer les tokens JWT
        // On transforme la clé secrète en bytes et on la hache avec l'algorithme HS256
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Génération du token JWT en utilisant l'email de l'utilisateur
    public String generateToken(String email) {
        // Construction du token
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                // On signe le token avec la clé secrète
                .signWith(key, SignatureAlgorithm.HS256)
                // On compacte le token en une chaîne de caractères
                .compact();
    }

    // Récupération de l'email de l'utilisateur à partir du token JWT
    public String getEmailFromToken(String token) {
        // On décompacte le token en une chaîne de caractères
        return Jwts.parser()
                .setSigningKey(key)
                // On vérifie la signature du token
                .parseClaimsJws(token)
                // On donne accés au corps du token
                .getBody()
                // renvoie la valeur (email)
                .getSubject();
    }

    // Token validation
    public boolean validateJwtToken(String token) {
        try {
            // Comme dans getEmailFromToken, on vérifie la signature du token
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) { // Si la signature du token est invalide
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) { // Si le token est mal formé
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) { // Si le token a expiré
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) { // Si le token n'est pas supporté
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) { // Si le token est vide
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false; // Si le token est invalide, on retourne false
    }
}
