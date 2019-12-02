//package com.fan.transfer.pereferial.db.impl;
//
//import com.fan.transfer.domain.Account;
//import com.fan.transfer.domain.Transaction;
//import com.fan.transfer.domain.User;
//import com.fan.transfer.pereferial.db.AccountRepository;
//import com.fan.transfer.pereferial.db.TransactionRepository;
//import com.fan.transfer.pereferial.db.UserRepository;
//
//public class InMemoryDB implements UserRepository, AccountRepository, TransactionRepository {
//    private InMemoryTable<User> userTable = new InMemoryTable<>(User.class);
//    private InMemoryTable<Account> accountTable = new InMemoryTable<>(Account.class);
//    private InMemoryTable<Transaction> transactionTable = new InMemoryTable<>(Transaction.class);
//
//    /**
//     * Add an User into Users table
//     * @param user New User to add
//     * @return true if User is added, and false if User exists
//     */
//    @Override
//    public boolean add(User user) {
//        return userTable.add(user);
//    }
//
//    /**
//     * Update an User by it's id in a table
//     * @param userId Existing User identity to update
//     * @param user Patch object to apply
//     * @return true if User was updated
//     */
//    @Override
//    public boolean update(String userId, User user) {
//        return userTable.update(userId, user);
//    }
//
//    /**
//     * Remove existing User from a table
//     * @param userId Existing User identity to remove
//     * @return  true if User was removed
//     */
//    @Override
//    public boolean removeUser(String userId) {
//        return userTable.remove(userId);
//    }
//
//    /**
//     * This function adds an Account into Accounts table
//     * @param account new account to add
//     * @return true if Account was added, and false if Account exists
//     */
//    @Override
//    public boolean add(Account account) {
//        return accountTable.add(account);
//    }
//
//    /**
//     * Update an Account by it's id in a table
//     * @param accountId Existing Account identity to update
//     * @param account Patch object to apply
//     * @return true if Account was updated
//     */
//    @Override
//    public boolean update(String accountId, Account account) {
//        return false;
//    }
//
//    /**
//     * Remove existing Account from a table
//     * @param accountId Existing Account identity to remove
//     * @return  true if Account was removed
//     */
//    @Override
//    public boolean removeAccount(String accountId) {
//        return false;
//    }
//
//    /**
//     * This function adds an Transaction into Transactions table
//     * @param transaction new transaction to add
//     * @return true if Transaction is added, and false if Transaction exists
//     */
//    public boolean add(Transaction transaction) {
//        return transactionTable.add(transaction);
//    }
//
//    /**
//     * Update a Transaction by it's id in a table
//     * @param transactionId Existing Transaction identity to update
//     * @param transaction Patch object to apply
//     * @return true if Transaction was updated
//     */
//    @Override
//    public boolean update(String transactionId, Transaction transaction) {
//        return false;
//    }
//
//    /**
//     * Remove existing Transaction from a table
//     * @param transactionId Existing Transaction identity to remove
//     * @return  true if Transaction was removed
//     */
//    @Override
//    public boolean removeTransaction(String transactionId) {
//        return false;
//    }
//
//    public void cleanDB() {
//        userTable.removeAll();
//        transactionTable.removeAll();
//        accountTable.removeAll();
//    }
//}
