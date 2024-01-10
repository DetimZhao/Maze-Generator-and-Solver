public class Cell {

    public final int x;
    public final int y;
    public boolean visited;
    public boolean rightWall = true;
    public boolean leftWall = true;
    public boolean bottomWall = true;
    public boolean topWall = true;
    public boolean solutionPath;


    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.solutionPath = false;
    }

    public boolean isVisited(){
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setRightWall(boolean open) {
        this.rightWall = !open;
        if (open) {
            this.visited = true;
        }
    }

    public void setLeftWall(boolean open) {
        this.leftWall = !open;
        if (open) {
            this.visited = true;
        }
    }

    public void setBottomWall(boolean open) {
        this.bottomWall = !open;
        if (open) {
            this.visited = true;
        }
    }

    public void setTopWall(boolean open) {
        this.topWall = !open;
        if (open) {
            this.visited = true;
        }
    }

    public void setSolutionPath(boolean solutionPath) {
        this.solutionPath = solutionPath;
    }

    public boolean hasTopWall() {
        return topWall;
    }

    public boolean hasRightWall() {
        return rightWall;
    }

    public boolean hasBottomWall() {
        return bottomWall;
    }

    public boolean hasLeftWall() {
        return leftWall;
    }

}
