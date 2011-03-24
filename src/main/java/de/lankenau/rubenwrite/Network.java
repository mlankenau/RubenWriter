package de.lankenau.rubenwrite;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;

public class Network {
	public static void start() {		
		try {
			File f = new File("/tmp/inet_on");
			f.createNewFile();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void stop() {		
		try {
			File f = new File("/tmp/inet_on");
			f.delete();
			stopBrowser();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	// killall firefox-bin
        public static void stopBrowser() {
                Process p;
                try { 
                        p = Runtime.getRuntime().exec("/usr/bin/killall firefox-bin");
                        readStream(p);
                        p.waitFor();
                        System.out.println("Exit valud of iptables: "+ p.exitValue());
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

}
