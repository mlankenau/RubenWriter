package de.lankenau.rubenwrite.data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BookDAO {
	public static int countWords(String s) {
		int count = 0;
		StringTokenizer wordTokenizer = new StringTokenizer(s, ", ;\"");
		while (wordTokenizer.hasMoreTokens()) {
			wordTokenizer.nextToken();
			count++;
		}
		
		return count;
	}
	
	public static TextBlock getText(int firstWord, int nwords) throws Exception {		
		
                InputStream inputStream = new BookDAO().getClass().getClassLoader().getResourceAsStream("die_abenteuer_von_tom_sawyer.txt");
                //InputStream inputStream = new FileInputStream("books/project_gutenberg.txt");
		InputStreamReader streamReader = new java.io.InputStreamReader(inputStream, "UTF-8");
		
		StringBuffer buffer = new StringBuffer();
		final int bufSize = 1024;
		char[] charBuf = new char[bufSize];
		while (true) {
			int nread = streamReader.read(charBuf, 0, bufSize);
			if (nread <= 0) break;			
			buffer.append(charBuf, 0, nread);
		}
				
		//StringTokenizer sentenceTokenizer = new StringTokenizer(buffer.toString(), ".!?");
		//ArrayList<String> sentenceList = new ArrayList<String>();
		//while (sentenceTokenizer.hasMoreTokens()) {
		//	sentenceList.add(sentenceTokenizer.nextToken());
		//}
		
		String[] words = buffer.toString().split(" ");
		
		int start = firstWord;
		int wordCount = 0;
		StringBuffer resultBuffer = new StringBuffer();
		
		for (int i=firstWord; i<firstWord+nwords; i++) {
		    resultBuffer.append(words[i % words.length]);	
		    resultBuffer.append(' ');
		}
		
		System.out.println("Wordcount: " + wordCount);		
		
		String text = resultBuffer.toString().trim();
		text = text.replace("[^a]", "");
		
                TextBlock block = new TextBlock();
                block.text = format(text);
                block.firstWord = firstWord;
                block.lastWord = firstWord + wordCount - 1;
		return block;
	}
	
	
	
	public static String format(String input) {
		input = input.replace('\r', ' ');
		input = input.replace('\n', ' ');
		StringBuffer buffer = new StringBuffer();
		char last = 0;
		int count = 0;
		for (char c : input.toCharArray()){ 
			boolean ignore = false;
			
			if (!"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\r\n ,.;:-'\"+*!$%&/()[]= öäüÖÄÜß".contains(""+c))
			    ignore = true;
			
			
			if (c == '_') c = ' ';
			
			if (c == ' ' && (last == ' ' || last == '\t')) ignore = true; 
			if (c == '\r' || c == '\n') ignore = true;
			
			if (c == ' ' && count > 70) {
				count = 0;
				buffer.append('\n');
			}
			
			if (!ignore) 	 
				buffer.append(c);
				
			last = c;
			count++;
		}
		
		return buffer.toString();
	}
}
