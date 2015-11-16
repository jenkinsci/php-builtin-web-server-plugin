package org.jenkinsci.plugins.phpwebserver.buildwrappers;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;

import org.jenkinsci.plugins.phpwebserver.beans.PhpWebserver;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Build wrapper to run a PHP built-in web server.
 *
 * @author Fengtan https://github.com/fengtan/
 *
 */
public class PhpWebserverBuildWrapper extends BuildWrapper {

	private final int port;
	private final String host;
	private final String root;
    private final boolean importEnvironment;

	@DataBoundConstructor
	public PhpWebserverBuildWrapper(int port, String host, String root, boolean environment) {
		this.port = port;
		this.host = host;
		this.root = root;
        this.importEnvironment = environment;
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public String getRoot() {
		return root;
	}

    public Boolean getImportEnvironment(){
        return importEnvironment;
    }

	@Override
	public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
		File rootDir = new File(build.getWorkspace().getRemote(), root);
		try {
			listener.getLogger().println("[PHP WEB SERVER] Starting server " + host+":"+port+" with document root "+rootDir.getAbsolutePath()+"...");
			final PhpWebserver server = new PhpWebserver(port, host, rootDir, importEnvironment);
            server
                .setLogger(listener.getLogger())
                .setBuildVars(build.getEnvironment(listener))
                .start();
			return new Environment() {
				@Override
				public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
					listener.getLogger().println("[PHP WEB SERVER] Stopping server...");
					server.stop();
					return true;
				}
			};
		} catch(IllegalStateException e) {
			listener.getLogger().println("[PHP WEB SERVER] Could not start server: "+e.getMessage());
			return null;
		} catch (IOException e) {
			listener.getLogger().println("[PHP WEB SERVER] Could not start server: "+e.getMessage());
			return null;
		} catch (InterruptedException e) {
            listener.getLogger().println("[PHP WEB SERVER] Could not start server: "+e.getMessage());
			return null;
        }
	}

    @Override
    public Environment setUp(Build build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        return setUp(build, launcher, listener);
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(PhpWebserverBuildWrapper.class);
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Run a PHP built-in web server";
        }

        /**
         * Field 'port' should not be empty.
         * Field 'port' should be a valid port.
         * Field 'port' should not be a well-known port.
         */
        public FormValidation doCheckPort(@AncestorInPath AbstractProject project, @QueryParameter String value) throws IOException {
            if (0 == value.length()) {
            	return FormValidation.error("Please set a port number");
            }
            try {
            	int port = Integer.parseInt(value);
            	if (port < 1024 || port > 65535) {
            		return FormValidation.error("Should be 1024 <= port <= 65535");
            	}
            }
            catch (NumberFormatException e) {
            	return FormValidation.error("Should be a numerical value");
			}
        	return FormValidation.ok();
        }

        /**
         * Field 'host' should not be empty.
         */
        public FormValidation doCheckHost(@AncestorInPath AbstractProject project, @QueryParameter String value) throws IOException {
            if (0 == value.length()) {
            	return FormValidation.error("Please set a host");
            }
        	return FormValidation.ok();
        }

        /**
         * Field 'root' should not be empty.
         */
        public FormValidation doCheckRoot(@AncestorInPath AbstractProject project, @QueryParameter String value) throws IOException {
            if (0 == value.length()) {
            	return FormValidation.warning("Workspace root will be used as document root");
            }
        	return FormValidation.ok();
        }

    }
}
