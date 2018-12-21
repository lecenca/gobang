package server;

import io.netty.channel.socket.nio.NioSocketChannel;

public class UserState {
    private NioSocketChannel channel;
    private int state;

    public UserState(int state, NioSocketChannel channel) {
        this.state = state;
        this.channel = channel;
    }

    public NioSocketChannel getChannel() {
        return channel;
    }

    public void setChannel(NioSocketChannel channel) {
        this.channel = channel;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static final int LOBBY_BEFORE = 1; //玩家已登录但大厅未初始化完毕
    public static final int LOBBY_FINNISH = 2; //大厅初始化完毕
    public static final int GAMMING_ROOM = 3;  //玩家已进入对弈界面
    public static final int READY = 4;//玩家已按准备
}
