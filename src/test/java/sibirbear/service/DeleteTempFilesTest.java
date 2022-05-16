package sibirbear.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sibirbear.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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