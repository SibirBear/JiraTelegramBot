package info.fermercentr.jiraAPI.schedule;

import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JiraScheduleTask {

    private static final Logger LOG = LogManager.getLogger(JiraScheduleTask.class);
    private final int TIMEOUT_FOR_TASK = 5;

    public void initTask(Runnable task) throws JiraApiException {
        LOG.info("[" + getClass() + "] " + " - Scheduled task is starting... " + task.getClass());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(task);

        try {
            future.get(TIMEOUT_FOR_TASK, TimeUnit.MINUTES);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            future.cancel(true);
            throw new JiraApiException("Scheduled task " + task.getClass() + " is cancelled until 5 minutes running. " + e);
        } finally {
            executorService.shutdownNow();
            LOG.info("[" + getClass() + "] " + " - Scheduled task is shutdown");
        }

    }

}
