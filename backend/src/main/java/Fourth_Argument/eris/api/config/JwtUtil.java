package Fourth_Argument.eris.api.config;

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
// JwtUtil class instantiation at application startup
public class JwtUtil {

    // Reads the JWT secret key from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Reads the JWT token validity duration from application.properties
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Secret key to sign JWT tokens
    private SecretKey key;

    // PostConstruct annotation runs this method after the object's construction
    @PostConstruct
    public void init() {
        // Compute the secret key to sign JWT tokens
        // Transform the secret into bytes and hash it with the HS256 algorithm
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generates a JWT token using a user's email
    public String generateToken(String email) {
        // Build the token
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                // Sign the token with the secret key
                .signWith(key, SignatureAlgorithm.HS256)
                // Compact the token into a string
                .compact();
    }

    // Extracts the user's email from the JWT token
    public String getEmailFromToken(String token) {
        // Parse the token from the string
        return Jwts.parser()
                .setSigningKey(key)
                // Verify the token's signature
                .parseClaimsJws(token)
                // Access the token body
                .getBody()
                // Return the value (email)
                .getSubject();
    }

    // Token validation
    public boolean validateJwtToken(String token) {
        try {
            // As in getEmailFromToken, verify the token's signature
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) { // If the token signature is invalid
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) { // If the token is malformed
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) { // If the token is expired
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) { // If the token is unsupported
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) { // If the token is empty
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false; // If the token is invalid, return false
    }
}
