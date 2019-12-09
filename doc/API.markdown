# API for money transfer POC

## Common parameters:
Headers:
```json
{
  "Content-Type": "application/json",
  "Accept": "application/json"
}
```

## User creation

POST /v1/user

Body:
```json
{
  "name": "andrew",
  "email": "filyaniny@gmail.com",
  "phone": "+357"
}
```
Reply:
```json
{
  "id": "2b2032ee-a959-444a-a321-21931095bdd0"
}
```
The id field will contain a generated Id for the new User created in DB 

## Account creation 

POST /v1/user/{userId}/account

Body:
```json
{
  "balance": 10000.98,
  "Currency": "USD"
}
```
Reply:
```json
{
  "id": "42c97d73-c034-470c-a1ff-9bf573efd1a0"
}
```
The id field will contain a generated Id for the new Account created in DB

## Get current balance for Account

GET /v1/user/{userId}/account/{AccountId}/balance

Reply:
```json
{
  "accountId": "42c97d73-c034-470c-a1ff-9bf573efd1a0",
  "balance": "10,000.98",
  "currency": "USD"
}
```

## Transfer money between two accounts
Body:
```json
{
  "from": "64e1c80e-e1eb-4c0a-b214-50be176a3d88",
  "to": "ce4a74fc-1dbe-40be-b78f-2badbc3da2c6",
  "amount": 100.01
}
```
