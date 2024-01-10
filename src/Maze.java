import java.util.Stack;

public class Maze {

    private int width;
    private int height;
    private Cell[][] cells;
    private Cell currCell;

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // First constructor has the same size (w and h) for the maze
    public Maze(int size) {
        this(size, size);
    }

    // Second constructor if user wants to define w and h for the maze
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        this.currCell = cells[0][0]; // initialize currCell with cells[0][0]

        // Generate the cells
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }


    // Generates maze and has calculations for how long it takes in ms
    public void generateMaze() {
        long startTimeMS = System.currentTimeMillis(); // Get the start time of the maze generation process
        System.out.printf("%d x %d Maze Generation has begun.\n", width, height);
        currCell = cells[0][0]; // Initialize currCell to the top-left cell
        generateMazeRecursively(currCell); // call recursive method

        long endTimeMS = System.currentTimeMillis(); // Get the end time of the maze generation process
        long elapsedTimeMS = Math.subtractExact(endTimeMS, startTimeMS); // Get elapsed time of maze generation
        System.out.println("\nMaze Successfully Generated");
        System.out.println("The process took " + elapsedTimeMS + " milliseconds");
    }

    // Recursive function that generates the maze
    private void generateMazeRecursively(Cell currCell) {
        currCell.setVisited(true); // should start at top-left cell at [0][0]

        Stack<Cell> unvisitedNeighbors = getUnvisitedNeighbors(currCell);

        while (!unvisitedNeighbors.isEmpty()) {
            int randIndex = (int) (Math.random() * unvisitedNeighbors.size());
            Cell randNeighbor = unvisitedNeighbors.get(randIndex); // randomly get a neighbor
            removeWalls(currCell, randNeighbor);
            generateMazeRecursively(randNeighbor);
            unvisitedNeighbors = getUnvisitedNeighbors(currCell); // update the list of unvisited neighbors after recursive call
        }
    }

    // Reset boolean visited and solution path of every cell
    private void resetVisitedCells() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j].setVisited(false);
                cells[i][j].setSolutionPath(false);
            }
        }
    }


    // get Stack of neighbor Cells that are unvisited
    public Stack<Cell> getUnvisitedNeighbors(Cell cell) {
        Stack<Cell> output = new Stack<>();

        for (int i = 0; i < 4; i++) {
            // find x-coordinate neighbor based on the current direction i and the x and y coordinates of the input Cell
            int xC = i % 2 == 0 ? cell.x : i / 2 == 0 ? cell.x + 1 : cell.x - 1;
            // find y-coordinate neighbor based on the current direction i and the x and y coordinates of the input Cell
            int yC = i % 2 != 0 ? cell.y : i / 2 == 0 ? cell.y + 1 : cell.y - 1;
            // check if neighbor at (xC, yC) is within bounds and unvisited
            if (xC >= 0 && xC < cells.length && yC >= 0 && yC < cells[xC].length && !cells[xC][yC].isVisited()) {
                output.push(cells[xC][yC]);
            }
        }
        return output;
    }

    // remove walls between adjacent cells (current cell and random neighbor)
    public void removeWalls(Cell currCell, Cell randNeighbor) {
        int xDiff = randNeighbor.x - currCell.x;
        int yDiff = randNeighbor.y - currCell.y;

        if (xDiff == 1) {
            currCell.setRightWall(false);
            randNeighbor.setLeftWall(false);
        } else if (xDiff == -1) {
            currCell.setLeftWall(false);
            randNeighbor.setRightWall(false);
        } else if (yDiff == 1) {
            currCell.setBottomWall(false);
            randNeighbor.setTopWall(false);
        } else if (yDiff == -1) {
            currCell.setTopWall(false);
            randNeighbor.setBottomWall(false);
        }
    }

    // solves the maze, calls solveMazeRecursively
    public Stack<Cell> solveMaze() {
//        System.out.println("solveMaze method is being called");
        Stack<Cell> visitedCellsStack = new Stack<>();
        visitedCellsStack.push(cells[0][0]); // add the starting cell to the stack
        cells[0][0].setVisited(true); // mark the starting cell as visited
        solveMazeRecursively(cells[0][0], visitedCellsStack);
        resetVisitedCells(); // reset the visited cells after finding the path
        return visitedCellsStack;
    }

    private Stack<Cell> solveMazeRecursively(Cell currCell, Stack<Cell> visitedCellsStack) {
        if (currCell.x == width - 1 && currCell.y == height - 1) { // if the end of the maze is reached
            return visitedCellsStack;
        }

        Stack<Cell> unvisitedNeighbors = getUnvisitedNeighbors(currCell); // list of unvisited neighbors of current cell
        while (!unvisitedNeighbors.isEmpty()) {
            int randIndex = (int) (Math.random() * unvisitedNeighbors.size());
            Cell randNeighbor = unvisitedNeighbors.get(randIndex); // get a random neighbor
            randNeighbor.setVisited(true);
            visitedCellsStack.push(randNeighbor);
            removeWalls(currCell, randNeighbor); // remove walls between current cell and chosen neighbor
            Stack<Cell> path = solveMazeRecursively(randNeighbor, visitedCellsStack); // chosen neighbor as current cell
            if (path != null) {
                for (Cell cell : path) {
                    cell.setSolutionPath(true); // solution is true in each path of the cell
                }
                return path;
            }
            visitedCellsStack.pop(); // if path is not found, pop last cell from stack and try again
            unvisitedNeighbors = getUnvisitedNeighbors(currCell); // update the list of unvisited neighbors after recursive call
        }
        // if stack is empty but end cell has not been reached, return null
        return null;
    }




}

