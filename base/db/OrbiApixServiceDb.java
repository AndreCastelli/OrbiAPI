package base.db;

import base.util.DateTimeUtil;
import base.util.MathUtil;
import base.util.PropertiesUtil;
import com.mongodb.client.*;
import org.bson.BsonNull;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class OrbiApixServiceDb extends ConnectionMongoDb {

    private PropertiesUtil propertiesUtil;

    public OrbiApixServiceDb() throws IOException {
        propertiesUtil = new PropertiesUtil();
        db = "orbi-apix-service";
        connectionString = "mongodb://" + propertiesUtil.getPropertyByName("orbi.db.apix.service.user")
                + ":" + propertiesUtil.getPropertyByNameBase64("orbi.db.apix.service.password")
                + "@" + propertiesUtil.getPropertyByName("orbi.db.apix.service.baseURI")
                + ":" + propertiesUtil.getPropertyByName("orbi.db.apix.service.port")
                + "/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false";
    }

    public Object getInformationOnTransactionType01() {
        openMongoConnection();
        collection = mongoDB.getCollection("Transactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.TWO, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.TWO, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId")
                        .append("events", new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("amount", "$amount")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("INTERNAL_SEND", "INTERNAL_SCHEDULE")))),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "COMPLETED")),
                new Document("$match", new Document("events.eventType", new Document("$ne", "FRAUD_SUSPECTED"))),
                new Document("$unwind", "$events"),
                new Document("$match", new Document("events.eventType", "RECEIVED")),
                new Document("$group", new Document("_id", new BsonNull()).append("valorTransacoes",
                        new Document("$sum", new Document("$convert",
                                new Document("input", "$events.amount")
                                        .append("to", "double")))).append("qtdTransacoes",
                        new Document("$sum", 1L)))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();
            JSONObject trans01Json = new JSONObject(cursor.next());
            trans01Json.put("DetalhamentoTransacoes", 1);

            return trans01Json;
        } catch (NoSuchElementException e) {
            JSONObject trans01Json = new JSONObject();
            trans01Json.put("qtdTransacoes", 0);
            trans01Json.put("valorTransacoes", 0);
            trans01Json.put("DetalhamentoTransacoes", 1);

            return trans01Json;
        }
    }

    public Object getInformationOnTransactionType02() {
        openMongoConnection();
        collection = mongoDB.getCollection("Transactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.TWO, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.TWO, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId")
                        .append("events", new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("amount", "$amount")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("INTERNAL_SEND", "INTERNAL_SCHEDULE")))),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "COMPLETED")),
                new Document("$match", new Document("events.eventType", "FRAUD_SUSPECTED")),
                new Document("$unwind", "$events"),
                new Document("$match", new Document("events.eventType", "RECEIVED")),
                new Document("$group", new Document("_id", new BsonNull()).append("valorTransacoes",
                        new Document("$sum", new Document("$convert",
                                new Document("input", "$events.amount")
                                        .append("to", "double")))).append("qtdTransacoes",
                        new Document("$sum", 1L)))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();
            JSONObject trans02Json = new JSONObject(cursor.next());
            trans02Json.put("DetalhamentoTransacoes", 2);

            return trans02Json;
        } catch (NoSuchElementException e) {
            JSONObject trans02Json = new JSONObject();
            trans02Json.put("qtdTransacoes", 0);
            trans02Json.put("valorTransacoes", 0);
            trans02Json.put("DetalhamentoTransacoes", 2);

            return trans02Json;
        }
    }

    public Object getInformationOnTransactionType03() {
        openMongoConnection();
        collection = mongoDB.getCollection("Transactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.TWO, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.TWO, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId")
                        .append("events", new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("amount", "$amount")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("INTERNAL_SEND", "INTERNAL_SCHEDULE")))),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "REJECTED")),
                new Document("$match", new Document("events.eventType", "FRAUD_SUSPECTED")),
                new Document("$unwind", "$events"),
                new Document("$match", new Document("events.eventType", "RECEIVED")),
                new Document("$group", new Document("_id", new BsonNull()).append("valorTransacoes",
                        new Document("$sum", new Document("$convert",
                                new Document("input", "$events.amount")
                                        .append("to", "double")))).append("qtdTransacoes",
                        new Document("$sum", 1L)))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();
            JSONObject trans03Json = new JSONObject(cursor.next());
            trans03Json.put("DetalhamentoTransacoes", 3);

            return trans03Json;
        } catch (NoSuchElementException e) {
            JSONObject trans03Json = new JSONObject();
            trans03Json.put("qtdTransacoes", 0);
            trans03Json.put("valorTransacoes", 0);
            trans03Json.put("DetalhamentoTransacoes", 3);

            return trans03Json;
        }
    }

    public Object getInformationOnTransactionType04() {
        openMongoConnection();
        collection = mongoDB.getCollection("Transactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.TWO, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.TWO, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId")
                        .append("events", new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("amount", "$amount")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("EXTERNAL_SEND", "EXTERNAL_SCHEDULE")))),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "REJECTED")),
                new Document("$match", new Document("events.eventType", "FRAUD_SUSPECTED")),
                new Document("$unwind", "$events"),
                new Document("$match", new Document("events.eventType", "RECEIVED")),
                new Document("$group", new Document("_id", new BsonNull()).append("valorTransacoes",
                        new Document("$sum", new Document("$convert",
                                new Document("input", "$events.amount")
                                        .append("to", "double")))).append("qtdTransacoes",
                        new Document("$sum", 1L)))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();
            JSONObject trans04Json = new JSONObject(cursor.next());
            trans04Json.put("DetalhamentoTransacoes", 4);

            return trans04Json;
        } catch (NoSuchElementException e) {
            JSONObject trans04Json = new JSONObject();
            trans04Json.put("qtdTransacoes", 0);
            trans04Json.put("valorTransacoes", 0);
            trans04Json.put("DetalhamentoTransacoes", 4);

            return trans04Json;
        }
    }

    public Object getInformationOnDevolution() {
        openMongoConnection();
        collection = mongoDB.getCollection("Transactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.TWO, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.TWO, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId")
                        .append("events", new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("amount", "$amount")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("INTERNAL_REFUND", "EXTERNAL_REFUND")))),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "COMPLETED")),
                new Document("$unwind", "$events"),
                new Document("$match", new Document("events.eventType", "RECEIVED")), new Document("$group",
                        new Document("_id", new BsonNull()).append("valorDevolucoes",
                                new Document("$sum", new Document("$convert",
                                        new Document("input", "$events.amount").append("to", "double"))))
                                .append("qtdDevolucoes", new Document("$sum", 1L)))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();
            JSONObject devolJson = new JSONObject(cursor.next());

            return devolJson;
        } catch (NoSuchElementException e) {
            JSONObject devolJson = new JSONObject();
            devolJson.put("qtdDevolucoes", 0);
            devolJson.put("valorDevolucoes", 0.0);

            return devolJson;
        }
    }

    public Object getInformationOnTransactionTime() {
        openMongoConnection();
        collection = mongoDB.getCollection("Transactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId").append("events",
                        new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("amount", "$amount")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("INTERNAL_SEND", "EXTERNAL_SEND")))),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", new Document("$in",
                        Arrays.asList("REJECTED", "COMPLETED")))),
                new Document("$project", new Document("percentileType",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$or", Arrays.asList(new Document("$eq",
                                                        Arrays.asList("$$events.transactionType", "INTERNAL_SEND")),
                                                new Document("$eq",
                                                        Arrays.asList("$$events.transactionType",
                                                                "EXTERNAL_SEND")))))), 0L)))
                        .append("startTime", new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq", Arrays.asList("$$events.eventType", "RECEIVED")))), 0L)))
                        .append("endTime", new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq",
                                                Arrays.asList("$$events.eventType", "CLIENT_NOTIFIED")))), 0L)))),
                new Document("$group", new Document("_id", "$percentileType.transactionType").append("times",
                        new Document("$addToSet", new Document("$subtract",
                                Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"), new Document("$sort", new Document("times", 1L)),
                new Document("$group", new Document("_id", "$_id")
                        .append("timesOrder", new Document("$push", "$times"))),
                new Document("$project", new Document("index50", new Document("$multiply", Arrays.asList(0.5d,
                        new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99", new Document("$multiply", Arrays.asList(0.99d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index50floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.5d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.99d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index50ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.5d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.99d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil50floor", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$floor", new Document("$multiply", Arrays.asList(0.5d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99floor", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$floor", new Document("$multiply", Arrays.asList(0.99d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil50ceil", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$ceil", new Document("$multiply", Arrays.asList(0.5d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$ceil", new Document("$multiply", Arrays.asList(0.99d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile50",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil50floor",
                                new Document("$multiply", Arrays.asList(new Document("$subtract",
                                                Arrays.asList("$percentil50ceil", "$percentil50floor")),
                                        new Document("$subtract",
                                                Arrays.asList("$index50", "$index50floor")))))), 1000L)))
                        .append("percentile99",
                                new Document("$divide", Arrays.asList(
                                        new Document("$sum", Arrays.asList("$percentil99floor",
                                                new Document("$multiply", Arrays.asList(new Document("$subtract",
                                                                Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                                        new Document("$subtract",
                                                                Arrays.asList("$index99",
                                                                        "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        JSONArray tranTimeJsonArray = new JSONArray();

        MongoCursor<Document> cursor = result.iterator();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            tranTimeJsonArray.put(document.toJson());
        }

        if (tranTimeJsonArray.length() < 2) {
            JSONObject tranTimeJsonInter = new JSONObject();
            JSONObject tranTimeJsonIntra = new JSONObject();
            tranTimeJsonInter.put("percentile50", 0.0);
            tranTimeJsonIntra.put("percentile50", 0.0);
            tranTimeJsonInter.put("percentile99", 0.0);
            tranTimeJsonIntra.put("percentile99", 0.0);
            tranTimeJsonArray.put(tranTimeJsonInter);
            tranTimeJsonArray.put(tranTimeJsonIntra);
        }

        return tranTimeJsonArray;
    }

    public String getDictTimeUserConsult() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictTransactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId").append("events",
                        new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("entryType", "$entryType")))),
                new Document("$match", new Document("events.transactionType", "SEARCH")),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate",
                                new Document("$gte",
                                        LocalDateTime.MIN.withYear(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                                .withMonth(Integer
                                                        .parseInt(dateTimeUtil
                                                                .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                        .append("$lt", LocalDateTime.MAX.withYear(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                                .withMonth(Integer
                                                        .parseInt(dateTimeUtil
                                                                .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "COMPLETED")),
                new Document("$project", new Document("startTime",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq", Arrays.asList("$$events.eventType", "RECEIVED")))), 0L)))
                        .append("endTime", new Document("$arrayElemAt",
                                Arrays.asList(new Document("$filter", new Document("input", "$events")
                                        .append("as", "events")
                                        .append("cond",
                                                new Document("$eq",
                                                        Arrays.asList("$$events.eventType",
                                                                "CLIENT_NOTIFIED")))), 0L)))),
                new Document("$group", new Document("_id", new BsonNull()).append("times", new Document("$addToSet",
                        new Document("$subtract",
                                Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"), new Document("$sort", new Document("times", 1L)),
                new Document("$group", new Document("_id", "$_id")
                        .append("timesOrder", new Document("$push", "$times"))),
                new Document("$project", new Document("index99", new Document("$multiply", Arrays.asList(0.99d,
                        new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.99d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.99d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil99floor", new Document("$arrayElemAt",
                                Arrays.asList("$timesOrder", new Document("$floor",
                                        new Document("$multiply", Arrays.asList(0.99d,
                                                new Document("$subtract",
                                                        Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt",
                                Arrays.asList("$timesOrder", new Document("$ceil",
                                        new Document("$multiply", Arrays.asList(0.99d,
                                                new Document("$subtract",
                                                        Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile99",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil99floor",
                                new Document("$multiply", Arrays.asList(
                                        new Document("$subtract",
                                                Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                        new Document("$subtract",
                                                Arrays.asList("$index99", "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.00";
        }
    }

    public String getDictTimeSendRegister() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictTransactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId")
                        .append("events", new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("entryType", "$entryType")))),
                new Document("$match", new Document("events.transactionType", "REGISTER")),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))
                        .append("events.entryType", new Document("$in", Arrays.asList("PHONE", "EMAIL")))),
                new Document("$match", new Document("events.eventType",
                        new Document("$in", Arrays.asList("COMPLETED", "OWNERSHIP_REQUESTED")))),
                new Document("$project", new Document("startTime",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events")
                                        .append("as", "events")
                                        .append("cond",
                                                new Document("$eq",
                                                        Arrays.asList("$$events.eventType", "RECEIVED")))), 0L)))
                        .append("endTime",
                                new Document("$arrayElemAt", Arrays.asList(
                                        new Document("$filter", new Document("input", "$events")
                                                .append("as", "events")
                                                .append("cond",
                                                        new Document("$eq",
                                                                Arrays.asList("$$events.eventType",
                                                                        "OWNERSHIP_REQUESTED")))), 0L)))),
                new Document("$group", new Document("_id", new BsonNull())
                        .append("times", new Document("$addToSet",
                                new Document("$subtract",
                                        Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"),
                new Document("$sort", new Document("times", 1L)), new Document("$group",
                        new Document("_id", "$_id").append("timesOrder", new Document("$push", "$times"))),
                new Document("$project", new Document("index99",
                        new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99floor", new Document("$floor",
                                new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil",
                                new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil99floor", new Document("$arrayElemAt",
                                Arrays.asList("$timesOrder", new Document("$floor",
                                        new Document("$multiply", Arrays.asList(0.95d,
                                                new Document("$subtract", Arrays.asList(
                                                        new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt",
                                Arrays.asList("$timesOrder", new Document("$ceil",
                                        new Document("$multiply", Arrays.asList(0.95d,
                                                new Document("$subtract",
                                                        Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile99",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil99floor",
                                new Document("$multiply", Arrays.asList(
                                        new Document("$subtract",
                                                Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                        new Document("$subtract",
                                                Arrays.asList("$index99", "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.00";
        }
    }

    public String getDictTimeUserRegister() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictTransactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId").append("events",
                        new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("entryType", "$entryType")))),
                new Document("$match", new Document("events.transactionType", "REGISTER")),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType",
                        new Document("$in", Arrays.asList("COMPLETED", "OWNERSHIP_REQUESTED")))),
                new Document("$match", new Document("events.eventType",
                        new Document("$in", Arrays.asList("COMPLETED", "OWNERSHIP_REQUESTED", "CRK_NOTIFIED")))),
                new Document("$project", new Document("startTime",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq",
                                                Arrays.asList("$$events.eventType", "CRK_NOTIFIED")))), 0L)))
                        .append("endTime", new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq",
                                                Arrays.asList("$$events.eventType", "CLIENT_NOTIFIED")))), 0L)))),
                new Document("$group", new Document("_id", new BsonNull()).append("times", new Document("$addToSet",
                        new Document("$subtract",
                                Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"), new Document("$sort", new Document("times", 1L)),
                new Document("$group", new Document("_id", "$_id")
                        .append("timesOrder", new Document("$push", "$times"))),
                new Document("$project", new Document("index99", new Document("$multiply", Arrays.asList(0.95d,
                        new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil99floor", new Document("$arrayElemAt",
                                Arrays.asList("$timesOrder", new Document("$floor",
                                        new Document("$multiply", Arrays.asList(0.95d,
                                                new Document("$subtract",
                                                        Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt",
                                Arrays.asList("$timesOrder", new Document("$ceil",
                                        new Document("$multiply", Arrays.asList(0.95d,
                                                new Document("$subtract", Arrays.asList(
                                                        new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile99",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil99floor",
                                new Document("$multiply", Arrays.asList(
                                        new Document("$subtract",
                                                Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                        new Document("$subtract",
                                                Arrays.asList("$index99", "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.00";
        }
    }

    public String getDictTimeUserExclusion() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictTransactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId").append("events",
                        new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("entryType", "$entryType")))),
                new Document("$match", new Document("events.transactionType", "EXCLUSION")),
                new Document("$match", new Document("events.eventType", "CLIENT_NOTIFIED")
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$match", new Document("events.eventType", "COMPLETED")),
                new Document("$project", new Document("startTime",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events")
                                        .append("as", "events")
                                        .append("cond",
                                                new Document("$eq",
                                                        Arrays.asList("$$events.eventType", "RECEIVED")))), 0L)))
                        .append("endTime", new Document("$arrayElemAt",
                                Arrays.asList(new Document("$filter", new Document("input", "$events")
                                        .append("as", "events")
                                        .append("cond",
                                                new Document("$eq",
                                                        Arrays.asList("$$events.eventType",
                                                                "CLIENT_NOTIFIED")))), 0L)))),
                new Document("$group", new Document("_id", new BsonNull()).append("times", new Document("$addToSet",
                        new Document("$subtract",
                                Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"),
                new Document("$sort", new Document("times", 1L)), new Document("$group",
                        new Document("_id", "$_id").append("timesOrder", new Document("$push", "$times"))),
                new Document("$project", new Document("index99", new Document("$multiply", Arrays.asList(0.95d,
                        new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil99floor", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile99",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil99floor",
                                new Document("$multiply", Arrays.asList(
                                        new Document("$subtract",
                                                Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                        new Document("$subtract",
                                                Arrays.asList("$index99", "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.00";
        }
    }

    public String getDictTimePortabilityNotification() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictTransactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionId").append("events",
                        new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("entryType", "$entryType")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("PORTABILITY_CLAIM", "OWNERSHIP_CLAIM")))),
                new Document("$match", new Document("events.eventType",
                        new Document("$in", Arrays.asList("CONFIRMED", "CANCELLED")))
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$project", new Document("startTime",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq",
                                                Arrays.asList("$$events.eventType", "ACKNOWLEDGED")))), 0L)))
                        .append("endTime", new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq",
                                                Arrays.asList("$$events.eventType", "CLIENT_NOTIFIED")))), 0L)))),
                new Document("$group", new Document("_id", new BsonNull()).append("times",
                        new Document("$addToSet", new Document("$subtract",
                                Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"), new Document("$sort", new Document("times", 1L)),
                new Document("$group", new Document("_id", "$_id")
                        .append("timesOrder", new Document("$push", "$times"))),
                new Document("$project", new Document("index99", new Document("$multiply", Arrays.asList(0.95d,
                        new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil99floor", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract", Arrays.asList(
                                                new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile99",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil99floor",
                                new Document("$multiply", Arrays.asList(
                                        new Document("$subtract",
                                                Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                        new Document("$subtract",
                                                Arrays.asList("$index99", "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.00";
        }
    }

    public String getDictTimePortabilitySend() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictTransactions");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group", new Document("_id", "$transactionExternalId").append("events",
                        new Document("$addToSet", new Document("eventType", "$eventType")
                                .append("transactionDate", "$transactionDate")
                                .append("transactionType", "$transactionType")
                                .append("entryType", "$entryType")))),
                new Document("$match", new Document("events.transactionType",
                        new Document("$in", Arrays.asList("PORTABILITY_CLAIM", "OWNERSHIP_CLAIM")))),
                new Document("$match", new Document("events.eventType",
                        new Document("$in", Arrays.asList("CONFIRMED", "CANCELLED")))
                        .append("events.transactionDate", new Document("$gte",
                                LocalDateTime.MIN.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                .append("$lt", LocalDateTime.MAX.withYear(Integer
                                        .parseInt(dateTimeUtil.addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                        .withMonth(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$project", new Document("startTime",
                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$eq", Arrays.asList("$$events.eventType", "RECEIVED")))), 0L)))
                        .append("endTime", new Document("$arrayElemAt", Arrays.asList(new Document("$filter",
                                new Document("input", "$events").append("as", "events").append("cond",
                                        new Document("$or", Arrays.asList(
                                                new Document("$eq", Arrays.asList("$$events.eventType", "CANCELLED")),
                                                new Document("$eq",
                                                        Arrays.asList("$$events.eventType", "CONFIRMED")))))), 0L)))),
                new Document("$group", new Document("_id", new BsonNull()).append("times", new Document("$addToSet",
                        new Document("$subtract",
                                Arrays.asList("$endTime.transactionDate", "$startTime.transactionDate"))))),
                new Document("$unwind", "$times"), new Document("$sort", new Document("times", 1L)),
                new Document("$group", new Document("_id", "$_id").append("timesOrder",
                        new Document("$push", "$times"))),
                new Document("$project", new Document("index99", new Document("$multiply", Arrays.asList(0.95d,
                        new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L)))))
                        .append("index99floor", new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("index99ceil", new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                new Document("$subtract", Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))
                        .append("percentil99floor", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$floor", new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract",
                                                Arrays.asList(new Document("$size", "$timesOrder"), 1L))))))))
                        .append("percentil99ceil", new Document("$arrayElemAt", Arrays.asList("$timesOrder",
                                new Document("$ceil", new Document("$multiply", Arrays.asList(0.95d,
                                        new Document("$subtract", Arrays.asList(
                                                new Document("$size", "$timesOrder"), 1L))))))))),
                new Document("$project", new Document("percentile99",
                        new Document("$divide", Arrays.asList(new Document("$sum", Arrays.asList("$percentil99floor",
                                new Document("$multiply",
                                        Arrays.asList(new Document("$subtract",
                                                        Arrays.asList("$percentil99ceil", "$percentil99floor")),
                                                new Document("$subtract",
                                                        Arrays.asList("$index99", "$index99floor")))))), 1000L))))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.00";
        }
    }

    public String getQueryDict() {
        openMongoConnection();
        collection = mongoDB.getCollection("DictInternalKeySearches");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("referenceDate",
                                new Document("$gte",
                                        LocalDateTime.MIN.withYear(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                                .withMonth(Integer
                                                        .parseInt(dateTimeUtil
                                                                .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                        .append("$lt", LocalDateTime.MAX.withYear(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                                .withMonth(Integer
                                                        .parseInt(dateTimeUtil
                                                                .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$group",
                        new Document("_id",
                                new BsonNull())
                                .append("sumSearches",
                                        new Document("$sum", "$internalKeySearchesCount")))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.0";
        }
    }

    public String getIndexAvailability() {
        openMongoConnection();
        collection = mongoDB.getCollection("AvailabilityIncidents");
        DateTimeUtil dateTimeUtil = new DateTimeUtil();

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("referenceDate",
                                new Document("$gte",
                                        LocalDateTime.MIN.withYear(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                                .withMonth(Integer
                                                        .parseInt(dateTimeUtil
                                                                .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM"))))
                                        .append("$lt", LocalDateTime.MAX.withYear(Integer
                                                .parseInt(dateTimeUtil
                                                        .addOrSubtractMonthsInADate(-MathUtil.ONE, "YYYY")))
                                                .withMonth(Integer
                                                        .parseInt(dateTimeUtil
                                                                .addOrSubtractMonthsInADate(-MathUtil.ONE, "MM")))))),
                new Document("$project",
                        new Document("downtime",
                                new Document("$subtract", Arrays.asList("$endTime", "$startTime")))),
                new Document("$group",
                        new Document("_id",
                                new BsonNull())
                                .append("sumDowntime",
                                        new Document("$sum", "$downtime")))))
                .allowDiskUse(true);

        try {
            MongoCursor<Document> cursor = result.iterator();

            return cursor.next().toString();
        } catch (NoSuchElementException e) {
            return "0.0";
        }
    }
}