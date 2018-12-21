package client;

public class Stone {

    public static final int None = 0;
    public static final int Black = -1;
    public static final int White = 1;

    public int x;
    public int y;
    public int color;
    public int step;

    public Stone(){}

    public Stone(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = None;
        this.step = 0;
    }

    public Stone(int x, int y, int color, int step) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.step = step;
    }

}
