package client.view;

import client.Board;
import client.Client;
import client.Stone;
import client.Type;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ChessBoard implements Initializable {
    private Board board = new Board();
    private Circle[][] stonesCircle = new Circle[19][19];
    private Label[][] steps = new Label[19][19];
    private int color = Stone.Black;
    @FXML
    private Pane chessPane;
    private Point pixel = new Point();
    private Point index = new Point();
    private AudioClip placeChessSound;

    private static final int borderGap = 25;
    private static final int stoneGap = 30;
    private static final int xLen = 18 * stoneGap + 2 * borderGap;
    private static final int yLen = 18 * stoneGap + 2 * borderGap;
    private static final int stoneRadius = (stoneGap - 0) / 2;

    private Client client;

    @FXML
    private void onClick(MouseEvent event) throws InterruptedException {
        if (client.getGameController().isBegin() && client.getGameController().getTurn() == this.color) {
            getPixelPos(event);
            int action = action();
            if (action != Type.Action.INVALID) {
//                String msg = Encoder.gameActionRequest(action, index.x, index.y);
//                Connect.send(msg);
                client.getService().action(index.x, index.y);
                playAction(action, index.x, index.y, color);
            }
        }
    }

    public void playAction(int action, int x, int y, int color) {
        place(x, y, color);
        client.getGameController().reverseTurn();
    }

    public void place(int x, int y, int color) {
        Circle stone = stonesCircle[x][y];
        Label step = steps[x][y];
        if (color == Stone.Black) {
            stone.setFill(Color.color(0.1, 0.1, 0.1));
            step.setTextFill(Color.WHITE);
        } else {
            stone.setFill(Color.color(0.97, 0.98, 0.98));
            step.setTextFill(Color.BLACK);
        }
        int px = borderGap + stoneGap * x;
        int py = borderGap + stoneGap * y;
        stone.setLayoutX(px);
        stone.setLayoutY(py);
        stone.setRadius(stoneRadius);
        stone.setEffect(new Lighting());
        if (!chessPane.getChildren().contains(stone))
            chessPane.getChildren().add(stone);
        else {
            int index = chessPane.getChildren().indexOf(stone);
            chessPane.getChildren().set(index, stone);
        }
        board.add(x, y, color);
        step.setText(Integer.toString(board.stones[x][y].step));
        step.setPrefSize(24, 12);
        step.setLayoutX(px - 12);
        step.setLayoutY(py - 8);
        step.setAlignment(Pos.BASELINE_CENTER);
        judge(x, y);
    }

    private void judge(int x, int y) {
        boolean flag = false; //false指未连成5子，true反之
        flag = flag
                ||vertical(x, y)
                ||horizontal(x,y)
                ||inclined01(x,y)
                ||inclined02(x,y);
        if(flag){
            client.getGameController().gameOver(color==client.getGameController().getTurn());
        }
    }

    private boolean vertical(int x, int y) {
        int color = board.stones[x][y].color;
        int ty = y - 4;
        if (ty < 0) ty = 0;
        int count = 0;
        for (; ty <= y + 4; ++ty) {
            if (ty >= 19)
                return false;
            if (board.stones[x][ty].color == color) {
                ++count;
            } else {
                count = 0;
            }
            if (count == 5){
                System.out.println("vertical success");
                return true;
            }
        }
        return false;
    }

    private boolean horizontal(int x, int y) {
        int color = board.stones[x][y].color;
        int tx = x - 4;
        if (tx < 0) tx = 0;
        int count = 0;
        for (; tx <= x + 4; ++tx) {
            if (tx >= 19)
                return false;
            if (board.stones[tx][y].color == color) {
                ++count;
            } else {
                count = 0;
            }
            if (count == 5){
                System.out.println("horizontal success");
                return true;
            }
        }
        return false;
    }

    private boolean inclined01(int x,int y){
        int color = board.stones[x][y].color;
        int tx = x,ty = y,sx = x,sy = y;
        int count01 = 0,count02 = 0;
        for(int i=1;i<5;++i){
            ++tx;
            ++ty;
            if(tx>=19||ty>=19)
                break;
            if(board.stones[tx][ty].color==color)
                ++count01;
            else
                break;
        }
        for(int i=1;i<5;++i){
            --sx;
            --sy;
            if(sx<0||sy<0)
                break;
            if(board.stones[sx][sy].color==color)
                ++count02;
            else
                break;
        }
        if(count01+count02+1>=5)
            return true;
        return  false;
    }

    private boolean inclined02(int x,int y){
        int color = board.stones[x][y].color;
        int tx = x,ty = y,sx = x,sy = y;
        int count01 = 0,count02 = 0;
        for(int i=1;i<5;++i){
            ++tx;
            --ty;
            if(tx>=19||ty<0)
                break;
            if(board.stones[tx][ty].color==color)
                ++count01;
            else
                break;
        }
        for(int i=1;i<5;++i){
            --sx;
            ++sy;
            if(sx<0||sy>=19)
                break;
            if(board.stones[sx][sy].color==color)
                ++count02;
            else
                break;
        }
        if(count01+count02+1>=5)
            return true;
        return  false;
    }

    public void anotherPlayerPlace(int x, int y) {
        playAction(Type.Action.PLACE, x, y, client.getGameController().getTurn());
    }

    public void setColor(int color) {
        this.color = color;
    }

    private void getPixelPos(MouseEvent event) {
        pixel.setLocation(event.getX(), event.getY());
    }

    private void getIndexPos() {
        index.setLocation((pixel.x - borderGap) / stoneGap, (pixel.y - borderGap) / stoneGap);
    }

    private int action() {
        if (pixel.x < borderGap - stoneRadius || pixel.x > xLen - borderGap + stoneRadius
                || pixel.y < borderGap - stoneRadius || pixel.y > yLen - borderGap + stoneRadius) {
            return Type.Action.INVALID;
        }
        int gridX = (pixel.x - borderGap) % stoneGap;
        int gridY = (pixel.y - borderGap) % stoneGap;
        int indexX = (pixel.x - borderGap) / stoneGap;
        int indexY = (pixel.y - borderGap) / stoneGap;
        if (gridX < stoneRadius && gridY < stoneRadius) {
            pixel.x = indexX * stoneGap + borderGap;
            pixel.y = indexY * stoneGap + borderGap;
        } else if (gridX < stoneRadius && gridY > stoneGap - stoneRadius) {
            pixel.x = indexX * stoneGap + borderGap;
            pixel.y = (indexY + 1) * stoneGap + borderGap;
        } else if (gridX > stoneGap - stoneRadius && gridY < stoneRadius) {
            pixel.x = (indexX + 1) * stoneGap + borderGap;
            pixel.y = indexY * stoneGap + borderGap;
        } else if (gridX > stoneGap - stoneRadius && gridY > stoneGap - stoneRadius) {
            pixel.x = (indexX + 1) * stoneGap + borderGap;
            pixel.y = (indexY + 1) * stoneGap + borderGap;
        } else {
            return Type.Action.INVALID;
        }
        getIndexPos();
        return board.action(index.x, index.y, client.getGameController().getTurn());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = Client.client;
        client.setChessBoard(this);
        Image boardPicture = new Image("/image/chessBoard.png");
        ImageView boardView = new ImageView(boardPicture);
        boardView.setFitWidth(590);
        boardView.setFitHeight(590);
        chessPane.getChildren().add(boardView);
        drawLine();
        drawStar();
        initStonesCircle();
        initStepsLable();
    }

    private void drawBoard() {
        Rectangle rec = new Rectangle(0, 0, xLen, yLen);
        rec.setFill(Color.rgb(249, 214, 91));
        chessPane.getChildren().add(rec);
    }

    private void drawLine() {
        Line line;
        for (int i = 0; i < 19; ++i) {
            line = new Line(borderGap, i * stoneGap + borderGap, xLen - borderGap, i * stoneGap + borderGap);
            chessPane.getChildren().add(line);
        }
        for (int i = 0; i < 19; ++i) {
            line = new Line(i * stoneGap + borderGap, borderGap, i * stoneGap + borderGap, yLen - borderGap);
            line.setStroke(Color.BLACK);
            chessPane.getChildren().add(line);
        }
    }

    private void drawStar() {
        int x = 3;
        int y;
        Circle circle;
        for (int i = 0; i < 3; ++i) {
            y = 3;
            for (int j = 0; j < 3; ++j) {
                circle = new Circle();
                circle.setFill(Color.BLACK);
                circle.setRadius(3);
                circle.setLayoutX(x * stoneGap + borderGap);
                circle.setLayoutY(y * stoneGap + borderGap);
                chessPane.getChildren().add(circle);
                y = y + 6;
            }
            x = x + 6;
        }
    }

    private void initStonesCircle() {
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                stonesCircle[i][j] = new Circle();
            }
        }
    }

    private void initStepsLable() {
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                steps[i][j] = new Label();
            }
        }
    }
}