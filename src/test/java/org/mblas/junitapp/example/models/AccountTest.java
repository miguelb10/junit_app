package org.mblas.junitapp.example.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mblas.junitapp.example.exceptions.InsufficientMoneyException;

import java.math.BigDecimal;

class AccountTest {

    @Test
    @DisplayName("Test account name")
    void testNameAccount() {
        Account account = new Account("Miguel", new BigDecimal("1000.12345"));
        String expected = "Miguel";
        String actual = account.getPerson();
        assertEquals(expected, actual);
        assertTrue(actual.equals("Miguel"));
    }

    @Test
    @DisplayName("Test account balance")
    void testBalanceAccount() {
        Account account = new Account("Miguel", new BigDecimal("1000.12345"));
        assertNotNull(account.getBalance());
        assertEquals(1000.12345, account.getBalance().doubleValue());
        assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Test ref account")
    void testRefAccount() {
        Account account = new Account("Miguel Blas", new BigDecimal("8900.9997"));
        Account account2 = new Account("Miguel Blas", new BigDecimal("8900.9997"));

        //assertNotEquals(account2, account);
        assertEquals(account2, account);
    }

    @Test
    @DisplayName("Test debit account")
    void testDebitAccount() {
        Account account = new Account("Miguel", new BigDecimal("1000.12345"));
        account.debit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(900, account.getBalance().intValue());
        assertEquals("900.12345", account.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Test credit account")
    void testCreditAccount() {
        Account account = new Account("Miguel", new BigDecimal("1000.12345"));
        account.credit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(1100, account.getBalance().intValue());
        assertEquals("1100.12345", account.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Test insufficient money")
    void insufficientMoneyExceptionAccount() {
        Account account = new Account("Miguel", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String expected = "Insufficient Money";
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test transfer money")
    void testTransferMoneyAccounts() {
        Account account1 = new Account("Jhon Doe", new BigDecimal("2500"));
        Account account2 = new Account("Miguel", new BigDecimal("1500.8989"));

        Bank bank = new Bank();
        bank.setName("IBK");
        ;
        bank.transfer(account2, account1, new BigDecimal(500));
        assertEquals("1000.8989", account2.getBalance().toPlainString());
        assertEquals("3000", account1.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Test relation bank acounts")
    void testRelationBankAccounts() {
        Account account1 = new Account("Jhon Doe", new BigDecimal("2500"));
        Account account2 = new Account("Miguel", new BigDecimal("1500.8989"));

        Bank bank = new Bank();
        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.setName("IBK");

        bank.transfer(account2, account1, new BigDecimal(500));
        assertAll(
                () -> assertEquals("1000.8989", account2.getBalance().toPlainString()),
                () -> assertEquals("3000", account1.getBalance().toPlainString()),
                () -> assertEquals(2, bank.getAccounts().size()),
                () -> assertEquals("IBK", account1.getBank().getName()),
                () -> assertEquals("Miguel", bank.getAccounts().stream()
                        .filter(c -> c.getPerson().equals("Miguel"))
                        .findFirst()
                        .get().getPerson()),
                () -> assertTrue(bank.getAccounts().stream()
                        .filter(c -> c.getPerson().equals("Miguel"))
                        .findFirst().isPresent()),
                () -> assertTrue(bank.getAccounts().stream()
                        .anyMatch(c -> c.getPerson().equals("Miguel")))
        );
    }
}