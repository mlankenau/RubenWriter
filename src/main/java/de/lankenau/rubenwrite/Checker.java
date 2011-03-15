package de.lankenau.rubenwrite;

public class Checker {

	private static boolean isWhitespace(char c) {
		return (c == ' ' || c == '\r' || c == '\n' || c == '\t');
	}
	public static int check(String userInput, String originalText) {
		int j = 0;
		
		for (int i=0; i<userInput.length(); i++) {
			char c1 = userInput.charAt(i);			
			if (isWhitespace(c1)) continue;
			
			char c2 = ' ';
			while (isWhitespace(c2)) {
				c2 = originalText.charAt(j++);
			}
			
			if (c1 != c2) return i;
		}
		
		return -1;
	}
}
