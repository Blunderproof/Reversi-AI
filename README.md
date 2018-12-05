# Reversi-AI
AI Java program for playing Reversi

## Programs

- The "Reversi" server program runs with 1 parameter representing the number of minutes before the game times out.

- The "Human" program runs with 2 parameters: `IP` and `Player Number`. 

- The "AI1" AI program runs with 3 parameters: `IP`, `Player Number` and `depth` representing the depth to which alpha/beta pruning is executed.

- The "Random" AI program runs 2 parameters: `IP` and `Player Number` and plays in a completely random fashion. Mostly for diagnostic purposes.

## Parameters

- The IP address parameter should be `127.0.0.1` if all processes are ran locally.

- The accepted player number values are 1 or 2 representing the order of play respectively.

- The tree depth variable will quickly encounter memory constrainsts at a depth of 7 or more. A higher number leads to a stronger AI.


## Windows

Have Java installed and click any of the bat files to run with that given specification.

## Mac
From the root of the directory run the following commands in terminal.
1) java -cp "ReversiServer/" Reversi {NumberMinutesOfGame}
2) java -cp "ReversiAI_1/" AI1 {IP} {PlayerNumber} {TreeDepth}
3) java -cp "ReversiHuman/" Human {IP} {PlayerNumber}

Note that two different players can play together with a running server and 2 appropriate IP addresses.
