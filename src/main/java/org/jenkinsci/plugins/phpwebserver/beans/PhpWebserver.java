package org.jenkinsci.plugins.phpwebserver.beans;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.jenkinsci.plugins.phpwebserver.config.PhpWebserverInstallation;

/**
 * Run/stop a PHP built-in web server.
 * 
 * @author Fengtan https://github.com/fengtan/
 *
 */
public class PhpWebserver {

	private Process process;

	/**
	 * Start web server.
	 */
	public PhpWebserver(int port, String host, File root) throws IOException {
		if (!portAvailable(port)) {
			throw new IllegalStateException("Port "+port+" is already used");
		}
		String php = PhpWebserverInstallation.getDefaultInstallation().getPhpExe();
		process = new ProcessBuilder(php, "--server", host+":"+port, "--docroot", root.getAbsolutePath()).start();
	}

	/**
	 * Stop web server.
	 */
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
