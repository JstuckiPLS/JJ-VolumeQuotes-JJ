package com.pls.core.domain.organization;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link BillToEntity}.
 * 
 * @author Artem Arapov
 *
 */
public class BillToEntityTest {

    private static final BigDecimal EXPECTED_AMOUNT = new BigDecimal(100);
    private static final BigDecimal EXPECTED_UNBILLED_AMOUNT = new BigDecimal(50);
    private static final BigDecimal EXPECTED_OPEN_BALANCE = new BigDecimal(30);

    @Test
    public void shouldReturnZeroLimitIfCreditAndUnbilledAmountIsNull() {
        BillToEntity sut = new BillToEntity();

        BigDecimal actual = sut.getAvailableCreditAmount();

        Assert.assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    public void shouldReturnZeroLimitIfCreditAmountIsNull() {
        BillToEntity sut = new BillToEntity();
        sut.setCreditLimit(null);
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(EXPECTED_AMOUNT);
        sut.setUnbilledRevenue(unbilledRevenue);

        BigDecimal actual = sut.getAvailableCreditAmount();
        BigDecimal expected = BigDecimal.ZERO.subtract(EXPECTED_AMOUNT);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnActualLimitIfUnbilledAmountIsNull() {
        BillToEntity sut = new BillToEntity();
        CreditLimitEntity limit = new CreditLimitEntity();
        limit.setCreditLimit(EXPECTED_AMOUNT);
        sut.setCreditLimit(limit);
        sut.setUnbilledRevenue(null);

        BigDecimal actual = sut.getAvailableCreditAmount();

        Assert.assertEquals(EXPECTED_AMOUNT, actual);
    }

    @Test
    public void shouldReturmActualLimitIfOpenBalanceIsNull() {
        BillToEntity sut = new BillToEntity();
        CreditLimitEntity limit = new CreditLimitEntity();
        limit.setCreditLimit(EXPECTED_AMOUNT);
        sut.setCreditLimit(limit);
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(EXPECTED_UNBILLED_AMOUNT);
        sut.setUnbilledRevenue(unbilledRevenue);
        sut.setOpenBalance(null);

        BigDecimal actual = sut.getAvailableCreditAmount();

        Assert.assertEquals(EXPECTED_AMOUNT, actual.add(EXPECTED_UNBILLED_AMOUNT));
    }

    @Test
    public void shouldReturnActualLimitNormalCase() {
        BillToEntity sut = new BillToEntity();
        CreditLimitEntity limit = new CreditLimitEntity();
        limit.setCreditLimit(EXPECTED_AMOUNT);
        sut.setCreditLimit(limit);
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(EXPECTED_UNBILLED_AMOUNT);
        sut.setUnbilledRevenue(unbilledRevenue);
        OpenBalanceEntity openBalance = new OpenBalanceEntity();
        openBalance.setBalance(EXPECTED_OPEN_BALANCE);
        sut.setOpenBalance(openBalance);

        BigDecimal actual = sut.getAvailableCreditAmount();

        Assert.assertNotNull(actual);
        Assert.assertEquals(EXPECTED_AMOUNT, actual.add(EXPECTED_UNBILLED_AMOUNT).add(EXPECTED_OPEN_BALANCE));
    }

    @Test
    public void shouldReturnZeroIfCreditLessThanUnbilledAmount() {
        BillToEntity sut = new BillToEntity();
        CreditLimitEntity limit = new CreditLimitEntity();
        limit.setCreditLimit(BigDecimal.TEN);
        sut.setCreditLimit(limit);
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(EXPECTED_UNBILLED_AMOUNT);
        sut.setUnbilledRevenue(unbilledRevenue);

        BigDecimal actual = sut.getAvailableCreditAmount();
        BigDecimal expected = BigDecimal.TEN.subtract(EXPECTED_UNBILLED_AMOUNT);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnZeroIfCreditLimitIsNull() {
        BillToEntity sut = new BillToEntity();
        CreditLimitEntity limit = new CreditLimitEntity();
        limit.setCreditLimit(null);
        sut.setCreditLimit(limit);
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(EXPECTED_UNBILLED_AMOUNT);
        sut.setUnbilledRevenue(unbilledRevenue);

        BigDecimal actual = sut.getAvailableCreditAmount();
        BigDecimal expected = BigDecimal.ZERO.subtract(EXPECTED_UNBILLED_AMOUNT);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnCreditLimitIfUnbilledRevenueIsNull() {
        BillToEntity sut = new BillToEntity();
        CreditLimitEntity limit = new CreditLimitEntity();
        limit.setCreditLimit(EXPECTED_AMOUNT);
        sut.setCreditLimit(limit);
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(null);
        sut.setUnbilledRevenue(unbilledRevenue);

        BigDecimal actual = sut.getAvailableCreditAmount();

        Assert.assertEquals(EXPECTED_AMOUNT, actual);
    }
}
