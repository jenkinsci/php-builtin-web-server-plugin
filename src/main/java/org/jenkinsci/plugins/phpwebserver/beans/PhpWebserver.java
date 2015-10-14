package org.jenkinsci.plugins.phpwebserver.beans;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Map;

import org.jenkinsci.plugins.phpwebserver.config.PhpWebserverInstallation;

/**
 * Run/stop a PHP built-in web server.
 *
 * @author Fengtan https://github.com/fengtan/
 *
 */
public class PhpWebserver {

	private Process process;
    private PrintStream _logger;
    private int _port;
    private String _host;
    private File _root;
    private Boolean _importEnvironment;
    private Map<String,String> _buildEnvVars;

	/**
	 * Start web server.
	 */
	public PhpWebserver(int port, String host, File root, boolean importEnvironment) throws IllegalStateException {
		if (!portAvailable(port)) {
			throw new IllegalStateException("Port "+port+" is already used");
		}

        this._logger = null;
        this._port = port;
        this._host = host;
        this._root = root;
        this._importEnvironment = importEnvironment;
	}

    public void start() throws IOException {
        String php = PhpWebserverInstallation.getDefaultInstallation().getPhpExe();
        ProcessBuilder pb = new ProcessBuilder(php, "--server", this._host + ":" + this._port, "--docroot", this._root.getAbsolutePath());
        if(this._importEnvironment){
            Map<String, String> phpEnv = pb.environment();
            for (Map.Entry<String, String> entry : this.getBuildVars().entrySet()) {
                if(this.getLogger() != null){
                    this.getLogger().println("Importing " + entry.getKey() + "=" + entry.getValue());
                }
                phpEnv.put(entry.getKey(), entry.getValue());
            }
        }
		process = pb.start();
    }

    public PhpWebserver setLogger(PrintStream logger) {
        this._logger = logger;
        return this;
    }

    public PrintStream getLogger() {
        return this._logger;
    }

    public PhpWebserver setBuildVars(Map<String,String> env) {
        this._buildEnvVars = env;
        return this;
    }

    public Map<String,String> getBuildVars() {
        return this._buildEnvVars;
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
