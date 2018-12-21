package client;

public class Board {

    public Stone[][] stones = new Stone[19][19];                       // 棋子
    public int step = 1;

    public Board() {
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                stones[i][j] = new Stone(i, j);
            }
        }
    }


    // Checks if the stones in color can be placed in the Point p.
    public int action(int x, int y, int color) {
        if (stones[x][y].color != Stone.None) {
            return Type.Action.INVALID;
        }
        return color;
    }

    // Adds a stone at the Point (x, y).
    public void add(int x, int y, int color) {
        stones[x][y].color = color;
        stones[x][y].step = step;
        ++step;
    }

    public Stone getStone(int x,int y){
        return stones[x][y];
    }

}
