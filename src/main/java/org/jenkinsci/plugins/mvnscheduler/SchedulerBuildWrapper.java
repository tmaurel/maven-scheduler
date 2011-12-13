package org.jenkinsci.plugins.mvnscheduler;


import hudson.Extension;
import hudson.Launcher;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;

/**
 * This {@code BuildWrapper} changes current project's goals before
 * building according to {@code ScheduledCause}and changes it
 * back to original goals once built.
 *
 * @author tmaurel
 */
public class SchedulerBuildWrapper extends BuildWrapper {

    private static final Logger LOGGER = Logger.getLogger(SchedulerBuildWrapper.class.getName());

    private final List<ScheduledMavenExecution> executions;

    private final AbstractProject parent;

    public SchedulerBuildWrapper(AbstractProject parent, List<ScheduledMavenExecution> commands) {
        this.executions = commands;
        this.parent = parent;
        for (ScheduledMavenExecution command : commands) {
            try {
                // schedule executions
                parent.addTrigger(command);
                command.start(parent, true);
            } catch (IOException e) {
                LOGGER.error("Couldn't add triggers to project", e);
            }
        }
    }

    public List<ScheduledMavenExecution> getExecutions() {
        return this.executions;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        boolean isScheduledBuild = false;
        String currentGoals = "";
        MavenModuleSet mvnProject = null;
        ScheduledMavenExecution.ScheduledCause scheduledCause = null;

        List causes = build.getCauses();
        for (Object cause : causes) {
            // checks if this build has been triggered by this plugin
            if (cause instanceof ScheduledMavenExecution.ScheduledCause) {
                scheduledCause = (ScheduledMavenExecution.ScheduledCause) cause;
                isScheduledBuild = true;
            }
        }

        if (isScheduledBuild && isMavenProject(this.parent)) {
            mvnProject = (MavenModuleSet) this.parent;
            currentGoals = mvnProject.getGoals();
            // changes goals to use
            mvnProject.setGoals(scheduledCause.getGoals());
        }
        return buildEnvironnement(mvnProject, currentGoals);
    }

    private Environment buildEnvironnement(final MavenModuleSet project, final String goals) {
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                if (project != null && StringUtils.isNotBlank(goals)) {
                    project.setGoals(goals);
                }
                return true;
            }
        };
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        private boolean isDisabled;

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            this.isDisabled = formData.getBoolean("isDisabled");
            save();
            return super.configure(req, formData);
        }

        public boolean isDisabled() {
            return this.isDisabled;
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return SchedulerBuildWrapper.isMavenProject(item);
        }

        @Override
        public String getDisplayName() {
            return Messages.MavenScheduler_DisplayName();
        }

        @Override
        public SchedulerBuildWrapper newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            List<Ancestor> ancestors = req.getAncestors();
            for (Ancestor ancestor : ancestors) {
                Object object = ancestor.getObject();
                if (SchedulerBuildWrapper.isMavenProject(object)) {
                    return new SchedulerBuildWrapper((AbstractProject) object,
                            req.bindParametersToList(ScheduledMavenExecution.class, "scheduled-execution."));
                }
            }
            return null;
        }
    }

    public static boolean isMavenProject(Object object) {
        return object instanceof MavenModuleSet;
    }

}
