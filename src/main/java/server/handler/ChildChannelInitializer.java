package server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import server.Server;
import util.ByteBufferToJsonHandler;
import util.JsonToByteBufferHandler;

public class ChildChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Server server;

    public ChildChannelInitializer(Server server){
        super();
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //inbound
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
                .addLast(new ByteBufferToJsonHandler())
                .addLast(new DealHandler(server));

        //outbound
        ch.pipeline().addLast(new JsonToByteBufferHandler());
    }
}
