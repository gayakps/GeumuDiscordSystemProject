package gaya.pe.kr.network.connection.handler;

import gaya.pe.kr.network.packet.bound.client.ServerPacketResponse;
import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import gaya.pe.kr.network.packet.global.MinecraftPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MinecraftClientPacketHandler extends SimpleChannelInboundHandler<MinecraftPacket> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 예외 처리
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("%s Server Join\n", ctx.channel().toString());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리
        System.out.printf("RECEIVED PACKET [FROM SERVER] : %s\n", minecraftPacket.getType().name());

        Channel channel = channelHandlerContext.channel();

        switch (minecraftPacket.getType()) {

            case PLAYER_MESSAGE:
                PlayerMessage loginPacket = (PlayerMessage) minecraftPacket;
                channel.writeAndFlush(new ServerPacketResponse());
                // 로그인 처리
                break;
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }
}
