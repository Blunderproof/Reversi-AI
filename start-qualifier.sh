#!/bin/bash

# COMPILE
javac ReversiServer/*.java
javac ReversiAI/*.java

# RUN
java -cp "ReversiServer/" Reversi 3 &
sleep 4
java -cp "ReversiAI/" AI3 127.0.0.1 1 6 & 
sleep 1

# then run: java -jar MCTS.jar 127.0.0.1 2