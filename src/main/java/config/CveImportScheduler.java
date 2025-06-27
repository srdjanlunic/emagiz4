package config;

import service.VulnerabilityService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * CveImportScheduler is a servlet context listener that sets up a daily scheduled task
 * to import CVEs (Common Vulnerabilities and Exposures) from an external feed.
 *
 * This automated nightly job supports the user story:
 * "As a System Owner, I want to import CVEs on a nightly basis from our 3rd party feed automatically."
 */
@WebListener
public class CveImportScheduler implements ServletContextListener {
    
    // Timer to schedule and manage the recurring task
    private Timer timer;
    // Singleton service instance to avoid repeated instantiation
    private static final VulnerabilityService vulnerabilityService = new VulnerabilityService();
    
    /**
     * Initializes the context by scheduling a daily task at 2:00 AM to import CVEs.
     * This uses a daemon thread (via `new Timer(true)`), so it won't block shutdown.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true); // Use daemon thread for scheduled task
        
        // Schedule the first run at 2:00 AM
        Date firstRun = computeNextRun(2, 0);
        
        // Set the repeat interval to 24 hours (in milliseconds)
        long period = 1000L * 60 * 60 * 24;
        
        // Schedule the CVE import task
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Use singleton service instance instead of creating new ones
                vulnerabilityService.importCVEs();
            }
        }, firstRun, period);
    }
    
    /**
     * Cleans up and cancels the scheduled timer when the servlet context is destroyed.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
        }
    }
    
    /**
     * Computes the next run time at the given hour and minute.
     * If the time for today has already passed, it schedules for the same time tomorrow.
     *
     * @param hourOfDay the hour of the day (0–23)
     * @param minute the minute of the hour (0–59)
     * @return the Date object representing the next valid run time
     */
    private Date computeNextRun(int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        
        Date candidate = cal.getTime();
        
        // If time already passed today, schedule for tomorrow
        if (candidate.before(new Date())) {
            cal.add(Calendar.DATE, 1);
            candidate = cal.getTime();
        }
        return candidate;
    }
}
