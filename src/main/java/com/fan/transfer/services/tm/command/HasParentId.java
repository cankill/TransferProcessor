package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Transaction;

public interface HasParentId {
    Transaction.Id getParentTransactionId();
}
