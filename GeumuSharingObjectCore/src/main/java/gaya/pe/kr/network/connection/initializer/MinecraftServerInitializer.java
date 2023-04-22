package gaya.pe.kr.network.connection.initializer;

import gaya.pe.kr.network.connection.decoder.MinecraftLengthFieldBasedFrameDecoder;
import gaya.pe.kr.network.connection.encoder.PacketEncoder;
import gaya.pe.kr.network.connection.decoder.PacketDecoder;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

public class MinecraftServerInitializer extends ChannelInitializer<SocketChannel> {

    PacketStartDirection packetStartDirection;
    SimpleChannelInboundHandler<AbstractMinecraftPacket> simpleChannelInboundHandler;
    public MinecraftServerInitializer(PacketStartDirection packetStartDirection, SimpleChannelInboundHandler<AbstractMinecraftPacket> simpleChannelInboundHandler) {
        this.packetStartDirection = packetStartDirection;
        this.simpleChannelInboundHandler = simpleChannelInboundHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 패킷 디코더
        pipeline.addLast(new PacketEncoder(packetStartDirection));
        pipeline.addLast(new LengthFieldPrepender(2));
        pipeline.addLast(new PacketDecoder(packetStartDirection));
        pipeline.addLast(new MinecraftLengthFieldBasedFrameDecoder(packetStartDirection));
        pipeline.addLast(simpleChannelInboundHandler);


    }
}

