package client.handler;

import client.Client;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import util.ByteBufferToJsonHandler;
import util.JsonToByteBufferHandler;


public class GameServiceHandlerInitializer extends ChannelInitializer<SocketChannel> {
    
    private Client client;

    public GameServiceHandlerInitializer(Client client) {
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        //inbound
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
                .addLast(new ByteBufferToJsonHandler())
                .addLast(new DealHandler(client,client.getService()));

        //outbound
        pipeline.addLast(new JsonToByteBufferHandler());
    }
}
