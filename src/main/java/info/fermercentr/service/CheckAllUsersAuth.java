package info.fermercentr.service;

import info.fermercentr.store.StoreUsers;

import java.util.ArrayList;
import java.util.List;

//Класс для проверки и удаления записей User, если соответствуют условиям для удаления
public class CheckAllUsersAuth {

    private final StoreUsers storeUsers;

    public CheckAllUsersAuth(final StoreUsers storeUsers) {
        this.storeUsers = storeUsers;
    }

    public void execute() {
        List<Long> ext = new ArrayList<>();
        storeUsers.getAll().forEach((k, v) -> {
            CheckUserAuth checkUserAuth = new CheckUserAuth(storeUsers.get(k));
            if(checkUserAuth.checkForIllegal())
                ext.add(k);
        });

        for(Long key : ext) {
            storeUsers.delete(key);
        }

    }


}
