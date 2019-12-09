package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.RollbackDebitCommand;

import java.math.BigDecimal;

public class RollbackDebitProcessor extends FinalRollbackProcessor<RollbackDebitCommand> {
    public RollbackDebitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                   Repository<Account.Id, Account> accountRepository,
                                   ProcessorFactory processorFactory) {
        super(transactionRepository, accountRepository, processorFactory);
    }

    /**
     * For Debit rollback we should leave current Balance as-is
     * @param currentBalance  Hint for current Balance amount
     * @param holdAmount Hint for a Hold amount
     * @return current amount unchanged
     */
    @Override
    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance;
    }
}
