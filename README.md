# Multithreaded Task Scheduler (Core Java)

A production-style, priority-based multithreaded task scheduling service built in **Core Java**.  
The system exposes lightweight HTTP APIs to submit tasks remotely and monitor runtime metrics, and executes tasks concurrently using a fixed-size worker pool.

This project focuses on **concurrency, thread-safety, and backend system design**, rather than UI or persistence, making it suitable for SDE interviews and large-scale backend roles.

---

## 🚀 Features

- Priority-based task scheduling (higher priority tasks are executed first)
- Concurrent task execution using a fixed-size thread pool
- Thread-safe in-memory scheduling queue using `PriorityBlockingQueue`
- Lightweight HTTP API for remote task submission
- Runtime metrics endpoint for observability
- Dockerized and deployed as a cloud service

---

## 🧱 Architecture



Client
|
POST /submit
|
Java HTTP Server
|
PriorityBlockingQueue
|
Dispatcher Thread
|
Fixed Thread Pool (Workers)


The scheduler follows a dispatcher–worker architecture:
- a dispatcher thread continuously polls the highest-priority task
- tasks are executed by a bounded worker pool

---

## 🛠️ Tech Stack

- Java (Core Java, no frameworks)
- Java Concurrency utilities (`ExecutorService`, `PriorityBlockingQueue`, `AtomicLong`)
- Built-in Java HTTP server (`com.sun.net.httpserver.HttpServer`)
- Docker
- Cloud deployment

---

## 📌 API Endpoints

### Submit a task



POST /submit?priority=<number>


**Example**



POST /submit?priority=8


**Response**



Task submitted: <task-id>


---

### Get runtime metrics



GET /metrics


**Response**

```json
{
  "queueSize": 2,
  "completedTasks": 14
}
```

# How It Works

Incoming requests are accepted through a lightweight Java HTTP server.

Each request creates a task with an associated priority.

Tasks are stored in a PriorityBlockingQueue.

A dispatcher thread continuously takes the highest-priority task from the queue.

Tasks are submitted to a fixed-size worker pool for concurrent execution.

Runtime statistics are maintained and exposed through the /metrics endpoint.

The scheduler is non-preemptive — once a task starts executing, it is not interrupted.

# Running Locally

From the src directory:

javac *.java
jar --create --file scheduler.jar --main-class Main *.class
java -jar scheduler.jar


The service will start on:

http://localhost:8080


# Docker

Build the image:

docker build -t task-scheduler .


Run the container:

docker run -p 8080:8080 task-scheduler

# Deployment

The service is deployed on Render as a Docker-based web service and binds to the runtime-assigned port using the PORT environment variable.

It runs as a long-lived backend service and accepts remote requests through a public URL.

# Metrics & Observability

The scheduler exposes:

current queue size

total number of completed tasks

through the /metrics endpoint, enabling basic runtime monitoring and backlog inspection.

# Thread Safety

Task scheduling uses a PriorityBlockingQueue for safe concurrent access.

Task execution is managed by a bounded ExecutorService.

Completed task count is tracked using atomic counters.

# Future Enhancements

Delayed and scheduled task execution

Task cancellation and timeout support

Configurable worker pool size

Persistent task storage

Detailed execution latency and throughput metrics

# Project Goal

This project demonstrates practical understanding of:

multithreading and synchronization

concurrent data structures

thread pool design

backend service architecture

production-style deployment and observability






-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
> [!IMPORTANT]
> **The system is intentionally built without external frameworks to focus on core Java concurrency fundamentals and system behavior.**
