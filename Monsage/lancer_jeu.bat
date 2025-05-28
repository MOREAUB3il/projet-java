@echo off
chcp 65001 > nul
title Monsage
cd /d "%~dp0"

set CP=./Monsage.jar;./sqlite-jdbc-3.49.1.0.jar 

echo Lancement de Monsage...
java -Dfile.encoding=UTF-8 -cp "%CP%" jeu.Main 

REM pause