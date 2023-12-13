package base.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Objects;

public class ConnectionMongoDb {
    protected String connectionString;
    protected String db;
    protected MongoDatabase mongoDB;
    protected MongoClient mongoClient;
    protected MongoCollection<Document> collection;

    public void openMongoConnection() {
        mongoClient = MongoClients.create(connectionString);
        mongoDB = mongoClient.getDatabase(db);
    }

    public void closeMongoConnection() {
        if (Objects.nonNull(mongoClient)) {
            mongoClient.close();
        }
    }
}