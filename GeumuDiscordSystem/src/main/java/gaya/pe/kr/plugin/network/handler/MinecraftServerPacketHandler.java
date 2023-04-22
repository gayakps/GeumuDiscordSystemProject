package gaya.pe.kr.plugin.network.handler;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsChat;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.question.data.Question;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * 서버로 부터 전송된 패킷을 처리 하는 곳
 */
public class MinecraftServerPacketHandler extends SimpleChannelInboundHandler<AbstractMinecraftPacket> {

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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractMinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리

        System.out.printf("RECEIVED PACKET [FROM SERVER] : %s\n", minecraftPacket.getType().name());

        switch (minecraftPacket.getType()) {


            case PLAYER_REQUEST_RESPONSE: {
                System.out.println("리스폰스 동착");
                AbstractPlayerRequestResponse abstractPlayerRequestResponse = (AbstractPlayerRequestResponse) minecraftPacket;
                Player player = Bukkit.getPlayer(abstractPlayerRequestResponse.getRequestPlayerUUID());
                abstractPlayerRequestResponse.sendData(player);
                break;
            }

            case PLAYER_REQUEST_RESPONSE_AS_OBJECT: {

                AbstractPlayerRequestResponseAsObject<?> abstractPlayerRequestResponseAsObject = (AbstractPlayerRequestResponseAsObject<?>) minecraftPacket;

                 Object tObject = abstractPlayerRequestResponseAsObject.getT();

                 Class<?> objectClazz = tObject.getClass();

                 if ( objectClazz == Answer[].class ) {
                     Answer[] answers = (Answer[]) tObject;
                 }
                 else if ( objectClazz == Question[].class ) {
                     Question[] questions = (Question[]) tObject;
                 }



                break;
            }


                
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }

}
