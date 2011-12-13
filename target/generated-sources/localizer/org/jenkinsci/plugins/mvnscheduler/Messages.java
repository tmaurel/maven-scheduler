// CHECKSTYLE:OFF

package org.jenkinsci.plugins.mvnscheduler;

import org.jvnet.localizer.Localizable;
import org.jvnet.localizer.ResourceBundleHolder;

@SuppressWarnings({
    "",
    "PMD"
})
public class Messages {

    private final static ResourceBundleHolder holder = ResourceBundleHolder.get(Messages.class);

    /**
     * Goals field cannot be empty
     * 
     */
    public static String MavenScheduler_Error_EmptyGoals() {
        return holder.format("MavenScheduler.Error.EmptyGoals");
    }

    /**
     * Goals field cannot be empty
     * 
     */
    public static Localizable _MavenScheduler_Error_EmptyGoals() {
        return new Localizable(holder, "MavenScheduler.Error.EmptyGoals");
    }

    /**
     * Maven Scheduler
     * 
     */
    public static String MavenScheduler_DisplayName() {
        return holder.format("MavenScheduler.DisplayName");
    }

    /**
     * Maven Scheduler
     * 
     */
    public static Localizable _MavenScheduler_DisplayName() {
        return new Localizable(holder, "MavenScheduler.DisplayName");
    }

    /**
     * Planning field cannot be empty
     * 
     */
    public static String MavenScheduler_Error_EmptyPlanning() {
        return holder.format("MavenScheduler.Error.EmptyPlanning");
    }

    /**
     * Planning field cannot be empty
     * 
     */
    public static Localizable _MavenScheduler_Error_EmptyPlanning() {
        return new Localizable(holder, "MavenScheduler.Error.EmptyPlanning");
    }

    /**
     * Scheduled Maven Execution
     * 
     */
    public static String MavenScheduler_ScheduledMavenExecution() {
        return holder.format("MavenScheduler.ScheduledMavenExecution");
    }

    /**
     * Scheduled Maven Execution
     * 
     */
    public static Localizable _MavenScheduler_ScheduledMavenExecution() {
        return new Localizable(holder, "MavenScheduler.ScheduledMavenExecution");
    }

}
