package server;

import client.Room;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import server.handler.ChildChannelInitializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private Map<String,UserState> userList;
    private Map<String,Room> waitingRoomList; //Map<RoomName,Room>
    private Map<String,Room> gammingRoomList; //Map<PlayerName,Room>

    private ReentrantLock lock;

    public Server(){
        userList = new TreeMap<>();
        waitingRoomList = new TreeMap<>();
        gammingRoomList = new TreeMap<>();
        lock = new ReentrantLock();
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress("127.0.0.1", 9999))
                    .childHandler(new ChildChannelInitializer(this));

            ChannelFuture f = sb.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
    }

    public Map<String, UserState> getUserList() {
        return userList;
    }

    public Map<String, Room> getWaitingRoomList() {
        return waitingRoomList;
    }

    public Map<String, Room> getGammingRoomList() {
        return gammingRoomList;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
