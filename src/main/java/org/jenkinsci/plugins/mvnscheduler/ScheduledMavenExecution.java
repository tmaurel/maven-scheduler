package org.jenkinsci.plugins.mvnscheduler;


import antlr.ANTLRException;
import hudson.model.Cause;
import hudson.model.Item;
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
    public ScheduledMavenExecution(String name, String goals, String planning) throws ANTLRException {
        super(planning);
        this.name = name;
        this.goals = goals;
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

    @Override
    public TriggerDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void run() {
        job.scheduleBuild(0, new ScheduledCause(this.name, this.goals));
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
            return this.desc;
        }

        public String getGoals() {
            return this.goals;
        }

    }
}
