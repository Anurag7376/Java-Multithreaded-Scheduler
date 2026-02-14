import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws Exception {

        TaskScheduler scheduler = new TaskScheduler(3);
        scheduler.start();

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);



        server.createContext("/", exchange -> {

    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(405, -1);
        return;
    }

    int queueSize = scheduler.getQueueSize();
    long completed = scheduler.getCompletedTaskCount();

    String response =
            "<html>" +
            "<head><title>Java Task Scheduler</title></head>" +
            "<body style='font-family: Arial;'>" +
            "<h2>🚀 Multithreaded Task Scheduler is Running</h2>" +
            "<p><strong>Status:</strong> Healthy</p>" +
            "<p><strong>Queue Size:</strong> " + queueSize + "</p>" +
            "<p><strong>Completed Tasks:</strong> " + completed + "</p>" +
            "<hr/>" +
            "<h3>Available Endpoints:</h3>" +
            "<ul>" +
            "<li>POST /submit?priority=5</li>" +
            "<li>GET /metrics</li>" +
            "<li>GET /</li>" +
            "</ul>" +
            "<hr/>" +
            "<p>Built with Core Java | ExecutorService | PriorityBlockingQueue</p>" +
            "</body>" +
            "</html>";

    exchange.getResponseHeaders().add("Content-Type", "text/html");
    exchange.sendResponseHeaders(200, response.getBytes().length);

    try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
    }
});








        server.createContext("/submit", exchange -> {

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            URI uri = exchange.getRequestURI();

            int parsedPriority = 5; // default

            String query = uri.getQuery();
            if (query != null) {
                for (String p : query.split("&")) {
                    String[] kv = p.split("=");
                    if (kv.length == 2 && kv[0].equals("priority")) {
                        parsedPriority = Integer.parseInt(kv[1]);
                    }
                }
            }

            final int taskPriority = parsedPriority;
            final String taskId = UUID.randomUUID().toString();

            scheduler.scheduleTask(
                    new Task(taskId, taskPriority, () -> {
                        try {
                            System.out.println(
                                    "Running task " + taskId +
                                            " with priority " + taskPriority
                            );
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    })
            );

            String response = "Task submitted: " + taskId;

            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });


        server.createContext("/metrics", exchange -> {

    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(405, -1);
        return;
    }

    int queueSize = scheduler.getQueueSize();
    long completed = scheduler.getCompletedTaskCount();

    String response =
            "{\n" +
            "  \"queueSize\": " + queueSize + ",\n" +
            "  \"completedTasks\": " + completed + "\n" +
            "}";

    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, response.getBytes().length);

    try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes());
    }
});


        server.start();

        System.out.println("Scheduler service running on port " + port);
    }
}
