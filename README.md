# VV-Assign3 : How to use this project

To start, we have to compile files from TargetProject's folder.
So, compile this file :

  	TargetProject/src/main/java/fr/istic/vv/TargetApp.java
  	
Then this :

   	src/main/java/fr/istic/vv/App.java

## Folders 
 
 **_src :_** -> main/java/fr/istic/vv
    <p>We have the runnable App.java</p>

 **_TargetProject :_** 
    <p>We use submodule from GitHub to add a targetproject in the Project</p>


 **_target :_** -> classes/fr/istic/vv 
    <p>All class create with test in file : </p>


  	src/main/java/fr/istic/vv/Mutators.java

## Fonctionnalités implémentées 

 ### Programme :
 
 Dans le projet /VV_MutationTesting/input
 
    > Création de nos classes arithmétiques (+,-,*,/)
    > Test unitaire de chaque classe arithmétique
 
  Dans le projet /VV_MutationTesting/mutation
  
    > Dans le package ../vv/mutation les mutants implémentées sont:
      
     Opération + est remplacé par -
     Opération - est remplacé par +
     Opération * est remplacé par /
     Opération / est remplacé par *
     
     Suppression des méthodes de type void