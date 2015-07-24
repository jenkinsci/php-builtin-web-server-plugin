package org.jenkinsci.plugins.phpwebserver;

import java.io.IOException;

public class PhpWebserver {
	
	private Process process;
	
	public PhpWebserver(int port, String host, String root) throws IOException {
		// TODO redirect output stream to listener
		// TODO deal with IOExcpetion
		// TODO root relative to workspace
		process = new ProcessBuilder("php", "--server", host+":"+port, "--docroot", root).start();
	}

	// TODO documentation
	public void stop() {
		process.destroy(); // TODO unless null
	}
}
