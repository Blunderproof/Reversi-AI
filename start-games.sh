#!/bin/bash

# COMPILE
javac ReversiServer/*.java
javac ReversiAI/*.java

# RUN
java -cp "ReversiServer/" Reversi 60 &
sleep 4
java -cp "ReversiAI/" AI2 127.0.0.1 1 6 & 
sleep 1
java -cp "ReversiAI/" AI1 127.0.0.1 2 6 &
