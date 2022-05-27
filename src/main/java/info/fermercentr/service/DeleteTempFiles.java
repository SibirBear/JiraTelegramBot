package info.fermercentr.service;

import info.fermercentr.config.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeleteTempFiles {

    public List<String> getListOfTempFiles() {
        List<String> result = new ArrayList<>();
        File p = new File(Config.getPathToExchange());
        for (File file : Objects.requireNonNull(p.listFiles())) {
            if (file.isFile()) {
                result.add(file.toString().replace("\\", "/"));
            }
        }

        return result;

    }

    public boolean deleteFile(String file) {
        return new File(file).delete();
    }


}
