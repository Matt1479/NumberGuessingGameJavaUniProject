# Number guessing game

## Project overview
As the project title says, it is a number guessing game. It is written in Java, and it features multiple game modes as well as single-player and (offline) multi-player.
The purpose of this project was to get familiar with Java and OOP.

## Game modes
0. Player guessing:
- The player tries to guess a number (within n tries) randomly chosen by the program.
- Feedback is provided after each guess (e.g., "too high" or "too low").
1. Reverse:
- The player selects a number, and the program tries to guess it.
- Range shrinks based on feedback (given automatically).
2. Mixed:
- Turns alternate between the player and the program.
- First to guess correctly wins.
3. Multi-player:
- Normal mode:
    - Multiple players compete to guess the (random) target number.
        - The target number is the same for all players.
    - The first to guess wins.
    - Leader perk: the leader gets 1 extra move.
    - Champion perk: Able to swap the target number once per game (to a random one), 1 in 2 chance to occurr.
- Tournament mode:
    - Players compete in structured formats like Best of 1 (BO1), Best of 3 (BO3), ..., Best of n.
    - Champion perk: the champion gets 1-3 moves (random).

## Features
- Different game modes.
- Difficulty levels: Easy, Normal, Custom.
- Player data is being saved and loaded.
- Perks.
