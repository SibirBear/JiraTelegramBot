package info.fermercentr.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import info.fermercentr.config.Config;

import java.io.File;
import java.io.IOException;

class DeleteTempFilesTest {

    @BeforeAll
    static void init() throws IOException {
        Config.init();
        File file = new File(Config.getPathToExchange() + "test.tst");
        file.delete();
        file.createNewFile();
    }

    @Test
    void deleteTempFilesTest() {
        DeleteTempFiles d = new DeleteTempFiles();
        for (String p : d.getListOfTempFiles()) {
            Assertions.assertTrue(d.deleteFile(p));
        }

    }
}