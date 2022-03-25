# Logipix Solver
Solving a Logipix puzzle using JAVA

# Description
The objective of this project is to create a solver for Logipix puzzles. The goal of a Logipix puzzle is to discover a hidden image using numerical clues. Each puzzle consists of a grid containing empty cells or cells with numbers. In order to solve such a puzzle, one has to connect the pairs of numbers (except the 1's), by paths whose length is equal to the value of the clues at the ends.

# Initialization
We initialize the puzzle from a text file. The first two lines give us respectively the width and the length of the puzzle. Then, each line of the text file represents a line of the puzzle knowing that the 0's represent empty cells.
For example, a text file that we can pass to our program is given by :

11

9

2 2 3 0 0 0 0 0 0 4 1 

0 1 0 0 0 0 0 0 0 1 0 

0 0 3 1 0 0 0 1 4 0 0 

0 0 0 4 0 1 0 6 0 0 0

...


# Back-Tracking (Brute Force)
The first method we are going to use to solve a logipix puzzle is based on "brute force": classical back-tracking. The idea is the following: We take the "clues" and we loop through them in ascending order. For each clue, we generate the set of all possible broken lines. We start with the first clue and choose one of the possibilities. Then we move on to the second index, do the same, making sure that this one is compatible with the previous choice and so on. If at any point we run out of possibilities, we change one of the choices we made before, and we continue. However, if we manage to go through all the clues and find the broken lines that correspond to them, the puzzle has a solution and we return true, otherwise we return false.

# Combination and Exclusion
We now consider combinations of possible partial solutions. We start by generating all possible "broken lines" corresponding to a given index. If the intersection of all these lines is non-empty, then we can conclude that the cells belonging to the intersection belong to the solution. After having successfully identified some cells belonging to the solution, some lines, which before were "possible" broken lines, are no longer compatible with the new current state. Therefore, the exclusion principle can be applied and these lines can be deleted before back-tracking.
That said, when implementing this method, we had the idea of an algorithm based on the same exclusion principle and giving faster results compared to the previous method. The idea is the following, before starting the back-tracking, we browse all the cells having a single broken line. Then, we mark their path to exclude them during the back-tracking. 
To have even more faster results, we've also changed the way we loop through the cells, instead of looping through them in ascending order, we follow the order implied by the grid (line by line from left to right).

# Probabilistic Approach
We used the same back-tracking code that we implemented previously. However, we have changed the addBrokenLines method, so that each brokenLine has a probability corresponding to the product : (distance from the cell to the cell / number of remaining steps) in order to be to classify them starting with the ones that, most likely, will be part of the solution.

# Tests
This project contains one test, allowing us to test all functions. We don't need to pass any arguments, we just need to change the path of the file we are working on ("logipix1.txt", "Man.txt", "Immensite.txt", "TeaCup.txt"), and specify which solver to use.
