package modules.evaluation;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EvaluationScheduler implements AutoCloseable {
    private final EvaluationDAO dao = new EvaluationDAO();

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(task -> {
                Thread thread = new Thread(task, "codejudge-evaluation");
                thread.setDaemon(true);
                return thread;
            });

    private boolean started;
    private boolean failureReported;

    public synchronized void start() {
        if (started) {
            return;
        }

        executor.scheduleWithFixedDelay(
                this::checkExpiredAttempts,
                0,
                1,
                TimeUnit.SECONDS
        );

        started = true;
    }

    private void checkExpiredAttempts() {
        try {
            dao.finalizeExpiredAttempts();

            if (failureReported) {
                System.err.println(
                        "\nAutomatic evaluation has recovered.");
                failureReported = false;
            }
        } catch (SQLException | RuntimeException e) {
            if (!failureReported) {
                System.err.println(
                        "\nAutomatic evaluation failed. "
                        + "It will retry; check the database connection.");
                failureReported = true;
            }
        }
    }

    @Override
    public void close() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}