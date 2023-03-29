package org.mblas.junitapp.example.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mblas.junitapp.example.exceptions.InsufficientMoneyException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class AccountTest {

    Account account;

    TestInfo testInfo;

    TestReporter testReporter;

    @BeforeEach
    void init(TestInfo testInfo, TestReporter testReporter) {
        this.account = new Account("Miguel", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
    }

    @AfterEach
    void tearDown() {
        System.out.println("End");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Init Test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("End Test");
    }

    @Test
    @DisplayName("Test account name")
    void testNameAccount() {
        testReporter.publishEntry("execute..." + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName());
        String expected = "Miguel";
        String actual = account.getPerson();
        assertEquals(expected, actual);
        assertTrue(actual.equals("Miguel"));
    }

    @Test
    @DisplayName("Test account balance")
    void testBalanceAccount() {
        assertNotNull(account.getBalance());
        assertEquals(1000.12345, account.getBalance().doubleValue());
        assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Test ref account")
    void testRefAccount() {
        account = new Account("Miguel Blas", new BigDecimal("8900.9997"));
        Account account2 = new Account("Miguel Blas", new BigDecimal("8900.9997"));

        //assertNotEquals(account2, account);
        assertEquals(account2, account);
    }

    @Test
    @DisplayName("Test debit account")
    void testDebitAccount() {
        account.debit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(900, account.getBalance().intValue());
        assertEquals("900.12345", account.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Test credit account")
    void testCreditAccount() {
        account.credit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(1100, account.getBalance().intValue());
        assertEquals("1100.12345", account.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Test insufficient money")
    void insufficientMoneyExceptionAccount() {
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

    @Test
    @DisplayName("Test account balance Dev")
    void testBalanceAccountDev() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(isDev);
        assertNotNull(account.getBalance());
        assertEquals(1000.12345, account.getBalance().doubleValue());
        assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Test account balance Dev 2")
    void testBalanceAccountDev2() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(isDev, () -> {
            assertNotNull(account.getBalance());
            assertEquals(1000.12345, account.getBalance().doubleValue());
            assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @Tag("Param")
    @ParameterizedTest(name = "number {index} test with value {0} - {argumentsWithNames}")
    @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
    @DisplayName("Test debit account value source")
    void testDebitAccountValueSource(String amount) {
        account.debit(new BigDecimal(amount ));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Tag("Param")

    @ParameterizedTest(name = "number {index} test with value {0} - {argumentsWithNames}")
    @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000"})
    @DisplayName("Test debit account csv source")
    void testDebitAccountCsvSource(String index, String amount) {
        System.out.println(index + "->" + amount);
        account.debit(new BigDecimal(amount ));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Tag("Param")
    @ParameterizedTest(name = "number {index} test with value {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    @DisplayName("Test debit account csv file source")
    void testDebitAccountCsvFileSource(String amount) {
        account.debit(new BigDecimal(amount ));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Tag("Param")
    @ParameterizedTest(name = "number {index} test with value {0} - {argumentsWithNames}")
    @MethodSource("amountList")
    @DisplayName("Test debit account csv method source")
    void testDebitAccountMethodSource(String amount) {
        account.debit(new BigDecimal(amount ));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> amountList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000");
    }

    @Tag("Param")
    @ParameterizedTest(name = "number {index} test with value {0} - {argumentsWithNames}")
    @CsvSource({"200,100", "250,200", "300,300", "500,500", "750,700", "1000,1000"})
    @DisplayName("Test debit account csv source 2")
    void testDebitAccountCsvSource2(String balance, String amount) {
        System.out.println(balance + "->" + amount);
        account.setBalance(new BigDecimal(balance));
        account.debit(new BigDecimal(amount));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @Timeout(2)
    void testTimeOut() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
    void testTimeOut2() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    void testTimeOutWithAssertions() throws InterruptedException {
        assertTimeout(Duration.ofSeconds(5), () -> {
            TimeUnit.MILLISECONDS.sleep(1000);
        });
    }
}