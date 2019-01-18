@echo off

START java -cp "ReversiServer/" Reversi 3
TIMEOUT 2
START java -cp "ReversiRandom_Java/" RandomGuy 127.0.0.1 1
START java -cp "ReversiAI/" AI_Hard 127.0.0.1 2