package com.fan.transfer.integrational


import com.fan.transfer.api.model.CreateAccountResponse
import com.fan.transfer.api.model.CreateUserResponse
import com.fan.transfer.api.model.GetBalanceResponse
import com.fan.transfer.domain.Account
import com.fan.transfer.domain.ErrorResponse
import com.fan.transfer.domain.TransactionStatus
import com.fan.transfer.domain.User
import spock.lang.Unroll

import javax.ws.rs.core.Response

@Unroll
class RestEndpointsSpec extends MainIntegrationalSpecification {
    def setup() {
        def restClient = restClientFactory.create(ENDPOINT_ADDRESS)
        client = restClient.getClient()
        userRepository.removeAll()
        accountRepository.removeAll()
        transactionRepository.removeAll()
    }

    def cleanup() {
        client.close()
    }

    def "create User '#userName'"() {
        setup:
        client.path(userPath)
        Response userResp = client.post(userCreateRequest)
        def response = userResp.readEntity(CreateUserResponse.class)
        def userId = new User.Id(response.id)
        def entity = userRepository.get(userId)

        expect:
        entity.name == userName
        entity.email == userEmail
        entity.phone == userPhone

        where:
        userPath << ["/user"]
        userName << ["AndrewFan"]
        userEmail << ["filyaniny@gmail.com"]
        userPhone << ["+35797648671"]
        userCreateRequest << [[name: userName, email: userEmail, phone: userPhone]]
    }

    def "create Account for User"() {
        setup:
        userRepository.add(user)
        client.path(accountPath, userId.value)
        Response accountResp = client.post(accountCreateRequest)
        def response = accountResp.readEntity(CreateAccountResponse.class)
        def accountId = new Account.Id(response.id)
        def entity = accountRepository.get(accountId)

        expect:
        entity.balance == accountBalance
        entity.currency == Currency.getInstance(accountCurrency)
        entity.userId == userId

        where:
        userId << [new User.Id("userId")]
        user << [new User(userId, "AndrewFan", "filyaniny@gmail.com", "+35797648671")]
        accountPath << ["/user/{userId}/account"]
        accountBalance << [33.77]
        accountCurrency << ["USD"]
        accountCreateRequest << [[balance: accountBalance, currency: accountCurrency]]
    }

    def "Do transfer from Account: '#accountIds[0]' to Account: '#accountIds[1]'"() {
        setup:
        userRepository.add(user)
        accounts.each { accountRepository.add(it) }
        
        client.path(transferPath, userId.value, accountIds[0].value)
        def transferResp = client.post(transferRequest)
        waitProcessingToFinish(300)
        def account1 = accountRepository.get(accountIds[0])
        def account2 = accountRepository.get(accountIds[1])

        expect:
        transferResp.status == Response.Status.OK.getStatusCode()
        account1.balance == accountBalances[0] - 14.01
        account2.balance == accountBalances[1] + 14.01

        where:
        userId << [new User.Id("userId")]
        user << [new User(userId, "AndrewFan", "filyaniny@gmail.com", "+35797648671")]
        accountIds << [[new Account.Id("account1"), new Account.Id("account2")]]
        accountBalances << [[100.09, 33.77]]
        accountCurrency << [Currency.getInstance("USD")]
        accounts << [[new Account(accountIds[0], userId, accountCurrency, accountBalances[0], []),
                      new Account(accountIds[1], userId, accountCurrency, accountBalances[1], [])]]
        transferPath << ["/user/{userId}/account/{accountId}/transfer"]
        transferRequest << [[from: accountIds[0].value, to: accountIds[1].value, amount: 14.01]]
    }

    def "Do transfer from Account: '#accountIds[0]' to Account: '#accountIds[1]' with not enough balance"() {
        setup:
        userRepository.add(user)
        accounts.each { accountRepository.add(it) }

        client.path(transferPath, userId.value, accountIds[0].value)
        def transferResp = client.post(transferRequest)
        waitProcessingToFinish(300)
        def account1 = accountRepository.get(accountIds[0])
        def account2 = accountRepository.get(accountIds[1])

        expect:
        transferResp.status == Response.Status.OK.getStatusCode()
        account1.balance == accountBalances[0]
        account1.hold.size() == 0
        account2.balance == accountBalances[1]
        account2.hold.size() == 1
        account2.hold[0].getStatus() == TransactionStatus.DONE
        account2.hold[0].amount == transferRequest.amount
        
        where:
        userId << [new User.Id("userId")]
        user << [new User(userId, "AndrewFan", "filyaniny@gmail.com", "+35797648671")]
        accountIds << [[new Account.Id("account1"), new Account.Id("account2")]]
        accountBalances << [[14.00, 33.77]]
        accountCurrency << [Currency.getInstance("USD")]
        accounts << [[new Account(accountIds[0], userId, accountCurrency, accountBalances[0], []),
                      new Account(accountIds[1], userId, accountCurrency, accountBalances[1], [])]]
        transferPath << ["/user/{userId}/account/{accountId}/transfer"]
        transferRequest << [[from: accountIds[0].value, to: accountIds[1].value, amount: 14.01]]
    }

    def "Do transfer from Account: '#accountIds[0]' to same Account"() {
        setup:
        userRepository.add(user)
        accounts.each { accountRepository.add(it) }

        client.path(transferPath, userId.value, accountIds[0].value)
        def transferResp = client.post(transferRequest)

        expect:
        transferResp.status == Response.Status.BAD_REQUEST.getStatusCode()

        where:
        userId << [new User.Id("userId")]
        user << [new User(userId, "AndrewFan", "filyaniny@gmail.com", "+35797648671")]
        accountIds << [[new Account.Id("account1"), new Account.Id("account2")]]
        accountBalances << [[14.00, 33.77]]
        accountCurrency << [Currency.getInstance("USD")]
        accounts << [[new Account(accountIds[0], userId, accountCurrency, accountBalances[0], []),
                      new Account(accountIds[1], userId, accountCurrency, accountBalances[1], [])]]
        transferPath << ["/user/{userId}/account/{accountId}/transfer"]
        transferRequest << [[from: accountIds[0].value, to: accountIds[0].value, amount: 1.01]]
    }

    def "Get '#accountId' Balance"() {
        setup:
        userRepository.add(user)
        accountRepository.add(account)

        client.path(balancePath, userId.value, accountId.value)
        def balanceResp = client.get()
        def balance = balanceResp.readEntity(GetBalanceResponse.class)

        expect:
        balanceResp.status == Response.Status.OK.getStatusCode()
        balance == expectedBalance

        where:
        userId << [new User.Id("userId")]
        user << [new User(userId, "AndrewFan", "filyaniny@gmail.com", "+35797648671")]
        accountId << [new Account.Id("account1")]
        accountBalance << [100.09]
        accountCurrency << [Currency.getInstance("USD")]
        account << [new Account(accountId, userId, accountCurrency, accountBalance, [])]
        balancePath << ["/user/{userId}/account/{accountId}/balance"]
        expectedBalance << [new GetBalanceResponse(accountId.value, accountBalance.toString(), accountCurrency.getCurrencyCode())]
    }

    def "get unknown '#userId' balance"() {
        setup:
        client.path(path, userId, accountId)
        Response resp = client.get()

        expect:
        resp.readEntity(ErrorResponse.class) == error

        where:
        path                                         | userId    | accountId    || error
        "/user/{userId}/account/{accountId}/balance" | "userId1" | "accountId1" || ErrorResponse.builder().error("User 'userId1' was not found").build()
    }

    def "get unknown '#accountId' balance"() {
        setup:
        userRepository.add(new User(new User.Id(userId), "User Name", "userEmail@some.com", "+0userPhoneNumber"))
        client.path(path, userId, accountId)
        Response resp = client.get()

        expect:
        resp.readEntity(ErrorResponse.class) == error

        where:
        path                                         | userId    | accountId    || error
        "/user/{userId}/account/{accountId}/balance" | "userId1" | "accountId1" || ErrorResponse.builder().error("Account 'accountId1' was not found").build()
    }
}