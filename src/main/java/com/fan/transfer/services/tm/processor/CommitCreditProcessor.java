package com.fan.transfer.services.tm.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.command.CommitCreditCommand;

import java.math.BigDecimal;

public class CommitCreditProcessor extends FinalCommitProcessor<CommitCreditCommand> {
    public CommitCreditProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                  Repository<Account.Id, Account> accountRepository,
                                  ProcessorFactoryInterface processorFactory) {
        super(transactionRepository, accountRepository, processorFactory);
    }

    /**
     * For Credit commit we must only remove a Hold for transaction,
     * as Balance is already changed.
     * @param holdAmount Hint of an amount from a Hold (ignored for Credit)
     * @return Current balance unchanged
     */
    @Override
    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance;
    }
}
