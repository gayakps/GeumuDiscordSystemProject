package gaya.pe.kr.network.connection.handler;

import gaya.pe.kr.DiscordManager;
import gaya.pe.kr.network.packet.bound.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.bound.client.ServerPacketResponse;
import gaya.pe.kr.network.packet.global.MinecraftPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 클라이언트로부터 전송된 Packet Handler
 */
public class MinecraftServerPacketHandler extends SimpleChannelInboundHandler<MinecraftPacket> {

    DiscordManager discordManager = DiscordManager.getInstance();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 예외 처리
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("%s Client Connetion\n", ctx.channel().toString());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, MinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리

        System.out.printf("RECEIVED PACKET [FROM CLIENT] : %s\n", minecraftPacket.getType().name());

        switch (minecraftPacket.getType()) {
            case SERVER_PACKET_RESPONSE:
                ServerPacketResponse loginPacket = (ServerPacketResponse) minecraftPacket;
                System.out.println(loginPacket.toString());
                // 로그인 처리
                break;
            // ...
            case DISCORD_AUTHENTICATION_REQUEST: {
                DiscordAuthenticationRequest discordAuthenticationRequest = (DiscordAuthenticationRequest) minecraftPacket;
                discordManager.
            }
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }
}
