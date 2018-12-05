@echo off

START java -cp "ReversiServer/" Reversi 3
TIMEOUT 2
START java -cp "ReversiAI/" AI2 127.0.0.1 1 6
START java -cp "ReversiAI/" AI3 127.0.0.1 2 6