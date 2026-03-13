package fourthargument.eris;

import org.junit.jupiter.api.Test;

import fourthargument.eris.ErisApplicationMain;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ErisApplicationTests {

	@Test
	void applicationClassExists() {
		// Verify the main application class exists
		assertTrue(ErisApplicationMain.class.isAnnotationPresent(
				org.springframework.boot.autoconfigure.SpringBootApplication.class));
	}

}
