package info.fermercentr.service;

import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import info.fermercentr.jiraAPI.issue.Issue;
import info.fermercentr.store.StoreOrders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import info.fermercentr.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeleteTempFilesFromOrdersTest {

    static StoreOrders storeOrders = new StoreOrders();

    @BeforeAll
    static void init() throws IOException, JiraApiException {
        Config.init();

        Issue issue1 = new Issue("Project", "Reporter");
        Issue issue2 = new Issue("Project", "Reporter");

        File file1 = new File(Config.getPathToExchange() + "test1.tst");
        File file2 = new File(Config.getPathToExchange() + "test2.tst");
        File file3 = new File(Config.getPathToExchange() + "test3.tst");

        file1.delete();
        file2.delete();
        file3.delete();

        file1.createNewFile();
        file2.createNewFile();
        file3.createNewFile();

        issue1.addAttachmentFile(Config.getPathToExchange() + "test1.tst");

        storeOrders.save(1, issue1);
        storeOrders.save(2, issue2);


    }

    @Test
    void deleteTempFilesOrdersTest() throws IOException {
        DeleteTempFilesOrders df = new DeleteTempFilesOrders(storeOrders);
        df.execute();

        Assertions.assertEquals(
                (int) Files.walk(Paths.get(Config.getPathToExchange()))
                        .filter(Files::isRegularFile).count(),
                df.getSavedListFiles().size());

    }

    @AfterAll
    static void end() {
        File[] files = new File(Config.getPathToExchange()).listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }


}
