package de.lankenau.rubenwrite;

import java.io.IOException;
import java.io.InputStreamReader;


public class Network {
	public static void start() {		
		Process p;
		try {
			p = Runtime.getRuntime().exec("/sbin/iptables", new String[] { "--delete", "OUTPUT",  "1"});
			readStream(p);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}	    		
	}

	private static void readStream(Process p) throws IOException {
		InputStreamReader reader = new InputStreamReader(p.getInputStream());
		int c;
		while ((c = reader.read()) != -1) {
			System.out.print((char)c);
		}
	}

	public static void stop() {		
		Process p;
		try {
			p = Runtime.getRuntime().exec("/sbin/iptables", new String[] { "-A", "OUTPUT", "-j", "DROP"});
			readStream(p);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}	    		
	}

}
