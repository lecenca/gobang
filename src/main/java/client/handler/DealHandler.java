package client.handler;

import client.Client;
import client.Room;
import client.Stone;
import client.gameService.EnterRoomResult;
import client.gameService.GameService;
import client.gameService.SignUpResult;
import client.view.GameController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import sun.plugin2.message.JavaScriptBaseMessage;
import util.Protocol;

import java.io.IOException;
import java.util.ArrayList;

public class DealHandler extends SimpleChannelInboundHandler<JsonNode> {

    private Client client;
    private GameService service;
    private ObjectMapper objectMapper;

    public DealHandler(Client client, GameService service) {
        this.client = client;
        this.service = service;
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) throws Exception {
        System.out.println("DealDealHandler read " + jsonNode.toString());

        int type = jsonNode.get("type").asInt();
        if (type == Protocol.LOG_IN) {
            dealLogin(channelHandlerContext, jsonNode);
        } else if (type == Protocol.CREATE_ROOM) {
            dealCreateRoom(channelHandlerContext, jsonNode);
        }else if(type==Protocol.ROOM_LIST){
            dealRoomList(channelHandlerContext,jsonNode);
        }else if(type==Protocol.ENTER_ROOM){
            dealEnterRoom(channelHandlerContext,jsonNode);
        }else if(type==Protocol.NEW_PLAYER){
            dealNewPlayer(channelHandlerContext,jsonNode);
        }else if(type==Protocol.READY){
            dealReady(channelHandlerContext,jsonNode);
        }else if(type==Protocol.ACTION){
            dealAction(channelHandlerContext,jsonNode);
        }else if(type==Protocol.OUT){
            dealOut(channelHandlerContext,jsonNode);
        }else if(type==Protocol.REMOVE_WAITING_ROOM){
            dealRemoveWaitingRoomList(channelHandlerContext,jsonNode);
        }else if(type==Protocol.NEW_ROOM){
            dealNewRoom(channelHandlerContext,jsonNode);
        }else if(type==Protocol.SIGN_UP){
            dealSignUp(channelHandlerContext,jsonNode);
        }else if(type==Protocol.CHAT){
            dealChat(channelHandlerContext,jsonNode);
        }
    }

    private void dealChat(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Platform.runLater(()->{client.getChatBox().sendMessage(jsonNode.get("message").asText());});
    }

    private void dealSignUp(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        service.setSignUpResult(jsonNode.get("success").asBoolean());
        service.getResultBack().release();
    }

    private void dealNewRoom(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Room room = new Room();
        room.setName(jsonNode.get("name").asText());
        room.setPlayer1(jsonNode.get("player1").asText(null));
        room.setPlayer2(jsonNode.get("player2").asText(null));
        client.getLobbyController().addRoom(room);
    }

    private void dealRemoveWaitingRoomList(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Platform.runLater(()->{client.getLobbyController().removeRoom(jsonNode.get("roomName").asText());});
    }

    private void dealOut(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Platform.runLater(()->{client.getGameController().opponentOut();});
    }

    private void dealAction(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        int x = jsonNode.get("indexX").asInt();
        int y = jsonNode.get("indexY").asInt();
        Platform.runLater(()->{client.getChessBoard().anotherPlayerPlace(x, y);});
    }

    private void dealReady(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        boolean ready = jsonNode.get("ready").asBoolean();
        Platform.runLater(()->{client.getGameController().setAnotherReady(ready);});
    }

    private void dealNewPlayer(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        String playerName = jsonNode.get("newPlayer").asText();
        Platform.runLater(()->{client.getGameController().newPlayer(playerName);});
    }

    private void dealEnterRoom(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        boolean success = jsonNode.get("success").asBoolean();
        EnterRoomResult result = new EnterRoomResult();
        result.success = success;
        if(success){
            result.roomState = jsonNode.get("roomState").asInt();
        }
        service.setEnterRoomResult(result);
        service.getResultBack().release();
    }

    private void dealRoomList(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) throws IOException {
//        ArrayList<Room> roomList = ((ArrayNode)(jsonNode.get("list")));
        Room[] list = objectMapper.readValue(jsonNode.get("list").toString(),Room[].class);
        service.setWaitingRoomList(list);
        service.getResultBack().release();
    }

    private void dealCreateRoom(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Boolean success = jsonNode.get("success").asBoolean();
        service.setCreateRoomSuccess(success);
        service.getResultBack().release();
    }

    private void dealLogin(ChannelHandlerContext channelHandlerContext, JsonNode jsonNode) {
        Boolean success = jsonNode.get("success").asBoolean();
        service.setLogInSuccess(success);
        service.getResultBack().release();
    }
}
