package org.jenkinsci.plugins.mvnscheduler;


import antlr.ANTLRException;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import hudson.triggers.TimerTrigger;
import hudson.triggers.TriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * Defines maven goals that will be executed based on a CRON expression
 */
public final class ScheduledMavenExecution extends TimerTrigger implements Serializable {

    /**
     * The name of the execution. Just for recognition purpose
     */
    private final String name;

    /**
     * The goals to be executed
     */
    private final String goals;

    /**
     * Skip the triggered build if nothing changed since last one
     */
    private final boolean skippedIfNotChanged;

    /**
     * Has been rebuilt since last trigger
     */
    private boolean hasBeenRebuilt;


    private final TriggerDescriptor descriptor = new TriggerDescriptor() {

        @Override
        public boolean isApplicable(Item item) {
            return SchedulerBuildWrapper.isMavenProject(item);
        }

        @Override
        public String getDisplayName() {
            return Messages.MavenScheduler_ScheduledMavenExecution();
        }
    };

    @DataBoundConstructor
    public ScheduledMavenExecution(String name, String goals, String planning, boolean skippedIfNotChanged) throws ANTLRException {
        super(planning);
        this.name = name;
        this.goals = goals;
        this.skippedIfNotChanged = skippedIfNotChanged;
        this.hasBeenRebuilt = true;
    }

    public String getName() {
        return this.name;
    }

    public String getGoals() {
        return this.goals;
    }

    public String getPlanning() {
        return this.spec;
    }

    public boolean isSkippedIfNotChanged() {
        return this.skippedIfNotChanged;
    }

    public void setHasBeenRebuilt(boolean hasBeenRebuilt) {
        this.hasBeenRebuilt = hasBeenRebuilt;
    }

    @Override
    public TriggerDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public void run() {
        SchedulerBuildWrapper.DescriptorImpl wrapperDescriptor = (SchedulerBuildWrapper.DescriptorImpl) Hudson.getInstance().getDescriptor(SchedulerBuildWrapper.class);

        // If the admin disabled all triggered build, prevent scheduled execution
        if (null != wrapperDescriptor && !wrapperDescriptor.isDisabled()) {

            // Make sure this is a Maven project
            if (SchedulerBuildWrapper.isMavenProject(this.job)) {

                MavenModuleSet project = (MavenModuleSet) this.job;
                Run lastBuild = project.getLastBuild();

                // If last build failed, prevent scheduled execution
                if (null != lastBuild && Result.FAILURE != lastBuild.getResult()) {

                    // If project has not been rebuilt since last trigger and user said we shouldnt
                    // rebuild in this case, prevent execution
                    if (!(this.isSkippedIfNotChanged() && !this.hasBeenRebuilt)) {

                        // Finally, schedule build
                        this.job.scheduleBuild(0, new ScheduledCause(this.name, this.goals));
                        this.hasBeenRebuilt = false;
                    }
                }
            }
        }
    }

    /**
     * The {@code Cause} of {@code ScheduledMavenExecution} builds
     */
    public final class ScheduledCause extends Cause {

        private final String goals;
        private final String desc;

        public ScheduledCause(String desc, String goals) {
            this.goals = goals;
            this.desc = desc;
        }

        @Override
        public String getShortDescription() {
            return Messages.MavenScheduler_ScheduledMavenExecution() + " : " + this.desc;
        }

        public String getGoals() {
            return this.goals;
        }

    }
}
