`# 2048 — Java Console Edition

A clean, dependency-free implementation of the classic 2048 puzzle game, playable in any terminal. Pure JDK — no libraries, no frameworks, one source file. +------+------+------+------+ | 2| | 4| | +------+------+------+------+ | 8| 4| 2| 2| +------+------+------+------+ | 16| 8| 64| | +------+------+------+------+ | 256| 32| 8| 4| +------+------+------+------+ Score: 1180 Best: 1180 Your move:


## Features

- Classic 2048 rules — slide, merge, reach 2048 to win
- Score tracking with session-best memory
- Restart anytime (`r`) and play-again prompt on game over
- Authentic 90/10 tile spawn distribution (2s and 4s), matching the original game
- ASCII-only rendering — works identically on Windows cmd, PowerShell, macOS Terminal and Linux
- Input validation and graceful exit (safe on EOF / Ctrl+D / Ctrl+Z)
- Zero dependencies, single file, ~230 lines

## Requirements

- Java 8 or later (only `java.util` is used)

## Quick Start

```bash
git clone https://github.com/YOUR-USERNAME/2048-java.git
cd 2048-java
javac Game2048.java
java Game2048
