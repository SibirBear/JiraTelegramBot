package sibirbear.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sibirbear.config.Config;
import sibirbear.service.CheckAllUsersAuth;
import sibirbear.service.CheckOrdersTimeExpired;
import sibirbear.service.DeleteTempFilesOrders;
import sibirbear.store.StoreOrders;
import sibirbear.store.StoreUsers;

public class InitTasks {

    private static final Logger log = LogManager.getLogger(InitTasks.class);

    // TODO: сделать класс для удаления мусора (папка TempImage, User, Order) который через определенный
    //  интервал удаляет не актуальные данные или при завершении программы. Не актуальные данные:
    //  1. CHECKORDERSTIMEEXPIRED Данные по заявке не используются более 12 часов - удаляем данные
    //  2. CHECKALLUSERSAUTH Юзер авторизован более установленного времени - удаление юзера, его данных по заявке
    //  3. DELETETEMPFILESFROMORDERS Удаляем файлы в TempImage если на них нет ссылки в Orders

    public InitTasks() {
    }

    public void start(final StoreUsers storeUsers, final StoreOrders storeOrders) {
        log.info("[" + Config.APP_NAME + "] - Start scheduled tasks.");

        taskCheckOrdersTimeExpired(storeOrders);
        taskCheckAllUsersAuth(storeUsers);
        taskDeleteTempFilesFromOrders(storeOrders);

        log.info("[" + Config.APP_NAME + "] - End scheduled tasks.");

    }

    private void taskCheckOrdersTimeExpired(final StoreOrders storeOrders) {
        log.info("[" + Config.APP_NAME + "] - Scheduled task: CheckOrdersTimeExpired");
        CheckOrdersTimeExpired checkOrdersTimeExpired
                = new CheckOrdersTimeExpired(storeOrders);
        checkOrdersTimeExpired.execute();
    }

    private void taskCheckAllUsersAuth(final StoreUsers storeUsers) {
        log.info("[" + Config.APP_NAME + "] - Scheduled task: CheckAllUsersAuth");
        CheckAllUsersAuth checkAllUsersAuth
                = new CheckAllUsersAuth(storeUsers);
        checkAllUsersAuth.execute();
    }

    private void taskDeleteTempFilesFromOrders(final StoreOrders storeOrders) {
        log.info("[" + Config.APP_NAME + "] - Scheduled task: DeleteTempFilesFromOrders");
        DeleteTempFilesOrders deleteTempFilesOrders
                = new DeleteTempFilesOrders(storeOrders);
        deleteTempFilesOrders.execute();
    }

}
