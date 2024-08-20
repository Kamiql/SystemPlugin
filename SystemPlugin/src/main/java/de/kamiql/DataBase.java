package de.kamiql;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataBase implements AutoCloseable {
    private static DataBase instance;
    private static MongoClient mongoClient;
    private static MongoCollection<Document> fileCollection;

    public DataBase(String connectionString, String databaseName, String collectionName) {
        mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        fileCollection = database.getCollection(collectionName);
    }

    public MongoCollection<Document> collection() {
        return fileCollection;
    }

    public MongoClient client() {
        return mongoClient;
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    public static String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
