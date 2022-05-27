package info.fermercentr.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import info.fermercentr.jiraAPI.issue.Issue;
import info.fermercentr.store.StoreOrders;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckOrdersTimeExpiredTest {

    private static final StoreOrders storeOrders = new StoreOrders();
    private static final StoreOrders expected = new StoreOrders();
    private static CheckOrdersTimeExpired checkAll;

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {

        //Наполнение тестируемых данных, используя рефлексию для доступа к закрытым полям
        Issue issue1 = new Issue("Project", "Reporter");
        storeOrders.save(1, issue1);

        Issue issue2 = new Issue("Project", "Reporter");
        Field dateTime2 = issue2.getClass().getDeclaredField("creationTimeIssue");
        dateTime2.setAccessible(true);
        dateTime2.set(issue2, LocalDateTime.now().minusHours(13));
        storeOrders.save(2, issue2);

        Issue issue3 = new Issue("Project", "Reporter");
        Field dateTime3 = issue3.getClass().getDeclaredField("creationTimeIssue");
        dateTime3.setAccessible(true);
        dateTime3.set(issue3, LocalDateTime.now().minusHours(6));
        storeOrders.save(3, issue3);

        Issue issue4 = new Issue("Project", "Reporter");
        Field dateTime4 = issue4.getClass().getDeclaredField("creationTimeIssue");
        dateTime4.setAccessible(true);
        dateTime4.set(issue4, LocalDateTime.now().minusHours(24));
        storeOrders.save(4, issue4);

        Issue issue5 = new Issue("Project", "Reporter");
        Field dateTime5 = issue5.getClass().getDeclaredField("creationTimeIssue");
        dateTime5.setAccessible(true);
        dateTime5.set(issue5, LocalDateTime.now().plusHours(24));
        storeOrders.save(5, issue5);

        //Наполнение ожидаемых данных, используя рефлексию для доступа к закрытым полям
        Issue issueEx1 = new Issue("Project", "Reporter");
        expected.save(1, issueEx1);

        Issue issueEx3 = new Issue("Project", "Reporter");
        expected.save(3, issueEx3);

        Issue issueEx5 = new Issue("Project", "Reporter");
        expected.save(5, issueEx5);

        checkAll = new CheckOrdersTimeExpired(storeOrders);
        checkAll.execute();

    }

    @Test
    void TestSize() {
        assertEquals(expected.getAll().size(), storeOrders.getAll().size(), "Error with size");
    }

    @Test
    void TestMapContent() {
        assertEquals(expected.getAll().keySet(), storeOrders.getAll().keySet(), "Error with content in map");
    }

}