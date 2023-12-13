package base.db;

import base.util.PropertiesUtil;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.time.LocalDate;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class OrbiCcsWorkerDb extends ConnectionMongoDb {

    private PropertiesUtil propertiesUtil;

    public OrbiCcsWorkerDb() throws IOException {
        propertiesUtil = new PropertiesUtil();
        db = "orbi-ccs-worker";
        connectionString = "mongodb://" + propertiesUtil.getPropertyByName("orbi.db.ccs.worker.user")
                + ":" + propertiesUtil.getPropertyByNameBase64("orbi.db.ccs.worker.password")
                + "@" + propertiesUtil.getPropertyByName("orbi.db.ccs.worker.baseURI")
                + ":" + propertiesUtil.getPropertyByName("orbi.db.ccs.worker.port")
                + "/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false";
        openMongoConnection();
    }

    public void updateLastExecutionDate(LocalDate lastCompletedExecution) {
        Bson filter = eq(
                "_class", "br.com.realizecfi.orbiccsworker.repository.model.CcsParameterModel");
        Bson updateOperation = set("lastCompletedExecution", lastCompletedExecution);
        mongoDB.getCollection("parameters").updateOne(filter, updateOperation);
    }
}