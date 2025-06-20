package config;

import service.VulnerabilityService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class CveImportScheduler implements ServletContextListener {
    private Timer timer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true);
        // schedule first run at 2:00 AM
        Date firstRun = computeNextRun(2, 0);
        long period = 1000L * 60 * 60 * 24; // 24h
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new VulnerabilityService().importCVEs();
            }
        }, firstRun, period);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Compute the next Date at which the clock hits the given hour:minute.
     * If that time today is already past, returns tomorrow at that time.
     */
    private Date computeNextRun(int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        Date candidate = cal.getTime();
        if (candidate.before(new Date())) {
            // already past for today → schedule tomorrow
            cal.add(Calendar.DATE, 1);
            candidate = cal.getTime();
        }
        return candidate;
    }
}
