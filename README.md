# GiTaigHub

GiTaigHub is a specification to code converter write in Java for Java.\n 

This idea come from one main practice which is to write a full description of software functionality before coding and testing it.\n
In big project, these steps are processed by 3 differents teams and the knowledge of functionality have to be duplicated to all these stackholders.\n
Here we are trying to automate test from the first document which describe the functionality. \n
To get this description, we use automatic Taiga.io bridge will allow the user to directly convert his user stories to TDD code and unit test.\n

This soft is capable to convert Structured English to code.\n
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

Will create three classes Customer, BankAccount, BankManagement which represents the entity.

To Continue...
