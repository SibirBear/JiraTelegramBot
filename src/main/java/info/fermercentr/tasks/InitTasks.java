package info.fermercentr.tasks;

import info.fermercentr.service.CheckAllUsersAuth;
import info.fermercentr.service.CheckDivision;
import info.fermercentr.store.StoreOrders;
import info.fermercentr.store.StoreUsers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import info.fermercentr.config.Config;
import info.fermercentr.service.CheckOrdersTimeExpired;
import info.fermercentr.service.DeleteTempFilesOrders;

import static info.fermercentr.App.BUILD_VERSION;

public class InitTasks {

    private static final Logger LOG = LogManager.getLogger(InitTasks.class);

    // A class for garbage disposal (TempImage, User, Order folder) that
    // deletes irrelevant data after a certain interval or at the end of
    // the program & received actual list of division for use. Not up-to-date data:
    // 1. SETDIVISIONS request to DB & received list of actual divisions for use
    // 2. CHECKORDERSTIMEEXPIRED Data on the application is not used for more than 12 hours - we delete the data
    // 3. CHECKALLUSERSAUTH The user is authorized for more than the set time - deleting the user, his data on the application
    // 4. DELETETEMPFILESFROMORDERS Delete files in TempImage if there is no link to them in Orders

    public InitTasks() {
    }

    public void start(final StoreUsers storeUsers, final StoreOrders storeOrders) {
        LOG.info("[" + Config.APP_NAME + "] ******");
        LOG.info("[" + Config.APP_NAME + "] - Start scheduled tasks. Building version: " + BUILD_VERSION);

        taskSetDivisions();
        taskCheckOrdersTimeExpired(storeOrders);
        taskCheckAllUsersAuth(storeUsers);
        taskDeleteTempFilesFromOrders(storeOrders);

        LOG.info("[" + Config.APP_NAME + "] - End scheduled tasks.");
        LOG.info("[" + Config.APP_NAME + "] ******");

    }

    private void taskSetDivisions() {
        LOG.info("[" + Config.APP_NAME + "] - ##Scheduled task: SetDivisions - update list of divisions.");
        CheckDivision.setDivisionsFromDB();
    }

    private void taskCheckOrdersTimeExpired(final StoreOrders storeOrders) {
        LOG.info("[" + Config.APP_NAME + "] - ##Scheduled task: CheckOrdersTimeExpired.");
        CheckOrdersTimeExpired checkOrdersTimeExpired
                = new CheckOrdersTimeExpired(storeOrders);
        checkOrdersTimeExpired.execute();
    }

    private void taskCheckAllUsersAuth(final StoreUsers storeUsers) {
        LOG.info("[" + Config.APP_NAME + "] - ##Scheduled task: CheckAllUsersAuth");
        CheckAllUsersAuth checkAllUsersAuth
                = new CheckAllUsersAuth(storeUsers);
        checkAllUsersAuth.execute();
    }

    private void taskDeleteTempFilesFromOrders(final StoreOrders storeOrders) {
        LOG.info("[" + Config.APP_NAME + "] - ##Scheduled task: DeleteTempFilesFromOrders");
        DeleteTempFilesOrders deleteTempFilesOrders
                = new DeleteTempFilesOrders(storeOrders);
        deleteTempFilesOrders.execute();
    }

}
