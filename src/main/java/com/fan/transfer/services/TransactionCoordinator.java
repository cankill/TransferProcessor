package com.fan.transfer.services;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;

public interface TransactionCoordinator {
    Transaction.Id initNew(Transaction transaction);
    TransactionStatus getStatus(Transaction.Id transactionId);

    void init ();
}
