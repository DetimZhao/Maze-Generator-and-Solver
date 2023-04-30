import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Stack;

public class AppGUI extends Application {

    private static Maze maze = new Maze(1, 1);
    private int cellWidth = 1; // should be noted that this is in pixels, used later
    private boolean solutionPathDrawn = false;

    public static void main(String[] args) {
        launch(args);
    }

    /*
    Section to input w and h inspired by: https://www.tutorialspoint.com/javafx/layout_gridpane.htm
    Other important tutorial/references:
    https://jenkov.com/tutorials/javafx/your-first-javafx-application.html
    https://jenkov.com/tutorials/javafx/stage.html
    https://stackoverflow.com/questions/46053974/using-platform-exit-and-system-exitint-together
    https://jenkov.com/tutorials/javafx/scene.html
    http://www.java2s.com/example/java-api/javafx/animation/animationtimer/animationtimer-0-0.html
    https://taylorial.com/gui/fxevents/
     */

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        // Add input fields for width and height as well as labels
        Text widthLabel = new Text("Width: ");
        Text heightLabel = new Text("Height: ");
        TextField widthInput = new TextField();
        TextField heightInput = new TextField();
        Button generateButton = new Button("Generate Maze");
        gridPane.add(widthLabel, 0, 0);
        gridPane.add(widthInput, 1, 0);
        gridPane.add(heightLabel, 0, 1);
        gridPane.add(heightInput, 1, 1);
        gridPane.add(generateButton, 1, 2);

        primaryStage.setScene(new Scene(gridPane, 500, 500));
        primaryStage.setTitle("Maze Generator and Solver");
        primaryStage.show();

        // make a button that on action when pressed will generate a new maze
        generateButton.setOnAction(e -> {
            int width = Integer.parseInt(widthInput.getText());
            int height = Integer.parseInt(heightInput.getText());
            maze = new Maze(width, height); // Create new maze with desired dimensions
            cellWidth = Math.min(100, 500 / Math.max(width, height)); // Adjust cell width to fit within window
            solutionPathDrawn = false;
            Canvas canvas = new Canvas(maze.getWidth() * cellWidth, maze.getHeight() * cellWidth);
            gridPane.add(canvas, 1, 3);

            generateMaze(canvas.getGraphicsContext2D());

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    drawMaze(canvas.getGraphicsContext2D());
                    if (solutionPathDrawn) {
                        stop(); // End drawing once path is drawn otherwise it will do it multiple times
                    }
                }
            };
            timer.start();
        });

        // when you exit out of the GUI, end GUI and program
        primaryStage.setOnCloseRequest((event) -> {
            System.out.println("Closing Stage");
            Platform.exit();
            System.exit(0);
        });
    }


    // only generates the maze, doesn't draw
    public void generateMaze(GraphicsContext gc) {
        Thread thread = new Thread(() -> {
            maze.generateMaze(); // generate maze
            // check if maze is properly visited (debugging)
            for (int i = 0; i < maze.getCells().length; i++) {
                for (int j = 0; j < maze.getCells()[i].length; j++) {
                    // check if maze cells have been visited, 1 if true, else 0
                    System.out.print(maze.getCells()[i][j].isVisited() ? "1 " : "0 ");
                }
                System.out.println();
            }
            drawMaze(gc); // draw the maze with GraphicsContext
        });
        thread.start(); // start maze generation thread
    }


    // draws the maze AND the solution path
    public void drawMaze(GraphicsContext gc) {
        // Draw the background
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Draw the cells
        Cell[][] mazeCells = maze.getCells();
        double cellWidth = gc.getCanvas().getWidth() / mazeCells[0].length;
        gc.setStroke(Color.DEEPSKYBLUE);
        gc.setLineWidth(0.5);
        for (int i = 0; i < mazeCells.length; i++) {
            for (int j = 0; j < mazeCells[i].length; j++) {
                Cell currCell = mazeCells[i][j];
                double x = j * cellWidth;
                double y = i * cellWidth;
                // if cell is visited, make it white, else make it black
                gc.setFill(currCell.isVisited() ? Color.WHITE : Color.BLACK);
                gc.fillRect(x, y, cellWidth, cellWidth);

                // Draw walls
                // draw top wall
                if (currCell.hasTopWall()) {
                    gc.strokeLine(x, y, x + cellWidth, y);
                }
                // draw right wall
                if (currCell.hasRightWall()) {
                    gc.strokeLine(x + cellWidth, y, x + cellWidth, y + cellWidth);
                }
                // draw bottom wall
                if (currCell.hasBottomWall()) {
                    gc.strokeLine(x, y + cellWidth, x + cellWidth, y + cellWidth);
                }
                // draw left wall
                if (currCell.hasLeftWall()) {
                    gc.strokeLine(x, y, x, y + cellWidth);
                }


                // check if cell is the start cell and draw circle if so
                if (currCell.equals(maze.getCell(0,0))) {
                    gc.setFill(Color.FORESTGREEN);
                    gc.fillOval(x + cellWidth / 4, y + cellWidth / 4, cellWidth / 2, cellWidth / 2);
                }

                // check if cell is the final cell and draw circle if so
                if (i == maze.getHeight() - 1 && j == maze.getWidth() - 1) {
                    gc.setFill(Color.RED);
                    gc.fillOval(x + cellWidth / 4, y + cellWidth / 4, cellWidth / 2, cellWidth / 2);
                }
            }
        }

        // Draw the solution path after walls have been done
        Stack<Cell> solutionPath = maze.solveMaze();
        if (solutionPath != null) {
            solutionPathDrawn = true;
            drawSolutionPath(gc, solutionPath);
        }
    }

    // this handles drawing the solution path
    public void drawSolutionPath(GraphicsContext gc, Stack<Cell> solutionPath) {
        gc.setStroke(Color.HOTPINK);
        gc.setLineWidth(2);

        // Draw lines connecting cells in the solution path
        while (!solutionPath.empty()) {
            Cell cell = solutionPath.pop();
            // center of cell
            double x1 = cell.x * cellWidth + (double) cellWidth / 2;
            double y1 = cell.y * cellWidth + (double) cellWidth / 2;
            if (!solutionPath.empty()) {
                Cell nextCell = solutionPath.peek();
                // center of cell
                double x2 = nextCell.x * cellWidth + (double) cellWidth / 2;
                double y2 = nextCell.y * cellWidth + (double) cellWidth / 2;
                gc.strokeLine(x1, y1, x2, y2); // draw line between the center of each cell
            }
        }
        solutionPathDrawn = true;

    }

}
