package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.RollbackCommand;

import java.math.BigDecimal;

public class RollbackCreditProcessor extends FinalProcessor<RollbackCommand> {
    public RollbackCreditProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                    Repository<Account.Id, Account> accountRepository) {
        super(transactionRepository, accountRepository);
    }

    /**
     * For Credit rollback we should increase current Balance by amount of a Hold
     * @param currentBalance  Hint for current Balance amount
     * @param holdAmount Hint for a Hold amount
     * @return current Balance increased by Hold amount
     */
    @Override
    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance.add(holdAmount);
    }

    @Override
    protected TransactionStatus hintTransactionStatus () {
        return TransactionStatus.ROLLED_BACK;
    }
}
