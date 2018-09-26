@echo off

START java -cp "ReversiServer/" Reversi 60
TIMEOUT 2
START java -cp "ReversiHuman/" Human localhost 1
START java -cp "ReversiAI_1/" AI1 localhost 2
