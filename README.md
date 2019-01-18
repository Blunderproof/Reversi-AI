# Reversi-AI
AI Java program for playing Reversi, either against people or other AI agents. 




## Run a simple game

- Requires: Java 8.

- Clone the repo, for a simple game run the following in 3 different terminals:

```
java -cp "ReversiServer/" Reversi 3 
java -cp "ReversiHuman/" Human 127.0.0.1 2
java -cp "ReversiAI/" AI2 127.0.0.1 1 6
```

Windows batch files with various game settings are included for ease of use.

## Custom games/difficulty

- The "Reversi" server program is neeeded to facilitate interactions between different players although it can be ran locally. It runs with 1 mandatory parameter representing the number of minutes before the game times out.

- The "Human" program allows user input to manually test the AI and play against it. It runs with 2 mandatory parameters: `IP` and `Player Number`. 

- A number of different AIs are included with differing levels of skill. Descriptions are included below. All AI program runs with 3 mandatory parameters: `IP`, `Player Number` and `depth` representing the depth to which alpha/beta pruning is executed.

- The "Random" AI program runs 2 mandatory parameters: `IP` and `Player Number` and plays in a completely random fashion.It is included mostly for diagnostic purposes.

### Parameters

- The IP address parameter should be `127.0.0.1` if all processes are ran locally.

- The accepted player number values are 1 or 2 representing the order of play respectively.

- The tree depth variable will quickly encounter memory constrainsts at a depth of 7 or more. A higher number leads to a stronger AI.


## Windows

Have Java installed and click any of the bat files to run with that given specification.

## Mac
From the root of the directory run the following commands in terminal.
1) java -cp "ReversiServer/" Reversi {NumberMinutesOfGame}
2) java -cp "ReversiAI_1/" {AI_LEVEL} {IP} {PlayerNumber} {TreeDepth}
3) java -cp "ReversiHuman/" Human {IP} {PlayerNumber}

Note that two different human players can play together with a running server and appropriate IP addresses.