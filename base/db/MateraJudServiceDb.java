package base.db;

import base.util.PropertiesUtil;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MateraJudServiceDb extends ConnectionMongoDb {

    private PropertiesUtil propertiesUtil;

    public MateraJudServiceDb() throws IOException {
        propertiesUtil = new PropertiesUtil();
        db = "orbi-jud-manager";
        connectionString = "mongodb://" + propertiesUtil.getPropertyByName("orbi.db.matera.jud.user")
                + ":" + propertiesUtil.getPropertyByNameBase64("orbi.db.matera.jud.password")
                + "@" + propertiesUtil.getPropertyByName("orbi.db.matera.jud.baseURI")
                + ":" + propertiesUtil.getPropertyByName("orbi.db.matera.jud.port")
                + "/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false";
    }

    public JSONObject getBlockOperationsRecent() {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialOrder");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$sort",
                                new Document("time", -1L)),
                        new Document("$limit", 1L),
                        new Document("$project",
                                new Document("blockExternalId", "$blockExternalId"))))
                .allowDiskUse(true);

        MongoCursor<Document> cursor = result.iterator();

        return new JSONObject(cursor.next());
    }

    public JSONObject getBlockOperationsRecentForTransfer() {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialOrder");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$sort",
                                new Document("time", -1L)),
                        new Document("$limit", 1L),
                        new Document("$project",
                                new Document("blockId", "$blockId")
                                        .append("document", "$document"))))
                .allowDiskUse(true);

        MongoCursor<Document> cursor = result.iterator();

        return new JSONObject(cursor.next());
    }

    public Object getBlockOperationsRecentWithoutTransfer() {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialOrder");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$sort",
                                new Document("time", -1L)),
                        new Document("$match",
                                new Document("judicialTransferModels",
                                        new Document("$exists", true)
                                                .append("$size", 0L))),
                        new Document("$limit", 1L),
                        new Document("$project",
                                new Document("blockId", "$blockId")
                                        .append("document", "$document"))))
                .allowDiskUse(true);

        MongoCursor<Document> cursor = result.iterator();

        return new JSONObject(cursor.next());
    }

    public Object getBlockOperationsRecentWithTransfer() {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialOrder");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$sort",
                                new Document("time", -1L)),
                        new Document("$match",
                                new Document("judicialTransferModels",
                                        new Document("$exists", true)
                                                .append("$size", 1L))),
                        new Document("$limit", 1L),
                        new Document("$project",
                                new Document("blockId", "$blockId")
                                        .append("document", "$document"))))
                .allowDiskUse(true);

        MongoCursor<Document> cursor = result.iterator();

        return new JSONObject(cursor.next());
    }

    public Object getMonitoringBlockedRecent() {
        openMongoConnection();
        collection = mongoDB.getCollection("Monitoring");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$sort",
                                new Document("time", -1L)),
                        new Document("$limit", 1L),
                        new Document("$project",
                                new Document("blockExternalId", "$blockExternalId")
                                        .append("document", "$document"))))
                .allowDiskUse(true);

        MongoCursor<Document> cursor = result.iterator();

        return new JSONObject(cursor.next());
    }

    public void insertJudicialBlockNotify(String blockExternalId) {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialBlockNotify");
        Document document = new Document();

        document.append("version", 0L);
        document.append("inclusionDate", LocalDateTime.now());
        document.append("_class", "br.com.realizecfi.orbijudmanager.repository.model.JudicialBlockNotifyModel");
        document.append("_id", UUID.randomUUID().toString());
        document.append("changeDate", LocalDateTime.now());
        document.append("userInclusion", "test-funcional");
        document.append("userChange", "test-funcional");
        document.append("originSystem", "F");
        document.append("entityType", "ORBI-JUD");
        document.append("blockExternalId", blockExternalId);

        collection.insertOne(document);
        closeMongoConnection();
    }

    public void deleteJudicialBlockNotify() {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialBlockNotify");
        collection.deleteMany(new Document());
    }

    public JSONObject getBlockNotify(String idBloqueioJud) {
        openMongoConnection();
        collection = mongoDB.getCollection("JudicialBlockNotify");

        var result = collection.find(eq("blockExternalId", idBloqueioJud));

        MongoCursor<Document> cursor = result.iterator();

        return new JSONObject(cursor.next());
    }

    public void deleteCollection(String collectionTable) {
        openMongoConnection();
        collection = mongoDB.getCollection(collectionTable);
        collection.deleteMany(new Document());
        closeMongoConnection();
    }
}
