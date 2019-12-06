package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.CommitCommand;

import java.math.BigDecimal;

public class CommitDebitProcessor extends FinalProcessor<CommitCommand> {
    public CommitDebitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                 Repository<Account.Id, Account> accountRepository) {
        super(transactionRepository, accountRepository);
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

    @Override
    protected TransactionStatus hintTransactionStatus () {
        return TransactionStatus.SUCCESSFUL;
    }
}
