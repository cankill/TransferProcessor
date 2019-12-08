package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Transaction;

public interface HasParentId {
    Transaction.Id getParentTransactionId();
}
