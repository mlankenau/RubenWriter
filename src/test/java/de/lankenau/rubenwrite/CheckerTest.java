package de.lankenau.rubenwrite;

import static org.junit.Assert.*;

import org.junit.Test;

public class CheckerTest {

	@Test
	public void testCheck_Simplest() {
		int result = Checker.check("Hallo", "Hallo");
		assertEquals(-1, result);
	}


	@Test
	public void testCheck_False() {
		int result = Checker.check("Hollo", "Hallo");
		assertEquals(1, result);
	}

	
	@Test
	public void testCheck_IgnoreWhitespace_01() {
		int result = Checker.check(" Hallo", "Hallo");
		assertEquals(-1, result);
	}

	@Test
	public void testCheck_IgnoreWhitespace_02() {
		int result = Checker.check("Hallo", "  Hallo");
		assertEquals(-1, result);
	}

	
}
