# GiTaigHub

GiTaigHub is a specification to code converter written in Java for Java.

This idea comes from one main practice which is to write a full description of software functionality before coding and testing it.


In big project, these steps are processed by 3 differents teams and the knowledge of functionality have to be duplicated to all these stackholders.


Here we are trying to automate tests from the first document which describes the functionality.


To get this description, we use automatic Taiga.io bridge to allow the user to directly convert his user stories to TDD code and unit test.

This soft is capable to convert Structured English to code.
Per example :
```
BankManagement can allow loan facility to Customer
IF Customer has a BankAccount THEN
   IF Customer has no dues from previous account THEN
      allow loan facility
   ELSE
      IF BankManagement approves Customer THEN
         allow loan facility
      ELSE
         reject
      ENDIF
   ENDIF
ELSE
   reject
ENDIF
```

Will create four classes Customer, BankAccount, BankManagement and Dues which represent the entities.

To be continued...
