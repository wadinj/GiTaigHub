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

Example of Structured English : 
UserStory : Cashier can add products to basket
```
Ticketing system can affect automatically to the appropriate team.
The ticket can be dispatch to 3 differents teams :
   - L1 : First Level team
   - L2 : Second level team
   - L3 : Third level team
The ticket could be set with several properties :
   - Emergency : Low, Medium or High
   - Environment : Integration, Approval, pre-production
   - Skill needed : System, Middleware, Network
   - Level of skills needed : Easy, Medium or hard
   - Description : A text which represents the operation
   - Server URL
 The ticket is represented by an unique id.
   IF Ticket is set on Integration environment THEN
      IF Skill needed for the ticket is System THEN
         IF level of skills needed is easy THEN
            Add ticket to L1 System
         ELSE IF level of skills needed is medium or hard THEN
            Add ticket to L2 System
         END IF
      END IF
      IF Skill needed for the ticket is Middleware THEN
         IF level of skills needed is easy THEN
            Add ticket to L1 System
         ELSE IF level of skills needed is medium or hard THEN
            Add ticket to L2 System
         END IF
      END IF
   ELSE IF ticket is set on Approval environment THEN
         IF Skill needed for the ticket is System THEN
            IF level of skills needed is easy THEN
               Add ticket to L2 System
            ELSE IF level of skills needed is medium THEN
               Add ticket to L2 System
            ELSE
               Add ticket to L3 System
            END IF
         END IF
      IF Skill needed for the ticket is Middleware THEN
         IF level of skills needed is easy THEN
               Add ticket to L2 Middleware
            ELSE IF level of skills needed is medium THEN
               Add ticket to L2 Middleware
            ELSE
               Add ticket to L3 Middleware
            END IF
      END IF
  ELSE
      IF Skill needed for the ticket is System THEN
            IF level of skills needed is easy THEN
               Add ticket to L2 System
            ELSE IF level of skills needed is medium or hard THEN
               Add ticket to L3 System
            END IF
      END IF
      IF Skill needed for the ticket is Middleware THEN
            IF level of skills needed is easy THEN
               Add ticket to L2 Middleware
            ELSE IF level of skills needed is medium or hard THEN
               Add ticket to L3 Middleware
            END IF
      END IF
  END IF
```
