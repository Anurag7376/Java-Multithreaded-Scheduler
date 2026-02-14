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

    String response = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Java Multithreaded Task Scheduler</title>
            <style>
                body {
                    font-family: 'Segoe UI', Arial, sans-serif;
                    background: linear-gradient(135deg, #1e3c72, #2a5298);
                    color: #fff;
                    margin: 0;
                    padding: 40px;
                }
                .container {
                    max-width: 800px;
                    margin: auto;
                    background: rgba(255, 255, 255, 0.1);
                    padding: 30px;
                    border-radius: 12px;
                    box-shadow: 0 8px 20px rgba(0,0,0,0.3);
                }
                h1 {
                    margin-top: 0;
                }
                .card {
                    background: rgba(255,255,255,0.15);
                    padding: 20px;
                    border-radius: 8px;
                    margin-bottom: 20px;
                }
                .metric {
                    font-size: 22px;
                    font-weight: bold;
                }
                code {
                    background: rgba(0,0,0,0.3);
                    padding: 4px 8px;
                    border-radius: 4px;
                }
                footer {
                    margin-top: 20px;
                    font-size: 14px;
                    opacity: 0.8;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>🚀 Multithreaded Task Scheduler</h1>
                <div class="card">
                    <p>Status: <span class="metric">Healthy</span></p>
                    <p>Queue Size: <span class="metric">""" + queueSize + """</span></p>
                    <p>Completed Tasks: <span class="metric">""" + completed + """</span></p>
                </div>

                <div class="card">
                    <h3>Available Endpoints</h3>
                    <p><code>POST /submit?priority=5</code></p>
                    <p><code>GET /metrics</code></p>
                    <p><code>GET /</code></p>
                </div>

                <footer>
                    Built with Core Java • ExecutorService • PriorityBlockingQueue • Docker Deployment
                </footer>
            </div>
        </body>
        </html>
        """;

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
