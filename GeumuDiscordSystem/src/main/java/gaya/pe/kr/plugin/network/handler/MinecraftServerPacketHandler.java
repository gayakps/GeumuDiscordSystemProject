package gaya.pe.kr.plugin.network.handler;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.client.ServerPacketResponse;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsChat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 서버로 부터 전송된 패킷을 처리 하는 곳
 */
public class MinecraftServerPacketHandler extends SimpleChannelInboundHandler<MinecraftPacket> {

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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리

        System.out.printf("RECEIVED PACKET [FROM SERVER] : %s\n", minecraftPacket.getType().name());

        switch (minecraftPacket.getType()) {
            case SERVER_PACKET_RESPONSE:
                ServerPacketResponse loginPacket = (ServerPacketResponse) minecraftPacket;
                System.out.println(loginPacket.toString());
                // 로그인 처리
                break;
            // ...

            case PLAYER_REQUEST_RESPONSE:
                System.out.println("리스폰스 동착");
                PlayerRequestResponseAsChat playerRequestResponseAsChat = (PlayerRequestResponseAsChat) minecraftPacket;
                Player player = Bukkit.getPlayer(playerRequestResponseAsChat.getRequestPlayerUUID());
                playerRequestResponseAsChat.sendData(player);
                
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }

}
