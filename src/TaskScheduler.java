import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class TaskScheduler {

    private final PriorityBlockingQueue<Task> queue;
    private final ExecutorService workerPool;
    private final AtomicLong completedTasks = new AtomicLong();

    private volatile boolean running = true;

    public TaskScheduler(int workers) {
        this.queue = new PriorityBlockingQueue<>();
        this.workerPool = Executors.newFixedThreadPool(workers);
    }

    public void start() {
        // Dispatcher thread
        Thread dispatcher = new Thread(() -> {
            while (running || !queue.isEmpty()) {
                try {
                    Task task = queue.take(); // blocks
                    workerPool.submit(() -> {
                        try {
                            task.getAction().run();
                        } finally {
                            completedTasks.incrementAndGet();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        dispatcher.setDaemon(true);
        dispatcher.start();
    }

    public void scheduleTask(Task task) {
        if (!running) {
            throw new IllegalStateException("Scheduler is shut down");
        }
        queue.offer(task);
    }

    public long getCompletedTaskCount() {
        return completedTasks.get();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void shutdown() {
        running = false;
        workerPool.shutdown();
    }
}
