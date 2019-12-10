package com.fan.transfer.services.tm.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.command.CommitDebitCommand;

import java.math.BigDecimal;

public class CommitDebitProcessor extends FinalCommitProcessor<CommitDebitCommand> {
    public CommitDebitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                 Repository<Account.Id, Account> accountRepository,
                                 ProcessorFactoryInterface processorFactory) {
        super(transactionRepository, accountRepository, processorFactory);
    }

    /**
     * For Debit commit we must remove a Hold for transaction,
     * and increase Balance on this amount.
     * @param holdAmount Hint of an amount from a Hold
     * @return The same provided Hint Hold amount
     */
    @Override
    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance.add(holdAmount);
    }
}
