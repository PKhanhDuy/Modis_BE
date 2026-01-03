# 📸 Modis_BE Backend

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0+-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Redis](https://img.shields.io/badge/Redis-Cache_%26_PubSub-red.svg)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange.svg)](https://www.rabbitmq.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **A high-performance backend API for a "Locket-style" photo sharing widget app.** > This system features smart caching strategies for instant feed loading, hybrid real-time messaging, and ML-assisted image processing.

---

## 🚀 Overview

This project serves as the backend infrastructure for a social application where users share live photos to friends' home screens. The architecture addresses high-concurrency challenges and User Experience (UX) optimization through:

1.  **Zero-Latency Feed:** Using Redis to serve temporary low-res images immediately while high-res images load in the background.
2.  **Reliable Notifications:** A hybrid approach using RabbitMQ for durable push notifications and Redis Pub/Sub for ephemeral real-time signals.
3.  **Smart Processing:** Integration with ML Kit metadata for intelligent image handling.

## 🛠 Tech Stack

### Core
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security & JWT (Stateless Authentication)
* **Database:**  MongoDB

### Cloud & Storage
* **Image Storage:** [Cloudinary](https://cloudinary.com/) (Optimization, Transformation, Storage)
* **Caching layer:** Redis (Key-Value store for Feed & Session)

### Messaging & Async
* **Message Broker:** RabbitMQ (Queueing for FCM Push Notifications & Emails)
* **Real-time Signals:** Redis Pub/Sub

### Integration
* **Client AI:** Google ML Kit (Backend processes metadata for face detection & image labeling)

---

## 🏗 System Architecture

```mermaid
graph TD
    Client[Mobile App] -->|HTTPS| LoadBalancer
    LoadBalancer --> API[Spring Boot API]

    subgraph Data Layer
        API -->|Read/Write Hot Data| Redis[(Redis)]
        API -->|Persist Data| DB[(MongoDB)]
        API -->|Store Images| Cloud[(Cloudinary)]
    end

    subgraph Async Processing
        API -->|Pub Realtime Event| RedisPubSub[Redis Pub/Sub]
        API -->|Push Notification Task| RabbitMQ[RabbitMQ]
        
        RabbitMQ --> Worker[Notification Worker]
        Worker --> FCM[Firebase Cloud Messaging]
    end

    note[Redis acts as a buffer for <br/>instant feed loading] -.-> Redis
