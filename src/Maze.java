
public class Maze {

    private int width;
    private int height;
    private int[][] cells;

    // First constructor has the same size (w and l) for the maze
    public Maze(int size) {
        // this keyword to call the other constructor
        this(size, size);
    }

    // Second constructor if user wants to define w and l for the maze
    private Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new int[width][height];
    }

}
