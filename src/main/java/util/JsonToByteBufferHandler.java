package util;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class JsonToByteBufferHandler extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String jsonStr, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(jsonStr.length()*2+2);
        System.out.println("JsonToByteBufferHandler encode "+jsonStr);
        byteBuf.writeCharSequence(jsonStr, CharsetUtil.UTF_16);
    }
}
