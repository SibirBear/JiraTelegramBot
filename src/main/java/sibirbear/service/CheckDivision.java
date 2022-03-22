package sibirbear.service;

import sibirbear.dao.ConnectOracleDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CheckDivision {

    private static final String SQL_QUERY = "select "
            + "store.store, store.store_name from rmsprd.store "
            + "join rmsprd.wf_customer on store.wf_customer_id=wf_customer.wf_customer_id "
            + "where wf_customer.wf_customer_group_id=2 "
            + "and store.store_close_date is null";

    // TODO
    //  1. По сути не нужно записывать весь ответ на запрос и потом проверять есть ли там нужный ключ или нет
    //  Можно сразу проверять наличие ключа в запросе если проверять в цикле while
    //  2. Или при инициализации приложения делать запрос и сохранять весь результат в Мапу, и проверять раз в сутки
    //  или даже раз в 3е суток, чаще нет смысла, данные после создания раньше не понадобятся

    public boolean isDivisionReal(String division) {
        return getMapOfDivisionsFromDB().containsKey(division);
    }

    private Map<String, String> getMapOfDivisionsFromDB() {
        ConnectOracleDB db = new ConnectOracleDB();

        Map<String, String> mapResponse = new HashMap<>();

        try (Connection con = db.initDB().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SQL_QUERY)) {
            while (rs.next()) {
                mapResponse.put(rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapResponse;

    }

}
