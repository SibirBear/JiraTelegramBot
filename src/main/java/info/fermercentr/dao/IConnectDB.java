package info.fermercentr.dao;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface IConnectDB {

    /**
     * Метод для инициализации параметров подключения к базе данных.
     *
     * @return Возвращает тип DataSource
     */
    DataSource initDB() throws SQLException;

}
