package gaya.pe.kr.network.connection.initializer;

import gaya.pe.kr.network.connection.decoder.MinecraftLengthFieldBasedFrameDecoder;
import gaya.pe.kr.network.connection.encoder.PacketEncoder;
import gaya.pe.kr.network.connection.decoder.PacketDecoder;
import gaya.pe.kr.network.connection.handler.MinecraftPacketClientHandler;
import gaya.pe.kr.network.connection.handler.MinecraftPacketServerHandler;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

public class MinecraftServerInitializer extends ChannelInitializer<SocketChannel> {

    PacketStartDirection packetStartDirection;

    public MinecraftServerInitializer(PacketStartDirection packetStartDirection) {
        this.packetStartDirection = packetStartDirection;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 패킷 디코더
        pipeline.addLast(new PacketDecoder());
        pipeline.addLast(new PacketEncoder());
        pipeline.addLast(new LengthFieldPrepender(2));
        pipeline.addLast(new MinecraftLengthFieldBasedFrameDecoder());

        // 패킷 핸들러

        if ( packetStartDirection.equals(PacketStartDirection.SERVER) ) {
            pipeline.addLast(new MinecraftPacketServerHandler());
        } else {
            pipeline.addLast(new MinecraftPacketClientHandler());
        }


    }
}

