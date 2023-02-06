package org.mblas.junitapp.example.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testNameAccount() {
        Account account = new Account("Miguel", new BigDecimal("1000.12345"));
        String expected = "Miguel";
        String actual = account.getPerson();
        assertEquals(expected, actual);
        assertTrue(actual.equals("Miguel"));
    }
}