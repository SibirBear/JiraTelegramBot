package sibirbear.service;

import sibirbear.dao.ConnectOracleDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CheckDivision {

    private static final Map<String, String> storeDivision = new HashMap<>();
    private static final String SQL_QUERY = "select "
            + "store.store, store.store_name from rmsprd.store "
            + "join rmsprd.wf_customer on store.wf_customer_id=wf_customer.wf_customer_id "
            + "where wf_customer.wf_customer_group_id=2 "
            + "and store.store_close_date is null";

    public static boolean isDivisionReal(String division) {
        return storeDivision.containsKey(division);
    }

    public static void setDivisionsFromDB() {
        ConnectOracleDB db = new ConnectOracleDB();

        try (Connection con = db.initDB().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SQL_QUERY)) {
            while (rs.next()) {
                storeDivision.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
