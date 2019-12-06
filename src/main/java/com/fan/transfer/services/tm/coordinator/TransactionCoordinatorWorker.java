package com.fan.transfer.services.tm.coordinator;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.TransferCommandManagerImpl;
import com.fan.transfer.services.tm.coordinator.model.CoordinatorDescriptor;
import com.fan.transfer.services.tm.worker.*;
import com.fan.transfer.services.tm.worker.model.SuccessInitReply;
import com.fan.transfer.services.tm.worker.model.SuccessReply;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class TransactionCoordinatorWorker implements Runnable {
    private final CoordinatorDescriptor tcDescriptor;
    private Map<Integer, TransferCommandManagerImpl.WorkerProcessDescriptor> workers;
    
    private CommitCreditProcessor commitCreditProcessor;
    private CommitDebitProcessor commitDebitProcessor;
    private RollbackCreditProcessor rollbackCreditProcessor;
    private RollbackDebitProcessor rollbackDebitProcessor;


    public TransactionCoordinatorWorker (final CoordinatorDescriptor tcDescriptor,
                                         final Map<Integer, TransferCommandManagerImpl.WorkerProcessDescriptor> workers,
                                         final Repository<Transaction.Id, Transaction> transactionRepository,
                                         final Repository<Account.Id, Account> accountRepository) {
        this.tcDescriptor = tcDescriptor;
        this.workers = workers;

        this.commitCreditProcessor = new CommitCreditProcessor(transactionRepository, accountRepository);
        this.commitDebitProcessor = new CommitDebitProcessor(transactionRepository, accountRepository);
        this.rollbackCreditProcessor = new RollbackCreditProcessor(transactionRepository, accountRepository);
        this.rollbackDebitProcessor = new RollbackDebitProcessor(transactionRepository, accountRepository);
    }

    @Override
    public void run () {
        workers.values().forEach(workerDescription -> workerDescription.getThread().start());
        try {
            while (!Thread.interrupted()) {
                processRepliesBatch();

//                sleepWhileEmptyQueues();
            }
        } catch (InterruptedException ignore) {
            log.info("Coordinator process '{}' was interrupted", tcDescriptor.getName());
        }
    }
    
    private void processRepliesBatch () throws InterruptedException {
        workers.values().forEach(worker -> {
            if(!worker.getWorkerDescriptor().getRepliesQueue().isEmpty()) {
                var reply = worker.getWorkerDescriptor().getRepliesQueue().pollFirst();
                var postResult = reply.execute();
                if(postResult instanceof SuccessInitReply) {
                    
                }

//            var reply = reply.execute();
//            tcDescriptor.getRepliesQueue().addLast(reply);

            }
        });
    }

    private void sleepWhileEmptyQueues () throws InterruptedException {
        synchronized (tcDescriptor) {
            while (tcDescriptor.queuesAreEmpty()) {
                log.debug("Transaction Coordinator process's '{}' queues are empty, go to wait state", tcDescriptor.getName());
                tcDescriptor.wait(5*60*1000);
            }
        }
    }






//    private void restore() {
//        transactionRepository.getAll().stream()
//                .filter(TransferCommandManagerImpl::isTmTransactionActive)
//                .sorted(TransferCommandManagerImpl::sortTransactionsByDateTime)
//                .forEach(trx -> activeQueue.addLast(trx));
//    }
//
//    private static int sortTransactionsByDateTime (Transaction transaction1, Transaction transaction2) {
//        if(transaction1.getDateTime() == null) {
//            return -1;
//        }
//
//        if(transaction2.getDateTime() == null) {
//            return 1;
//        }
//
//        return transaction1.getDateTime().compareTo(transaction2.getDateTime());
//    }
//
//    private static boolean isTmTransactionActive (Transaction trx) {
//        return trx.getType() == TransactionType.TM &&
//                trx.getStatus() == TransactionStatus.IN_PROGRESS;
//    }

}
