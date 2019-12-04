package com.fan.transfer.services;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TransactionCoordinatorImpl implements TransactionCoordinator {
    private ConcurrentLinkedDeque<Transaction> activeQueue = new ConcurrentLinkedDeque<>();
    
    @Inject
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> transactionRepository;

    public void init () {
        restore();
    }

    @Override
    public Transaction.Id initNew (Transaction transaction) {
        transaction.setId(generateId());
        transaction.setType(TransactionType.TM);
        transaction.setDateTime(LocalDateTime.now(ZoneOffset.UTC));
        if(!transactionRepository.add(transaction)) {
            throw new DbException(String.format("Transaction '%s' was not created", transaction));
        }

        activeQueue.addLast(transaction);

        return transaction.getId();
    }

    @Override
    public TransactionStatus getStatus (Transaction.Id transactionId) {
        return null;
    }
    
    private void restore() {
        transactionRepository.getAll().stream()
                .filter(TransactionCoordinatorImpl::isTmTransactionActive)
                .sorted(TransactionCoordinatorImpl::sortTransactionsByDateTime)
                .forEach(trx -> activeQueue.addLast(trx));
    }

    private static int sortTransactionsByDateTime (Transaction transaction1, Transaction transaction2) {
        if(transaction1.getDateTime() == null) {
            return -1;
        }

        if(transaction2.getDateTime() == null) {
            return 1;
        }

        return transaction1.getDateTime().compareTo(transaction2.getDateTime());
    }

    private static boolean isTmTransactionActive (Transaction trx) {
        return trx.getType() == TransactionType.TM &&
               trx.getStatus() == TransactionStatus.IN_PROGRESS;
    }

    private static Transaction.Id generateId () {
        return Transaction.Id.valueOf(UUID.randomUUID().toString());
    }
}
