package sibirbear.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sibirbear.config.Config;

import javax.validation.constraints.AssertTrue;

import static org.junit.jupiter.api.Assertions.*;

class CheckDivisionTest {

    final String divForTest = "5190";

    @BeforeAll
    static void initConfig() {
        Config.init();
    }


    @Test
    void isDivisionRealTest() {
        CheckDivision checkDivision = new CheckDivision();
        boolean div = checkDivision.isDivisionReal(divForTest);

        Assertions.assertTrue(div);

    }
}