package rewards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import common.money.MonetaryAmount;

/**
 * A system test that verifies the components of the RewardNetwork application
 * work together to reward for dining successfully. Uses Spring to bootstrap the
 * application for use in a test environment using Stub repository classes.
 */
@SpringJUnitConfig(classes = TestInfrastructureConfig.class)
@ActiveProfiles("stub")
public class StubRewardNetworkTests {

    /**
     * The object being tested.
     */
    @Autowired
    private RewardNetwork rewardNetwork;

    @Test
    public void rewardForDining() {
        // create a new dining of 100.00 charged to credit card
        // '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234",
                                            "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork
            .rewardAccountFor(dining);

        // assert the expected reward confirmation results
        assertNotNull(confirmation);
        assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation
            .getAccountContribution();
        assertNotNull(contribution);

        // the contribution account number should be '123456789'
        assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        assertEquals(MonetaryAmount.valueOf("8.00"), contribution.getAmount());

        // the total contribution amount should have been split into 2
        // distributions
        assertEquals(2,
                     contribution.getDistributions()
                                 .size());

        // each distribution should be 4.00 (as both have a 50% allocation)
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution
            .getDistribution("Annabelle")
            .getAmount());
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution
            .getDistribution("Corgan")
            .getAmount());
    }
}
