# GiTaigHub

GiTaigHub is a specification to code converter written in Java for Java.

This idea comes from one main practice which is to write a full description of software functionality before coding and testing it.


In big project, these steps are processed by 3 differents teams and the knowledge of functionality have to be duplicated to all these stackholders.


Here we are trying to automate tests from the first document which describes the functionality.


To get this description, we use automatic Taiga.io bridge to allow the user to directly convert his user stories to TDD code and unit test.

This software is able to convert Structured English to code.
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

Our solution is also able to handle huge structured english, such as :


UserStory : Ticketing system automatic affectation
```
TicketingSystem can affect automatically to Team
   IF Ticket's environment is set on Integration THEN
      IF Ticket's skill is middleware THEN
         IF Skill's level is easy THEN
            add Ticket to L1System
         ELSE 
            IF Skill's level is medium or hard THEN
                add Ticket to L2System
            ENDIF
        ENDIF
      ENDIF
   ELSE
    IF Ticket's environment is set on approval THEN
         IF Ticket's skill is system THEN
            IF Skill's level is easy THEN
               add Ticket to L2System
            ELSE 
                IF Skill's level is medium THEN
                add Ticket to L2System
                ELSE
                add Ticket to L3System
                ENDIF
            ENDIF
         ENDIF
    ELSE
      IF Ticket's skill is middleware THEN
         IF Skill's level is easy THEN
               add Ticket to L2Middleware
            ELSE
                IF Skill's level is medium THEN
                add Ticket to L2Middleware
                ELSE
                add Ticket to L3Middleware
                ENDIF
            ENDIF
      ENDIF
   ENDIF
      IF Ticket's skill is system THEN
            IF Skill's level is easy THEN
               add Ticket to L2System
            ELSE 
                IF Skill's level is medium or hard THEN
                add Ticket to L3System
                ENDIF
            ENDIF
      ENDIF
      IF Ticket's skill is middleware THEN
            IF Skill's level is easy THEN
               add Ticket to L2Middleware
            ELSE 
                IF Skill's level is medium or hard THEN
                add Ticket to L3Middleware
                ENDIF
            ENDIF
      ENDIF
  ENDIF
```


All user stories can be found on our Taiga project on this link : 

https://tree.taiga.io/project/franckw-opl/
