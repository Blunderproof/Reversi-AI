@echo off

START java -cp "ReversiServer/" Reversi 60
TIMEOUT 2
START java -cp "ReversiHuman/" Human 127.0.0.1 1
START java -cp "ReversiAI_1/" AI1 127.0.0.1 2 6
~                                                                                                  ~                                                     
