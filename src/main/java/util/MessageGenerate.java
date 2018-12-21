package util;

import client.Room;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class MessageGenerate {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String logInSuccess(boolean flag){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.LOG_IN);
        objectNode.put("success",flag);
        return objectNode.toString();
    }

    public static String signUpSuccess(boolean flag){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.SIGN_UP);
        objectNode.put("success",flag);
        return objectNode.toString();
    }

    public static String logIn(String name,String password){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.LOG_IN);
        objectNode.put("name",name);
        objectNode.put("password",password);
        return objectNode.toString();
    }

    public static String newRoom(Room room){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.NEW_ROOM);
        objectNode.put("name",room.getName());
        objectNode.put("player1",room.getPlayer1());
        objectNode.put("player2",room.getPlayer2());
        return objectNode.toString();
    }

    public static String createRoomSuccess(boolean success){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.CREATE_ROOM);
        objectNode.put("success",success);
        return objectNode.toString();
    }

    public static String roomList(Map<String,Room> waitingRoomList) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.ROOM_LIST);
        objectNode.putArray("list");
        ArrayNode arrayNode = (ArrayNode) objectNode.get("list");
        ObjectNode node;
        for(Room room: waitingRoomList.values()){
            node = objectMapper.createObjectNode();
            node.put("name",room.getName());
            node.put("player1",room.getPlayer1());
            node.put("player2",room.getPlayer2());
            arrayNode.add(node);
        }
        return objectNode.toString();
    }

    public static String getRoomList() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.ROOM_LIST);
        return objectNode.toString();
    }

    public static String enterRoom(Room room) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.ENTER_ROOM);
        objectNode.put("room",room.getName());
        return objectNode.toString();
    }

    public static String enterRoomSuccess(Room room) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.ENTER_ROOM);
        objectNode.put("success",true);
        objectNode.put("roomState",room.getState());
        return objectNode.toString();
    }

    public static String newPlayer(String player) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.NEW_PLAYER);
        objectNode.put("newPlayer",player);
        return objectNode.toString();
    }

    public static String action(int x, int y) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.ACTION);
        objectNode.put("indexX",x);
        objectNode.put("indexY",y);
        return objectNode.toString();
    }

    public static String ready(boolean flag) {
        //flag为true为准备，false为取消准备
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.READY);
        objectNode.put("ready",flag);
        return objectNode.toString();
    }

    public static String out(String roomName) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.OUT);
        objectNode.put("roomName",roomName);
        return objectNode.toString();
    }

    public static String removeWaitingRoom(String name) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.REMOVE_WAITING_ROOM);
        objectNode.put("roomName",name);
        return objectNode.toString();
    }

    public static Object createRoom(Room room) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.CREATE_ROOM);
        objectNode.put("name",room.getName());
        objectNode.put("player1",room.getPlayer1());
        objectNode.put("player2",room.getPlayer2());
        return objectNode.toString();
    }

    public static Object enterRoomFalse() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.CREATE_ROOM);
        objectNode.put("success",false);
        return objectNode.toString();
    }

    public static Object signUp(String ac, String pw) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.SIGN_UP);
        objectNode.put("account",ac);
        objectNode.put("password",pw);
        return objectNode.toString();
    }

    public static Object chat(String text) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.CHAT);
        objectNode.put("message",text);
        return objectNode.toString();
    }

    public static Object logout() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("type",Protocol.LOG_OUT);
        return objectNode.toString();
    }
}
