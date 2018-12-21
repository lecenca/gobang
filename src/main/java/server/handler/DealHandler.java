package server.handler;

import client.Room;
import com.fasterxml.jackson.databind.JsonNode;
import dao.NamePasswordDao;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import server.Server;
import server.UserState;
import util.MessageGenerate;
import util.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DealHandler extends SimpleChannelInboundHandler<JsonNode> {

    private Server server;
    private NamePasswordDao namePasswordDao;

    private String userName;
    private String roomName;

    public DealHandler(Server server) {
        super();
        this.server = server;
        try {
            InputStream cfgFile = Resources.getResourceAsStream("mybatis.xml");
            SqlSession session = new SqlSessionFactoryBuilder().build(cfgFile).openSession();
            namePasswordDao = session.getMapper(NamePasswordDao.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) throws Exception {
        System.out.println("DealHandler read " + jsonNode.toString());
        int type = jsonNode.get("type").asInt();
        if (type == Protocol.LOG_IN) {
            dealLogIn(channelHandlerContext, jsonNode);
        } else if (type == Protocol.SIGN_UP) {
            dealSignUp(channelHandlerContext,jsonNode);
        } else if (type == Protocol.ROOM_LIST) {
            dealRoomList(channelHandlerContext, jsonNode);
        } else if (type == Protocol.ENTER_ROOM) {
            dealEnterRoom(channelHandlerContext, jsonNode);
        } else if (type == Protocol.READY) {
            dealReady(channelHandlerContext, jsonNode);
        } else if (type == Protocol.ACTION) {
            dealAction(channelHandlerContext, jsonNode);
        } else if (type == Protocol.OUT) {
            dealOut(channelHandlerContext, jsonNode);
        } else if (type == Protocol.CREATE_ROOM) {
            dealCreateRoom(channelHandlerContext, jsonNode);
        }else if(type==Protocol.CHAT){
            dealChat(channelHandlerContext,jsonNode);
        }else if(type==Protocol.LOG_OUT){
            dealLogOut(channelHandlerContext,jsonNode);
        }
    }

    private void dealLogOut(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        server.getUserList().remove(userName);
    }

    private void dealChat(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Room room = server.getGammingRoomList().get(userName);
        String anotherPlayer = room.getAnotherPlayer(userName);
        NioSocketChannel socketChannel = server.getUserList().get(anotherPlayer).getChannel();
        socketChannel.writeAndFlush(jsonNode.toString());
    }

    private void dealSignUp(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        String ac = jsonNode.get("account").asText();
        String pw = jsonNode.get("password").asText();
        String password02 = namePasswordDao.getPassword(ac);
        if(password02!=null){
            channelHandlerContext.channel().writeAndFlush(MessageGenerate.signUpSuccess(false));
            return;
        }
        namePasswordDao.insertAccount(ac,pw);
        channelHandlerContext.channel().writeAndFlush(MessageGenerate.signUpSuccess(true));
    }

    private void dealOut(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        //从房间里退出：
        //1、若房间只有一人，则退出后，房间也从等待房间表中除去。
        //2、若房间有两人，且未开始游戏，则一人退出后，房间从游戏房间表
        //   中除去，并添加到等待房间表中。（两边都按准备才算开始）
        //3、若房间有两人，且已开始游戏，则一人退出后，另一人直接判断胜利，且
        //   将房间从游戏房间表移除。
        server.getLock().lock();
        try {
            Map<String, Room> gamingRoomList = server.getGammingRoomList();
            Map<String, Room> waitingRoomList = server.getWaitingRoomList();
            Map<String, UserState> userList = server.getUserList();
            Room room = gamingRoomList.get(userName);
            String roomName = jsonNode.get("roomName").asText();
            if (room == null)
                room = waitingRoomList.get(roomName);
            String player1 = room.getPlayer1();
            String player2 = room.getPlayer2();
            if (player1 == null || player2 == null) {
                //情况1
                waitingRoomList.remove(roomName);
                userList.get(userName).setState(UserState.LOBBY_FINNISH);
                broadcast(MessageGenerate.removeWaitingRoom(roomName));
                return;
            }
            NioSocketChannel anotherPlayerChannel = server
                    .getUserList()
                    .get(room.getAnotherPlayer(userName))
                    .getChannel();
            if (room.getState() == Room.GAMMING) {
                //情况3
                gamingRoomList.remove(player1);
                gamingRoomList.remove(player2);
                userList.get(player1).setState(UserState.LOBBY_FINNISH);
                userList.get(player2).setState(UserState.LOBBY_FINNISH);
            } else {
                //情况2
                gamingRoomList.remove(userName);
                gamingRoomList.remove(room.getAnotherPlayer(userName));
                room.out(userName);
                if (room.getState() == Room.WAITING_2 &&
                        userList.get(userName).getState() == UserState.READY)
                    room.setState(Room.WAITING_1);
                userList.get(userName).setState(UserState.LOBBY_FINNISH);
                waitingRoomList.put(room.getName(), room);
                broadcast(MessageGenerate.newRoom(room));
            }
            anotherPlayerChannel.writeAndFlush(jsonNode.toString());
        } finally {
            server.getLock().unlock();
        }
    }

    private void dealAction(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        server.getLock().lock();
        try {
            Room room = server.getGammingRoomList().get(userName);
            String anotherPlayer = room.getAnotherPlayer(userName);
            server.getUserList().get(anotherPlayer).getChannel().writeAndFlush(jsonNode.toString());
        } finally {
            server.getLock().unlock();
        }
    }

    private void dealReady(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        server.getLock().lock();
        try {
            boolean ready = jsonNode.get("ready").asBoolean();
            Room room = server.getGammingRoomList().get(userName);
            if (room != null) {
                String player = room.getAnotherPlayer(userName);
                NioSocketChannel channel = server.getUserList().get(player).getChannel();
                channel.writeAndFlush(MessageGenerate.ready(ready));
            } else {
                room = server.getWaitingRoomList().get(roomName);
            }
            if (ready) {
                room.setState(room.getState() + 1);
                server.getUserList().get(userName).setState(UserState.READY);
            } else {
                room.setState(room.getState() - 1);
                server.getUserList().get(userName).setState(UserState.GAMMING_ROOM);
            }
        } finally {
            server.getLock().unlock();
        }
    }

    private void dealEnterRoom(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        server.getLock().lock();
        try {
            String roomName = jsonNode.get("room").asText();
            Map<String, Room> waitingRoomList = server.getWaitingRoomList();
            Room room = waitingRoomList.get(roomName);
            if (room == null) {
                channelHandlerContext.channel().writeAndFlush(MessageGenerate.enterRoomFalse());
                return;
            }
            waitingRoomList.remove(roomName);
            //通知另一个玩家他有新玩家加入
            if (room.getPlayer1() != null) {
                server.getUserList()
                        .get(room.getPlayer1())
                        .getChannel()
                        .writeAndFlush(MessageGenerate.newPlayer(userName));
            }
            if (room.getPlayer2() != null) {
                server.getUserList()
                        .get(room.getPlayer2())
                        .getChannel()
                        .writeAndFlush(MessageGenerate.newPlayer(userName));
            }
            room.enter(userName);
            this.roomName = room.getName();
            server.getGammingRoomList().put(room.getPlayer1(), room);
            server.getGammingRoomList().put(room.getPlayer2(), room);
            server.getUserList().get(userName).setState(UserState.GAMMING_ROOM);
            channelHandlerContext.channel().writeAndFlush(MessageGenerate.enterRoomSuccess(room));
            broadcast(MessageGenerate.removeWaitingRoom(roomName));
        } finally {
            server.getLock().unlock();
        }
    }

    private void broadcast(String msg) {
        for (UserState userState : server.getUserList().values()) {
            if (userState.getState() == UserState.LOBBY_FINNISH) {
                userState.getChannel().writeAndFlush(msg);
            }
        }
    }

    private void dealRoomList(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        server.getLock().lock();
        channelHandlerContext.channel().writeAndFlush(
                MessageGenerate.roomList(server.getWaitingRoomList()));
        Map<String, UserState> userList = server.getUserList();
        userList.get(userName).setState(UserState.LOBBY_FINNISH);
        server.getLock().unlock();
    }

    private void dealCreateRoom(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        server.getLock().lock();
        Room room = new Room();
        room.setName(jsonNode.get("name").asText());
        room.setPlayer1(jsonNode.get("player1").asText(null));
        room.setPlayer2(jsonNode.get("player2").asText(null));
        room.setState(Room.WAITING_1);

        Map<String, Room> waitingRoomList = server.getWaitingRoomList();
        if (waitingRoomList.get(room.getName()) == null) {
            waitingRoomList.put(room.getName(), room);
            roomName = room.getName();
            channelHandlerContext.channel().writeAndFlush(MessageGenerate.createRoomSuccess(true));

            Map<String, UserState> userList = server.getUserList();
            userList.get(userName).setState(UserState.GAMMING_ROOM);
//            String jsonStr = MessageGenerate.newRoom(room);
//            for (UserState userState : userList.values()) {
//                if(userState.getChannel()!=channelHandlerContext.channel() &&
//                        userState.getState()==UserState.LOBBY_FINNISH)
//                    userState.getChannel().writeAndFlush(jsonStr);
//            }
            broadcast(MessageGenerate.newRoom(room));
        } else {
            channelHandlerContext.writeAndFlush(MessageGenerate.createRoomSuccess(false));
        }
        server.getLock().unlock();
    }

    private void dealLogIn(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        String name = jsonNode.get("name").asText();
        String password = jsonNode.get("password").asText();

        if(server.getUserList().get(name)!=null){
            channelHandlerContext.channel().writeAndFlush(MessageGenerate.logInSuccess(false));
            return;
        }

        String password02 = namePasswordDao.getPassword(name);
        if (password02 != null && password02.equals(password)) {
            server.getLock().lock();
            UserState userState = new UserState(UserState.LOBBY_BEFORE, (NioSocketChannel) channelHandlerContext.channel());
            server.getUserList().put(name, userState);
            userName = name;
            channelHandlerContext.channel().writeAndFlush(MessageGenerate.logInSuccess(true));
            server.getLock().unlock();
        } else {
            channelHandlerContext.channel().writeAndFlush(MessageGenerate.logInSuccess(false));
        }
    }


}
