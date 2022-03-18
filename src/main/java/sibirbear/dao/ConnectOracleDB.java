package sibirbear.dao;

import oracle.jdbc.pool.OracleDataSource;
import sibirbear.config.Config;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ConnectOracleDB implements IConnectDB {

    private final String url = Config.getConfigOracleDB().getUrl();
    private final String user = Config.getConfigOracleDB().getUser();
    private final String pass = Config.getConfigOracleDB().getPass();
    private OracleDataSource source = null;

    @Override
    public DataSource initDB() throws SQLException {
        source = new OracleDataSource();
        source.setURL(url);
        source.setUser(user);
        source.setPassword(pass);

        return source;

    }

}
