@echo off

START java -cp "ReversiServer/" Reversi 60
TIMEOUT 3
START java -cp "ReversiAI/" AI2 127.0.0.1 1 5
START java -cp "ReversiAI/" AI2 127.0.0.1 2 1
