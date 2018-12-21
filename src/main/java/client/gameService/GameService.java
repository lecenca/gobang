package client.gameService;

import client.Client;
import client.handler.GameServiceHandlerInitializer;
import client.Room;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import util.MessageGenerate;

import java.util.concurrent.Semaphore;

public class GameService {

    private NioSocketChannel socketChannel;
    private Client client;
    private boolean logInSuccess;
    private boolean createRoomSuccess;
    private Room[] waitingRoomList;
    private Semaphore resultBack;
    private EnterRoomResult enterRoomResult;
    private boolean signUpResult;

    public GameService(Client client) {
        this.client = client;
        resultBack = new Semaphore(1);
        resultBack.drainPermits();
    }

    public void init() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .remoteAddress("127.0.0.1", 9999)
                .handler(new GameServiceHandlerInitializer(client));
        try {
            ChannelFuture future = bootstrap.connect().sync();
            socketChannel = (NioSocketChannel) future.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String nameStr, String passwordStr) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.logIn(nameStr, passwordStr)).sync();
        resultBack.acquire();
        return logInSuccess;
    }

    public boolean createRoom(Room room) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.createRoom(room)).sync();
        resultBack.acquire();
        return createRoomSuccess;
    }

    public void setLogInSuccess(boolean logInResult) {
        this.logInSuccess = logInResult;
    }

    public void setCreateRoomSuccess(boolean createRoomSuccess) {
        this.createRoomSuccess = createRoomSuccess;
    }

    public void setWaitingRoomList(Room[] waitingRoomList) {
        this.waitingRoomList = waitingRoomList;
    }

    public Semaphore getResultBack() {
        return resultBack;
    }

    public boolean getSignUpResult() {
        return signUpResult;
    }

    public void setSignUpResult(boolean signUpResult) {
        this.signUpResult = signUpResult;
    }

    public Room[] requireWaitingRoomList() throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.getRoomList()).sync();
        resultBack.acquire();
        return waitingRoomList;
    }

    public EnterRoomResult enterRoom(Room room) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.enterRoom(room)).sync();
        resultBack.acquire();
        return enterRoomResult;
    }

    public void action(int x, int y) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.action(x,y)).sync();
    }

    public void ready(boolean flag) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.ready(flag)).sync();
    }

    public void out(String roomName) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.out(roomName)).sync();
    }

    public EnterRoomResult getEnterRoomResult() {
        return enterRoomResult;
    }

    public void setEnterRoomResult(EnterRoomResult enterRoomResult) {
        this.enterRoomResult = enterRoomResult;
    }

    public boolean signUp(String ac, String pw) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.signUp(ac,pw)).sync();
        resultBack.acquire();
        return signUpResult;
    }

    public void chat(String text) throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.chat(text)).sync();
    }

    public void logout() throws InterruptedException {
        socketChannel.writeAndFlush(MessageGenerate.logout()).sync();
    }
}
