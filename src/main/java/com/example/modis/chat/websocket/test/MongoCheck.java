package com.example.modis.chat.websocket.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Component
public class MongoCheck implements CommandLineRunner {

    @Value("${spring.data.mongodb.uri:mongodb+srv://tanluc:123@modis.hazw95q.mongodb.net/Modis?appName=Modis}")
    private String mongoUri;

    @Override
    public void run(String... args) {
        System.out.println("====== MONGO URI CHECK ======");
        System.out.println(mongoUri);
        System.out.println("=============================");
    }
}
