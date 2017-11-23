# VV-Assign3 : How to use this project

To start, we have to compile files from TargetProject's folder.
So, compile this file :

  	TargetProject/src/main/java/fr/istic/vv/TargetApp.java

## Folders 
 
 **_src :_** -> main/test/fr/istic/vv
    <p>We have the test class MutateTests.java</p>

 **_TargetProject :_** 
    <p>We use submodule from GitHub to add a target project in the Project</p>

 **_target :_** -> classes/fr/istic/vv 
    <p>Mutated .class directory</p>

## Fonctionnalités implémentées 

 ### Programme :
 
 Dans le projet /VV-Assign3/TargetProject
 
    > Création de nos classes arithmétiques (+,-,*,/)
    > Test unitaire de chaque classe arithmétique
 
  Dans le projet /VV-Assign3
  
    > Les mutants implémentées sont:
      
     Remplacement de la valeur de retour par 0 dans les méthodes retournant un double
     
     Remplacement de la valeur de retour par TRUE ou FALSE dans les méthodes retournant un booléen
     
 ## Problèmes
 
 Lorsque on lance tous les tests de la classe MutateTests d'un coup, seul le premier test modifie le bytecode.
 Mais pas de soucis quand les tests sont lancés un par un.