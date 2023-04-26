package gaya.pe.kr.velocity.minecraft.network.handler;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsChat;
import gaya.pe.kr.qa.answer.packet.client.PlayerProceedingAnswerRequest;
import gaya.pe.kr.qa.question.packet.client.PlayerProceedingQuestionRequest;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.OptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.checkerframework.checker.units.qual.A;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * 클라이언트 패킷을 처리 하는 공간
 */
public class MinecraftClientPacketHandler extends SimpleChannelInboundHandler<AbstractMinecraftPacket> {

    DiscordManager discordManager = DiscordManager.getInstance();
    AnswerManager answerManager = AnswerManager.getInstance();
    QuestionManager questionManager = QuestionManager.getInstance();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 예외 처리
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        OptionManager optionManager = OptionManager.getInstance();

        System.out.printf("%s Client Join\n", ctx.channel().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractMinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리
        System.out.printf("RECEIVED PACKET [FROM CLIENT] : %s\n", minecraftPacket.getType().name());

        Channel channel = channelHandlerContext.channel();

        switch (minecraftPacket.getType()) {

            case DISCORD_AUTHENTICATION_REQUEST: {
                DiscordAuthenticationRequest discordAuthenticationRequest = (DiscordAuthenticationRequest) minecraftPacket;
                System.out.println("수신 완료 " + discordAuthenticationRequest.toString());

                UUID requestPlayerUUID = discordAuthenticationRequest.getPlayerUUID();
                long packetId = discordAuthenticationRequest.getPacketID();
                String requestPlayerName = discordAuthenticationRequest.getPlayerName();

                DiscordAuthentication discordAuthentication = discordManager.getDiscordAuthentication(discordAuthenticationRequest);

                PlayerRequestResponseAsChat playerRequestResponseAsChat;

                if ( discordAuthentication != null ) {


                    if ( discordAuthentication.isExpired() ) {
                        playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                                , "이미 만료된 코드입니다 재 신청 해주세요");
                        discordManager.removeDiscordAuthentication(requestPlayerName);
                    } else {

                        if ( discordAuthentication.isEqualCodeAndPlayerName(discordAuthenticationRequest) ) {
                            playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                                    , "인증 성공");
                            discordManager.addDiscordAuthenticationUser(discordAuthentication);
                            discordManager.removeDiscordAuthentication(requestPlayerName);
                        } else {
                            playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                                    , "틀린 인증번호 입니다");
                        }
                    }

                } else {
                    playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                            , "인증을 요청하시기 바랍니다");
                }

                sendPacket(channel, playerRequestResponseAsChat);

                break;

            }
            case PLAYER_PROCEEDING_QUESTION_REQUEST: {
                //TODO 질문에 대한 답변 요청에 대한 내용

                PlayerProceedingQuestionRequest playerProceedingQuestionRequest = (PlayerProceedingQuestionRequest) minecraftPacket;

                break;
            }
            case PLAYER_PROCEEDING_ANSWER_REQUEST: {
                //TODO 질문 요청
                PlayerProceedingAnswerRequest playerProceedingAnswerRequest = (PlayerProceedingAnswerRequest) minecraftPacket;
                break;
            }
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }

    public void sendPacket(Channel channel, AbstractMinecraftPacket minecraftPacket) {

        VelocityThreadUtil.asyncTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
            try {
                channelFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
