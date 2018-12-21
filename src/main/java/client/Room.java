package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {

    private String name;
    private String player1; //黑棋晚间
    private String player2; //白棋玩家
    private int state;

//    private static String[] roomState = {"等待中", "准备中", "对战中"};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public StringProperty getNameProperty() {
        return new SimpleStringProperty(this.name);
    }

    public StringProperty getPlayer1Property() {
        return new SimpleStringProperty(player1==null?"等待加入":player1);
    }

    public StringProperty getPlayer2Property() {
        return new SimpleStringProperty(player2==null?"等待加入":player2);
    }

    public void enter(String userName) {
        if(player1==null)
            player1 = userName;
        else
            player2 = userName;
    }

    public static final int WAITING_1 = 1; //无人按开始
    public static final int WAITING_2 = 2; //一人按了开始
    public static final int GAMMING = 3;  //两人按了开始
    public static final int GAME_OVER = 4; //游戏结束

    public String getAnotherPlayer(String userName) {
        if(player1.equals(userName))
            return player2;
        return player1;
    }

    public void out(String userName) {
        if(player1.equals(userName))
            player1 = null;
        if(player2.equals(userName))
            player2 = null;
    }
}
