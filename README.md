# Wallet Service

This project was developed for a Recarga Pay recruitment process. It took something about 8 to 9 hours to complete it.

## Starting this app

This project was built using maven and Java version 21. Make sure you have both Java SDK and Maven installed. Once you have it, the very first thing you need to do is to build its jar using maven from the root folder of the project:

mvn clean package

Once the application is built, you can just run the jar:

java -jar ./target/wallet-0.0.1-SNAPSHOT.jar

Alternatively to the whole process above, you can run it just using maven with the following command:

mvn spring-boot:run

This project does not depend on anything else to run, since it uses an H2 database that is started automatically.

Once it's started, API should be available at http://localhost:8080

## How to use it

Here is a list of available functionalities and valid curl commands for each of them:
- Retrieve person by document number:
```
curl --location --request GET 'http://localhost:8080/persons/{documentNumber}'
```
- Create a wallet for a person:
```
curl --location --request POST 'http://localhost:8080/persons/{documentNumber}/wallets' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "description": "Wallet to pay bills"
    }'
```
- Retrieve balance by wallet number:
```
curl --location --request GET 'http://localhost:8080/wallets/{accountNumber}/balance'
```
- Withdrawal from wallet:
```
curl --location --request POST 'http://localhost:8080/wallets/{accountNumber}/withdrawals' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "amount": 1.00
    }'
```
- Deposit to wallet:
```
curl --location --request POST 'http://localhost:8080/wallets/{accountNumber}/deposits' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "amount": 1.50
    }'
```
- Transfer between wallets:
```
curl --location --request POST 'http://localhost:8080/wallets/{sourceAccountNumber}/transfers' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "destinationAccountNumber": {destinationAccountNumber},
        "amount": 1.00
    }'
```
- Get wallet's transaction history between dates:
```
curl --location --request GET 'http://localhost:8080/wallets/1/transactions?from=2025-06-05&to=2025-06-08' \
    --header 'Content-Type: application/json'
```

Alternatively, this project contains a postman collection that can be found in ```./src/main/resources/postman/```

<b>IMPORTANT</b>:

Since user creation or management was not a requirement, this application will automatically add two users that can be used to test:
- Person id 11122233344, who is created owning two wallets (numbers 1 and 2)
- Person id 55566677788, who doesn't own any wallet

Have in mind that application is not set to persist any data inserted or removed after it's terminated, meaning everytime you restart the application will work as a database reset.

If you ever want to change anything on this users, check ```DataInitializer``` class.

<b>ABOUT THE H2 DATABASE</b>

If you'd like to check database, it will be available at 

```http://localhost:8080/h2-console```

Use the following settings to get access:
```
JDB URL: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
User name: sa
Password: <<leave it blank>>
```
## Auditing:

Audit package does not refer to the API, but it was the solution I came up to keep traceability of transaction operations. 

It stores all monetary transactions in a table and identifies each transaction by ```trace_id``` column. If you find the same trace_id value for more than one operation, it means that all those operations are part of the same request. This concept is important because transfers are a pair of transactions (debit from a source account and credit to a destination account) and they will be identified as part of a single operation by this column.

Audit table also stores a status (SUCCESS or FAILURE) and a message field with any relevant information (at this point, it stores exception messages for failed transactions).

## Design choices:

As mentioned before, this project runs on Java 21. It is a Spring Boot application and it uses a H2 memory database. I tried to use as few libraries as possible due to the simplicity of this project. Almost everything is just part of Spring library.

Code separates functionalities in 4 main packages:
- Person
- Wallet
- Transaction
- Audit

Because of the project size they are just packages, but they were thought to be early stage of what could become a module or even a microservice, as if this project was meant to grow someday.

## Tradeoffs due to time and project goals

- A lot of business rules were not implemented although they are obvious. I implemented a overdraft limit verification to accounts just to show that I care for business rules and validations, but many others (required fields in requests, amounts greater than zero, etc) could have been made even with requirements not specifying them.
- Also I opted for some rules that I don't know if they should be the way I did. For example: can a single person have multiple wallets? I assumed yes, but it's not up to me to decide it in a real scenario.
- As mentioned earlier in this document, if I was working on a real project we should be separating responsabilities on modules or microservices, not packages
- I used document number as primary ID on database. Also used wallet (account) number as primary id. I know it's not what we do in a real scenario, but it was something that could save me some time in development
- I opted not to use mapping libraries, such as Mapstruct, because my entities were too small. I thought it was not worth it, but I'd surely do and use something more robust in a real scenario.
- Naturally an H2 database is not ideal for a real project. Also I'm using ```ddl-auto: create-drop``` configuration and creating database from ORM with very few validations. In a real scenario it should be an SQL script that makes sure database validates data regardless how the application was written.
- Auditing would be much better if we used a proper logging tool (like Splunk or Sumologic). At this moment these logs are only accessible by querying directly to database. I just wanted to show how to keep trace of operations, but I don't consider it a proper solution.