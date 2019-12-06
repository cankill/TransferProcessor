package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;

import java.math.BigDecimal;

public class DebitProcessor extends InitProcessor {
    public DebitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                           Repository<Account.Id, Account> accountRepository) {
        super(transactionRepository, accountRepository);
    }

    /**
     * For Debit processing current Balance always enough
     * @param currentBalance Hint for current Balance
     * @param transferAmount Hint for transfer amount
     * @return true
     */
    protected boolean balanceIsEnough (BigDecimal currentBalance, BigDecimal transferAmount) {
        return true;
    }

    /**
     *  Provide a hint for Transaction type
     * @return TransactionType.DEBIT
     */
    protected TransactionType hintTransactionType () {
        return TransactionType.DEBIT;
    }

    /**
     * For Debit balance should not be changed as amount of transfer should be on Hold
     * @param currentBalance Hint for current Balance
     * @param holdAmount Hint for transfer amount
     * @return unchanged current Balance
     */
    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance;
    }
}
