public class Task implements Comparable<Task> {

    private final String taskId;
    private final int priority;
    private final Runnable action;

    public Task(String taskId, int priority, Runnable action) {
        this.taskId = taskId;
        this.priority = priority;
        this.action = action;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getPriority() {
        return priority;
    }

    public Runnable getAction() {
        return action;
    }

    // Higher priority first
    @Override
    public int compareTo(Task other) {
        return Integer.compare(other.priority, this.priority);
    }
}
