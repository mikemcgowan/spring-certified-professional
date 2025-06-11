package accounts.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import accounts.internal.StubAccountManager;
import rewards.internal.account.Account;

/**
 * A JUnit test case testing the AccountController.
 */
public class AccountControllerTests {

    private AccountController controller;

    @BeforeEach
    public void setUp() throws Exception {
        controller = new AccountController(new StubAccountManager());
    }

    @Test
    public void testHandleDetailsRequest() {
        Account account = controller.accountDetails(0);
        assertNotNull(account);
        assertEquals(Long.valueOf(0), account.getEntityId());
    }

    @Test
    public void testHandleSummaryRequest() {
        List<Account> accounts = controller.accountSummary();
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(Long.valueOf(0),
                     accounts.get(0)
                             .getEntityId());
    }
}
