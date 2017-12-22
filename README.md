# VV-Assign3 : How to use this project

First run this command in the targeted project folder :

  	mvn package -DskipTests
  	
Then in the config.properties file in src/main/resources, specify the targeted project path
and the maven home path.

To run our program run this command:

    mvn surefire:test -Dtest=MutateTests
    
Results are in the log/ folder as targetedProject.html.

## Features 
 
 Here are the different mutations done by our program:
 
    1. '-' is replaced by +'
    2. '+' is replaced by '-'
    3. body of boolean methods replaced by 'return true'
    4. body of boolean methods replaced by  'return false'
    5. '<' is replaced by '>'
    6. '>' is replaced by '<'
    7. removed body of void methods
    8. body of double methods replaced by 'return 0'
    9. '/' is replaced by '*'
    10. '*' is replaced by '/'
    11. 'if==' is replaced by 'if!='
    12. 'if!=' is replaced by 'if=='