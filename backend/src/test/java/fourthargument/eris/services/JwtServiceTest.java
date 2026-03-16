package fourthargument.eris.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import fourthargument.eris.api.model.User;
import fourthargument.eris.api.services.JwtService;

class JwtServiceTest {

    private JwtService jwtService;
    private User userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Use reflection to set the @Value fields
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        userDetails = new User();
        userDetails.setEmail("test@example.com");
        userDetails.setUsername("testuser");
    }

    @Test
    void generateToken_andExtractUsername() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        // User.getUsername() returns email (overridden in User entity)
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void generateToken_withExtraClaims() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtService.generateToken(claims, userDetails);

        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_validToken() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_wrongUser() {
        String token = jwtService.generateToken(userDetails);

        User otherUser = new User();
        otherUser.setEmail("other@example.com");

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_expiredToken() {
        // Create a service with very short expiration
        JwtService shortLivedService = new JwtService();
        ReflectionTestUtils.setField(shortLivedService, "secretKey",
                "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b");
        ReflectionTestUtils.setField(shortLivedService, "jwtExpiration", -1000L); // already expired

        String token = shortLivedService.generateToken(userDetails);

        // Token was created with a past expiration date, so parsing it will
        // either return false or throw ExpiredJwtException
        try {
            boolean valid = jwtService.isTokenValid(token, userDetails);
            assertFalse(valid);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Expected: expired tokens throw on parse
            assertTrue(true);
        }
    }

    @Test
    void getExpirationTime() {
        assertEquals(3600000L, jwtService.getExpirationTime());
    }
}
