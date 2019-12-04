package com.fan.transfer.services.tm;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionType;
import com.fan.transfer.services.tm.model.CommandReplyType;
import com.fan.transfer.services.tm.model.WorkerDescription;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.model.Command;
import com.fan.transfer.services.tm.model.CommandReply;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
class UserBucketWorker implements Runnable {
    private final ConcurrentLinkedDeque<Command> commands;
    private final ConcurrentLinkedDeque<CommandReply> replies;
    private final WorkerDescription description;
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final Repository<Account.Id, Account> accountRepository;

    public UserBucketWorker (ConcurrentLinkedDeque<Command> commands,
                             ConcurrentLinkedDeque<CommandReply> replies,
                             WorkerDescription description,
                             Repository<Transaction.Id, Transaction> transactionRepository,
                             Repository<Account.Id, Account> accountRepository) {
        this.commands = commands;
        this.replies = replies;
        this.description = description;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void run () {
        try {
            while (!Thread.interrupted()) {
                process();
            }
        } catch (InterruptedException ignore) {
            log.info("Worker process '{}' was interrupted", description.getName());
        }
    }

    private void process () throws InterruptedException {
        synchronized (commands) {
            while (commands.isEmpty()) {
                log.debug("Worker process's '{}' queue is empty, go to wait state", description.getName());
                commands.wait();
            }
        }

        while (!commands.isEmpty()) {
            Command command = commands.pollFirst();
            switch (command.getType()) {
                case INIT_TRANSACTION:
                    processInitTransaction(command);
                    break;
                case COMMIT_TRANSACTION:
                    processCommitTransaction(command);
                    break;
                case ROLLBACK_TRANSACTION:
                    processRollbackTransaction(command);
                    break;
                default:
                    log.error("Command type is undefined: '{}'", command.getType());
            }
        }
    }

    private void processInitTransaction (Command command) {
        Transaction transaction = command.getTransaction();
        switch (transaction.getType()) {
            case DEBIT:
                Account account = accountRepository.get(transaction.getFrom());
                if(account != null) {
                    
                    account.getHold();
                } else {
                    CommandReply.builder()
                            .id(command.getId())
                            .type(CommandReplyType.FAILURE)
                            .message("Account '%s' not found")
                            .build();
                }

                break;
            case CREDIT:
                break;
            default:
                log.error("Transaction type is incorrect: '{}'", transaction.getType());

        }
    }

    private void processCommitTransaction (Command command) {

    }

    private void processRollbackTransaction (Command command) {

    }
}
