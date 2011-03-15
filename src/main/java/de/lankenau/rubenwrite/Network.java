package de.lankenau.rubenwrite;

import java.io.IOException;

public class Network {
	public static void start() {		
		Process p;
		try {
			p = Runtime.getRuntime().exec("/sbin/iptables", new String[] { "--delete", "OUTPUT",  "1"});
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}	    		
	}

	public static void stop() {		
		Process p;
		try {
			p = Runtime.getRuntime().exec("/sbin/iptables", new String[] { "-A", "OUTPUT", "-j", "DROP"});
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}	    		
	}

}
