package rewards.internal.account;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import common.money.MonetaryAmount;
import common.money.Percentage;
import rewards.AccountContribution;
import rewards.AccountContribution.Distribution;

/**
 * An account for a member of the reward network. An account has one or more
 * beneficiaries whose allocations must add up to 100%.
 *
 * An account can make contributions to its beneficiaries. Each contribution is
 * distributed among the beneficiaries based on an allocation.
 *
 * An entity. An aggregate.
 */
//	TODO-02: Review the JPA annotations on this class and make sure you know what each does.
//	@Entity - Marks this class as a JPA persistent class
//	@Table - Specifies the exact table name to use on the DB (would be "Account" if unspecified).
//	@Id - Indicates the field to use as the primary key on the database
//	@Column - Identifies column-level customization, such as the exact name of the column on the table.
//	@OneToMany - Identifies the field on the 'one' side of a one to many relationship.
//	@JoinColumn - Identifies the column on the 'many' table containing the column to be used when joining.  Usually a foreign key.
@Entity
@Table(name = "T_ACCOUNT")
public class Account {

    @Id
    @Column(name = "ID")
    private Long entityId;

    // No need for @Column, mapped automatically to NUMBER
    private String number;

    // No need for @Column, mapped automatically to NAME
    private String name;

    @OneToMany
    @JoinColumn(name = "ACCOUNT_ID")
    private Set<Beneficiary> beneficiaries = new HashSet<Beneficiary>();

    @Column(name = "CREDIT_CARD")
    private String creditCardNumber;

    public Account() {
    }

    /**
     * Create a new account.
     *
     * @param number the account number
     * @param name the name on the account
     */
    public Account(String number, String name) {
        this.number = number;
        this.name = name;
    }

    /**
     * Getter for the credit card number for this account.
     *
     * @return the credit card number for this account as a 16-character String.
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Setter for the credit card number for this account.
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Returns the number used to uniquely identify this account.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Returns the name on file for this account.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id for this account.
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the id for this account. Package local - only available to tests.
     */
    void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Add a single beneficiary with a 100% allocation percentage.
     *
     * @param beneficiaryName the name of the beneficiary (should be unique)
     */
    public void addBeneficiary(String beneficiaryName) {
        addBeneficiary(beneficiaryName, Percentage.oneHundred());
    }

    /**
     * Add a single beneficiary with the specified allocation percentage.
     *
     * @param beneficiaryName the name of the beneficiary (should be unique)
     * @param allocationPercentage the beneficiary's allocation percentage within
     * this account
     */
    public void addBeneficiary(String beneficiaryName, Percentage allocationPercentage) {
        beneficiaries.add(new Beneficiary(beneficiaryName, allocationPercentage));
    }

    /**
     * Validation check that returns true only if the total beneficiary allocation
     * adds up to 100%.
     */
    public boolean isValid() {
        Percentage totalPercentage = Percentage.zero();
        for (Beneficiary b : beneficiaries) {
            try {
                totalPercentage = totalPercentage.add(b.getAllocationPercentage());
            } catch (IllegalArgumentException e) {
                // total would have been over 100% - return invalid
                return false;
            }
        }
        if (totalPercentage.equals(Percentage.oneHundred())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Make a monetary contribution to this account. The contribution amount is
     * distributed among the account's beneficiaries based on each beneficiary's
     * allocation percentage.
     *
     * @param amount the total amount to contribute
     */
    public AccountContribution makeContribution(MonetaryAmount amount) {
        if (!isValid()) {
            throw new IllegalStateException(
                "Cannot make contributions to this account: it has invalid beneficiary allocations");
        }
        Set<Distribution> distributions = distribute(amount);
        return new AccountContribution(getNumber(), amount, distributions);
    }

    /**
     * Distribute the contribution amount among this account's beneficiaries.
     *
     * @param amount the total contribution amount
     * @return the individual beneficiary distributions
     */
    private Set<Distribution> distribute(MonetaryAmount amount) {
        Set<Distribution> distributions = new HashSet<Distribution>(beneficiaries.size());
        for (Beneficiary beneficiary : beneficiaries) {
            MonetaryAmount distributionAmount = amount.multiplyBy(beneficiary.getAllocationPercentage());
            beneficiary.credit(distributionAmount);
            Distribution distribution = new Distribution(beneficiary.getName(), distributionAmount,
                                                         beneficiary.getAllocationPercentage(), beneficiary.getSavings());
            distributions.add(distribution);
        }
        return distributions;
    }

    /**
     * Returns the beneficiaries for this account. Callers should not attempt to
     * hold on or modify the returned set. This method should only be used
     * transitively; for example, called to facilitate account reporting.
     *
     * @return the beneficiaries of this account
     */
    public Set<Beneficiary> getBeneficiaries() {
        return Collections.unmodifiableSet(beneficiaries);
    }

    /**
     * Returns a single account beneficiary. Callers should not attempt to hold on
     * or modify the returned object. This method should only be used transitively;
     * for example, called to facilitate reporting or testing.
     *
     * @param name the name of the beneficiary e.g "Annabelle"
     * @return the beneficiary object
     */
    public Beneficiary getBeneficiary(String name) {
        for (Beneficiary b : beneficiaries) {
            if (b.getName()
                 .equals(name)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No such beneficiary with name '" + name + "'");
    }

    /**
     * Used to restore an allocated beneficiary. Should only be called by the
     * repository responsible for reconstituting this account.
     *
     * @param beneficiary the beneficiary
     */
    void restoreBeneficiary(Beneficiary beneficiary) {
        beneficiaries.add(beneficiary);
    }

    public String toString() {
        return "Number = '" + number + "', name = " + name + "', beneficiaries = " + beneficiaries;
    }
}
