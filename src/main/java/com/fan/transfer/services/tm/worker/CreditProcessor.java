package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

public class CreditProcessor extends InitProcessor {
    public CreditProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                            Repository<Account.Id, Account> accountRepository) {
        super(transactionRepository, accountRepository);
    }

    /**
     * For Credit processing current Balance should be more or equals
     * of a transfer amount
     * @param currentBalance Hint for current Balance
     * @param amount Hint for transfer amount
     * @return true if current Balance is enough to decrease a transfer amount
     */
    protected boolean balanceIsEnough (BigDecimal currentBalance, BigDecimal amount) {
        return currentBalance.compareTo(amount) > 0;
    }

    /**
     *  Provide a hint for Transaction type
     * @return TransactionType.CREDIT
     */
    protected TransactionType hintTransactionType () {
        return TransactionType.CREDIT;
    }

    /**
     * For Credit balance should be decreased for amount of transfer
     * @param currentBalance Hint for current Balance
     * @param holdAmount Hint for transfer amount
     * @return decreased current Balance
     */
    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance.subtract(holdAmount);
    }
}