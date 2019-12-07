package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.RollbackCommandOld;

import java.math.BigDecimal;

public class RollbackDebitProcessor extends FinalProcessor<RollbackCommandOld> {
    public RollbackDebitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                   Repository<Account.Id, Account> accountRepository, ProcessorFactory processorFactory) {
        super(transactionRepository, accountRepository);
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

    @Override
    protected TransactionStatus hintTransactionStatus () {
        return TransactionStatus.ROLLBACK;
    }
}
