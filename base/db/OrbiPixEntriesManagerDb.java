package base.db;

import base.util.PropertiesUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrbiPixEntriesManagerDb extends ConnectionMySqlDb {

    String queryFindToken = "select token from confirmation_token\n"
            + "where 1=1\n"
            + "and account_id = '%s'\n"
            + "and key_type = '%s'\n"
            + "and confirmation_status = '%s'\n";

    private PropertiesUtil propertiesUtil;

    public OrbiPixEntriesManagerDb() throws IOException {
        propertiesUtil = new PropertiesUtil();

        user = propertiesUtil.getPropertyByName("orbi.db.pix.entries.manager.service.user");
        password = propertiesUtil.getPropertyByName("orbi.db.pix.entries.manager.service.password");

        connectionString = "jdbc:mysql://" + propertiesUtil.getPropertyByName(
                "orbi.db.pix.entries.manager.service.server")
                + ":" + propertiesUtil.getPropertyByName("orbi.db.pix.entries.manager.service.port")
                + "/" + propertiesUtil.getPropertyByName("orbi.db.pix.entries.manager.service.db");

    }

    public Integer getToken(String accountId, String keyType, String confirmationStatus) throws InterruptedException {
        Integer token = null;
        openMySqlConnection();

        try {
            String formattedQuery = String.format(queryFindToken, accountId, keyType, confirmationStatus);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(formattedQuery);

            rs.next();
            token = rs.getInt("token");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeMySqlConnection();
        }
        return token;
    }
}