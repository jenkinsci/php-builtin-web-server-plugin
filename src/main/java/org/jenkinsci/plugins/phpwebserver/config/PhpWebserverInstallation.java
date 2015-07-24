package org.jenkinsci.plugins.phpwebserver.config;

import static hudson.init.InitMilestone.EXTENSIONS_AUGMENTED;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Functions;
import hudson.init.Initializer;
import hudson.model.EnvironmentSpecific;
import hudson.model.TaskListener;
import hudson.model.Node;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolProperty;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Handle PHP installations.
 * 
 * @author Fengtan https://github.com/Fengtan/
 *
 */
public class PhpWebserverInstallation extends ToolInstallation implements NodeSpecific<PhpWebserverInstallation>, EnvironmentSpecific<PhpWebserverInstallation> {

    @DataBoundConstructor
    public PhpWebserverInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    public static transient final String DEFAULT = "Default";
    
    private static final Logger LOGGER = Logger.getLogger(PhpWebserverInstallation.class.getName());

    /**
     * Get PHP executable.
     */
    public String getPhpExe() {
        return getHome();
    }

    /**
     * Return all installations.
     */
    private static PhpWebserverInstallation[] getInstallations(DescriptorImpl descriptor) {
    	PhpWebserverInstallation[] installations = null;
        try {
            installations = descriptor.getInstallations();
        } catch (NullPointerException e) {
            installations = new PhpWebserverInstallation[0];
        }
        return installations;
    }

    /**
     * Return the default installation.
     */
    public static PhpWebserverInstallation getDefaultInstallation() {
        DescriptorImpl phpTools = Jenkins.getInstance().getDescriptorByType(PhpWebserverInstallation.DescriptorImpl.class);
        PhpWebserverInstallation tool = phpTools.getInstallation(PhpWebserverInstallation.DEFAULT);
        if (tool != null) {
            return tool;
        } else {
        	PhpWebserverInstallation[] installations = phpTools.getInstallations();
            if (installations.length > 0) {
                return installations[0];
            } else {
                onLoaded();
                return phpTools.getInstallations()[0];
            }
        }
    }
    
	@Override
	public PhpWebserverInstallation forEnvironment(EnvVars environment) {
        return new PhpWebserverInstallation(getName(), environment.expand(getHome()), Collections.<ToolProperty<?>>emptyList());
	}

	@Override
	public PhpWebserverInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException {
        return new PhpWebserverInstallation(getName(), translateFor(node, log), Collections.<ToolProperty<?>>emptyList());
	}

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    @Initializer(after=EXTENSIONS_AUGMENTED)
    public static void onLoaded() {
        // Create default tool installation if needed. Uses "php" or migrates data from previous versions.
        DescriptorImpl descriptor = (DescriptorImpl) Jenkins.getInstance().getDescriptor(PhpWebserverInstallation.class);
        PhpWebserverInstallation[] installations = getInstallations(descriptor);

        if (installations != null && installations.length > 0) {
            //No need to initialize if there's already something.
            return;
        }

        String defaultPhpExe = Functions.isWindows() ? "php.exe" : "php";
        PhpWebserverInstallation tool = new PhpWebserverInstallation(DEFAULT, defaultPhpExe, Collections.<ToolProperty<?>>emptyList());
        descriptor.setInstallations(new PhpWebserverInstallation[] { tool });
        descriptor.save();
    }

    @Extension
    public static class DescriptorImpl extends ToolDescriptor<PhpWebserverInstallation> {

        /**
         * Load the persisted global configuration.
         */
        public DescriptorImpl() {
        	super();
            load();
        }
        
        /**
         * Human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return "PHP";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            setInstallations(req.bindJSONToList(clazz, json.get("tool")).toArray(new PhpWebserverInstallation[0]));
            save();
            return true;
        }
        
        /**
         * Executable should be a valid path.
         */
        public FormValidation doCheckHome(@QueryParameter File value) {
            Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
            String path = value.getPath();
            return FormValidation.validateExecutable(path);
        }
        
        /**
         * Get PHP installation.
         */
        public PhpWebserverInstallation getInstallation(String name) {
            for(PhpWebserverInstallation i : getInstallations()) {
                if(i.getName().equals(name)) {
                    return i;
                }
            }
            if (name.length() > 0) {
                LOGGER.log(Level.WARNING, "invalid phpTool selection {0}", name);
            }
            return null;
        }

        /**
         * Get all PHP installations.
         */
        public List<ToolDescriptor<? extends PhpWebserverInstallation>> getApplicableDesccriptors() {
            List<ToolDescriptor<? extends PhpWebserverInstallation>> r = new ArrayList<ToolDescriptor<? extends PhpWebserverInstallation>>();
            for (ToolDescriptor td : Jenkins.getInstance().<ToolInstallation,ToolDescriptor<?>>getDescriptorList(ToolInstallation.class)) {
                if (PhpWebserverInstallation.class.isAssignableFrom(td.clazz))
                    r.add(td);
            }
            return r;
        }

    }


}
