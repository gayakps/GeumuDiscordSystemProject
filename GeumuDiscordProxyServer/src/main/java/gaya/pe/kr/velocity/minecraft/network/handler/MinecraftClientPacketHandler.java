package gaya.pe.kr.velocity.minecraft.network.handler;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsChat;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.ThreadUtil;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.PatternMatcher;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * 클라이언트 패킷을 처리 하는 공간
 */
public class MinecraftClientPacketHandler extends SimpleChannelInboundHandler<AbstractMinecraftPacket> {

    DiscordManager discordManager = DiscordManager.getInstance();
    AnswerManager answerManager = AnswerManager.getInstance();
    QuestionManager questionManager = QuestionManager.getInstance();

    ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();
    QAUserManager qaUserManager = QAUserManager.getInstance();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 예외 처리
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();
        List<AbstractOption> abstractOptionList = serverOptionManager.getAllOptions();
        sendPacket(ctx.channel(), new ServerOption(abstractOptionList));
        System.out.printf("%s Client Connection & send packet\n", ctx.channel().toString());
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
            case PLAYER_TRANSIENT_PROCEEDING_QUESTION_REQUEST: {
                //TODO 질문에 대한 답변 요청에 대한 내용
                PlayerTransientProceedingQuestionRequest playerProceedingQuestionRequest = (PlayerTransientProceedingQuestionRequest) minecraftPacket;

                /**
                 *  Process
                 * 질문이 인게임으로 들어오게 되면
                 * 1. 질문 가능 시간과 현재 최근 질문 중 가장 빠른 시간을 화인함
                 * 2. 필터링 ( 자동답변의 여부에 따라 정해짐 )
                 * 3. 최종 질문 번호를 추출함 ( DB 로 부터 추출 )
                 */

                QARequestResult qaRequestResult = questionManager.canQuestion(playerProceedingQuestionRequest);
                String message = qaRequestResult.getMessage();
                String contents = playerProceedingQuestionRequest.getContent();

                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(playerProceedingQuestionRequest.getPlayerUUID(), playerProceedingQuestionRequest.getPacketID());


                if ( qaRequestResult.getType().equals(QARequestResult.Type.FAIL) ) {
                    response.addMessage(message);
                    sendPacket(channel, response);
                    return;
                }

                // 1차 진행을 했기 때문에 Filtering 을 진행

                AnswerPatternOptions answerPatternOptions = serverOptionManager.getAnswerPatternOptions();

                ConfigOption configOption = serverOptionManager.getConfigOption();

                for (PatternMatcher patternMatcher : answerPatternOptions.getPatternMatcherList()) {
                    if ( patternMatcher.isMatch(contents) ) {
                        //TODO 자동 답변 필터링에 걸림
                        String answer = patternMatcher.getMessage();

                        response.addMessage(configOption.getAnswerSendSuccessIfQuestionerOnlineBroadcast()
                                .replace("%playername%", "&cSYSTEM")
                                .replace("%answer%", answer)
                        );

                        sendPacket(channel, response);
                        return;
                    }
                }

                // 필터링에도 걸리지 않았고 전체 질문을 진행하고자함

                int lastQuestionNumber = questionManager.getQuestionNumber();
                Question question = new Question(lastQuestionNumber, contents, qaUserManager.getUser(playerProceedingQuestionRequest.getPlayerName()) );

                ThreadUtil.schedule( ()-> {
                    questionManager.broadCastQuestion(question, qaRequestResult);
                    sendPacket(channel, response);
                });

                break;
            }
            case PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST: {
                //TODO 질문 요청
                PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = (PlayerTransientProceedingAnswerRequest) minecraftPacket;
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
