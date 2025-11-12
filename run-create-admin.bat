@echo off
REM Exécute l’outil de création d’admin (sans JavaFX)
set CLASSPATH=target/classes;C:/Users/Matthieu/.m2/repository/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar;C:/Users/Matthieu/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar
java -cp "%CLASSPATH" fr.hockey.tools.CreateAdminTool