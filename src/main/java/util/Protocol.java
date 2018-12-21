package util;

public class Protocol {
    /**
     *  protocol
     *
     *  报文结构:
     *  | length | json |
     *  length段 占 4 byte，指json段的长度，以byte为单位，
     *  json段 是一个json字符串，编码为UTF-16，且必定包含type字段。
     */
    public static final int LOG_IN = 1;
    public static final int SIGN_UP = 2;
    public static final int NEW_ROOM = 3;
    public static final int ROOM_LIST = 4;
    public static final int ENTER_ROOM = 5;
    public static final int NEW_PLAYER = 6;
    public static final int ACTION = 7;
    public static final int READY = 8;
    public static final int OUT = 9;
    public static final int REMOVE_WAITING_ROOM = 10;
    public static final int CREATE_ROOM = 11;
    public static final int CHAT = 12;
    public static final int LOG_OUT = 13;
}
