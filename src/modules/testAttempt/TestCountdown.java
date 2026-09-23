package modules.testAttempt;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TestCountdown implements AutoCloseable {
    private final LocalDateTime deadline;
    private final ScheduledExecutorService executor;
    private boolean closed;

    public TestCountdown(LocalDateTime deadline) {
        this.deadline = deadline;
        this.executor = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "codejudge-countdown");
            thread.setDaemon(true);
            return thread;
        });

        executor.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
    }

    private synchronized void tick() {
        if (closed) {
            return;
        }

        long millis = Duration.between(
                LocalDateTime.now(), deadline).toMillis();
        long seconds = Math.max(0, (millis + 999) / 1000);

        if (seconds == 0) {
            System.out.println(
                    "\nTime is up. Answers are closed. "
                    + "Automatic evaluation will finalize saved answers.");
            System.out.println(
                    "Press Enter to continue, then check My Results.");
            close();
            return;
        }

        System.out.printf(
                "%n[Time left: %02d:%02d]%n",
                seconds / 60, seconds % 60);
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            closed = true;
            executor.shutdownNow();
        }
    }
}