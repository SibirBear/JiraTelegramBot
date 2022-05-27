package info.fermercentr.service;

import info.fermercentr.model.User;
import info.fermercentr.store.StoreUsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import info.fermercentr.config.Config;
import info.fermercentr.core.CoreConstants;
import info.fermercentr.model.Steps;

import java.lang.reflect.Field;
import java.time.LocalDate;


class CheckAllUsersAuthTest {

    private static final StoreUsers storeUsers = new StoreUsers();
    private static final StoreUsers expected = new StoreUsers();
    private static CheckAllUsersAuth checkAll;

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {
        Config.init();
        final long daysToExpired = Config.getDaysForReAuth() * 10L;

        //Filling in the test data using reflection to access closed fields
        User user1 = new User("1", Steps.STEP101);
        Field date1 = user1.getClass().getDeclaredField("expiredDate");
        date1.setAccessible(true);
        date1.set(user1, LocalDate.now().minusDays(daysToExpired));
        storeUsers.save(1, user1);

        User user2 = new User("2", Steps.STEP102);
        Field date2 = user2.getClass().getDeclaredField("expiredDate");
        date2.setAccessible(true);
        date2.set(user2, LocalDate.now().minusDays(daysToExpired));
        storeUsers.save(2, user2);

        User user3 = new User("3", Steps.STEP101);
        Field date3 = user3.getClass().getDeclaredField("expiredDate");
        date3.setAccessible(true);
        date3.set(user3, LocalDate.now().plusDays(daysToExpired));
        storeUsers.save(3, user3);

        User user4 = new User(CoreConstants.NOT_AUTH, Steps.STEP101);
        Field date4 = user4.getClass().getDeclaredField("date");
        date4.setAccessible(true);
        date4.set(user4, LocalDate.now().minusDays(2));
        storeUsers.save(4, user4);

        User user5 = new User(CoreConstants.NOT_AUTH, Steps.STEP101);
        storeUsers.save(5, user5);

        User user6 = new User(CoreConstants.NOT_AUTH, Steps.STEP102);
        storeUsers.save(6, user6);

        User user7 = new User(CoreConstants.NOT_AUTH, Steps.STEP101);
        Field date7 = user7.getClass().getDeclaredField("date");
        date7.setAccessible(true);
        date7.set(user7, LocalDate.now().plusDays(2));
        storeUsers.save(7, user7);

        User user8 = new User(CoreConstants.NOT_AUTH, Steps.STEP101);
        Field date8 = user8.getClass().getDeclaredField("date");
        date8.setAccessible(true);
        date8.set(user8, LocalDate.now().minusDays(1));
        Field expiredDate8 = user8.getClass().getDeclaredField("expiredDate");
        expiredDate8.setAccessible(true);
        expiredDate8.set(user8, LocalDate.now().plusDays(daysToExpired));
        storeUsers.save(8, user8);

        User user9 = new User(CoreConstants.NOT_AUTH, Steps.STEP102);
        Field date9 = user9.getClass().getDeclaredField("date");
        date9.setAccessible(true);
        date9.set(user9, LocalDate.now().minusDays(1));
        Field expiredDate9 = user9.getClass().getDeclaredField("expiredDate");
        expiredDate9.setAccessible(true);
        expiredDate9.set(user9, LocalDate.now().minusDays(daysToExpired));
        storeUsers.save(9, user9);

        //Filling in expected data using reflection to access closed fields
        User userEx2 = new User("2", Steps.STEP102);
        expected.save(2, userEx2);

        User userEx3 = new User("3", Steps.STEP101);
        expected.save(3, userEx3);

        User userEx5 = new User("5", Steps.STEP101);
        expected.save(5, userEx5);

        User userEx6 = new User(CoreConstants.NOT_AUTH, Steps.STEP102);
        expected.save(6, userEx6);

        User userEx7 = new User(CoreConstants.NOT_AUTH, Steps.STEP101);
        expected.save(7, userEx7);

        checkAll = new CheckAllUsersAuth(storeUsers);
        checkAll.execute();
    }

    @Test
    void TestSize() {
        Assertions.assertEquals(expected.getAll().size(), storeUsers.getAll().size(), "Error with size");
    }

    @Test
    void TestMapContent() {
        Assertions.assertEquals(expected.getAll().keySet(), storeUsers.getAll().keySet(), "Error with content in map");
        //Assertions.assertTrue(storeUsers.getAll().entrySet().stream().allMatch(e -> expected.getAll().containsKey(e.getKey())), "Stream");
    }

}