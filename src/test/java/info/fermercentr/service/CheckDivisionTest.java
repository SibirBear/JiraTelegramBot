package info.fermercentr.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import info.fermercentr.config.Config;


class CheckDivisionTest {

    final String divForTest = "5190";

    @BeforeAll
    static void initConfig() {
        Config.init();
    }

    @Test
    void isDivisionRealTest() {

        CheckDivision.setDivisionsFromDB();
        boolean div = CheckDivision.isDivisionReal(divForTest);

        Assertions.assertTrue(div);

    }
}