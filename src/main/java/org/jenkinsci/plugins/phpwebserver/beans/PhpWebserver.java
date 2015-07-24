package org.jenkinsci.plugins.phpwebserver.beans;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import org.jenkinsci.plugins.phpwebserver.config.PhpWebserverInstallation;

public class PhpWebserver {
	
	private Process process;
	
	public PhpWebserver(int port, String host, File root) throws IOException {
		if (!portAvailable(port)) {
			throw new IllegalStateException("Port "+port+" is already used");
		}
		String php = PhpWebserverInstallation.getDefaultInstallation().getPhpExe();
		process = new ProcessBuilder(php, "--server", host+":"+port, "--docroot", root.getAbsolutePath()).start();
	}

	// TODO documentation
	public void stop() {
		if (process != null) {
			process.destroy();	
		}
	}
	
	/**
	 * Return false if 'port' is already used, true otherwise.
	 */
	private static boolean portAvailable(int port) {
	    ServerSocket socket = null;
	    try {
	        socket = new ServerSocket(port);
	        socket.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    	// Do not handle exception.
	    } finally {
	        if (socket != null) {
	            try {
	                socket.close();
	            } catch (IOException e) {
	            	// Do not handle exception.
	            }
	        }
	    }
	    return false;
	}
}
