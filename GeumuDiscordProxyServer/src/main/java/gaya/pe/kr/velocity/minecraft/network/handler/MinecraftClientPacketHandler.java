package gaya.pe.kr.velocity.minecraft.network.handler;

import com.velocitypowered.api.proxy.Player;
import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.non_response.BroadCastMessage;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsChat;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsClickableCommandChat;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.qa.answer.packet.client.PlayerRecentQuestionAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.ThreadUtil;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.PatternMatcher;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.network.manager.NetworkManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

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

    public static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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
        channelGroup.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractMinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리
        System.out.printf("RECEIVED PACKET [FROM CLIENT] : %s\n", minecraftPacket.getType().name());

        Channel channel = channelHandlerContext.channel();

        switch (minecraftPacket.getType()) {

            case DISCORD_AUTHENTICATION_REQUEST: {
                DiscordAuthenticationRequest discordAuthenticationRequest = (DiscordAuthenticationRequest) minecraftPacket;

                ConfigOption configOption = serverOptionManager.getConfigOption();

                UUID requestPlayerUUID = discordAuthenticationRequest.getPlayerUUID();
                long packetId = discordAuthenticationRequest.getPacketID();
                String requestPlayerName = discordAuthenticationRequest.getPlayerName();

                DiscordAuthentication discordAuthentication = discordManager.getDiscordAuthentication(discordAuthenticationRequest);

                PlayerRequestResponseAsChat playerRequestResponseAsChat;

                if ( discordAuthentication != null ) {

                    if ( discordAuthentication.isExpired() ) {
                        // 만료되었을때
                        playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                                , configOption.getAuthenticationCodeExpired());
                        discordManager.removeDiscordAuthentication(requestPlayerName);

                    } else {

                        if ( discordAuthentication.isEqualCodeAndPlayerName(discordAuthenticationRequest) ) {
                            // 인증 성공할 경우
                            playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                                    , configOption.getAuthenticationSuccess());
                            discordManager.addDiscordAuthenticationUser(discordAuthentication);
                            discordManager.removeDiscordAuthentication(requestPlayerName);
                        } else {
                            // 인증 코드가 맞지 않을경우
                            playerRequestResponseAsChat = new PlayerRequestResponseAsChat(requestPlayerUUID, packetId
                                    , configOption.getAuthenticationFailAuthenticationCodeDoesNotMatch());
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
                QARequestResult qaRequestResult = questionManager.processQuestion(playerProceedingQuestionRequest);
                String message = qaRequestResult.getMessage();
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(playerProceedingQuestionRequest.getPlayerUUID(), playerProceedingQuestionRequest.getPacketID());
                response.addMessage(message);
                sendPacket(channel, response);

                break;
            }
            case PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST: {
                //TODO 질문 요청
                PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = (PlayerTransientProceedingAnswerRequest) minecraftPacket;
                QARequestResult qaRequestResult = answerManager.processAnswer(playerTransientProceedingAnswerRequest);
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(playerTransientProceedingAnswerRequest.getPlayerUUID(), playerTransientProceedingAnswerRequest.getPacketID());
                String message = qaRequestResult.getMessage();
                response.addMessage(message);
                sendPacket(channel, response);
                break;
            }
            case PLAYER_RECENT_QUESTION_ANSWER_REQUEST: {

                // 최근 답변 진행

                PlayerRecentQuestionAnswerRequest playerRecentQuestionAnswerRequest = (PlayerRecentQuestionAnswerRequest) minecraftPacket;

                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(playerRecentQuestionAnswerRequest.getPlayerUUID(), playerRecentQuestionAnswerRequest.getPacketID());

                if ( qaUserManager.existUser(playerRecentQuestionAnswerRequest.getTargetPlayerName())) {

                    ConfigOption configOption = serverOptionManager.getConfigOption();
                    //TODO 질문자가 존재하기 떄문에 최근 질문 등등을 가져와야함

                    QAUser questioner = qaUserManager.getUser(playerRecentQuestionAnswerRequest.getTargetPlayerName()); // 질문자

                    List<Question> recentQuestions = questionManager.getTargetQAUserRecentQuestion(questioner);

                    if ( !recentQuestions.isEmpty() ) {

                        QAUser answerer = qaUserManager.getUser(playerRecentQuestionAnswerRequest.getPlayerName()); // 답변자
                        Question recentQuestion = recentQuestions.get(0); // 반드시 0보다 크다

                        if ( !recentQuestion.isAnswer() ) {

                            PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = new PlayerTransientProceedingAnswerRequest(
                                    recentQuestion.getId(),
                                    playerRecentQuestionAnswerRequest.getAnswerContent(),
                                    playerRecentQuestionAnswerRequest.getPlayerName(),
                                    playerRecentQuestionAnswerRequest.getPlayerUUID()
                            );
                            QARequestResult qaRequestResult = answerManager.processAnswer(playerTransientProceedingAnswerRequest);

                            if ( qaRequestResult.getType().equals(QARequestResult.Type.SUCCESS) ) {
                                // 답변 성공

                                if ( recentQuestions.size() == 1 ) {
                                    // 답변을 했는데 이미 최근 질문으
                                } else {

                                }

                            }

                            response.addMessage(qaRequestResult.getMessage());


                        } else {
                            //TODO 최근 질문이 이미 답변이 되었다면

                            int questionableAmount = questionManager.getQuestionableAmount(questioner);

                            if ( questionableAmount <= 0 ) {
                                response.addMessage(configOption.getAnswerSendFailAlreadyAnsweredRecentQuestionAndNoRemainOldQuestion());
                                //최근질문에 답변이 되어있고 해당 유저에게 남아있는 질문이 없을 경우
                            } else {
                                //최근질문에 답변이 되어있고 해당 유저에게 남아있는 질문이 있을 경우
                                PlayerRequestResponseAsClickableCommandChat playerRequestResponseAsClickableCommandChat = new PlayerRequestResponseAsClickableCommandChat(
                                        playerRecentQuestionAnswerRequest.getPlayerUUID(),
                                        playerRecentQuestionAnswerRequest.getPacketID(),
                                        "/질문 ~~",
                                        configOption.getAnswerSendFailAlreadyAnsweredRecentQuestionAndRemainOldQuestion().replace("%remain_question%", Integer.toString(questionableAmount)),
                                        configOption.getAnswerSendFailAlreadyAnsweredRecentQuestionAndRemainOldQuestionHoverMessage()
                                );
                                sendPacket(channel, playerRequestResponseAsClickableCommandChat);
                                return;
                            }

                        }

                    } else {
                        //TODO 최근 질문이 존재하지 않을 경우
                        //answer_send_fail_already_answered_recent_question_and_no_remain_old_question:
                        response.addMessage(configOption.getAnswerSendFailNotExistRecentQuestionAndNoRemainOldQuestion());
                    }

                } else {
                    //TODO 질문자가 존재하지 않음
                }

                sendPacket(channel, response);

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

    public void sendPacket(Channel channel, AbstractMinecraftPacket... minecraftPackets) {

        VelocityThreadUtil.asyncTask( ()-> {

            for (AbstractMinecraftPacket minecraftPacket : minecraftPackets) {
                ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
                try {
                    channelFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });

    }

    public void sendPacketAllChannel(AbstractMinecraftPacket minecraftPacket) {

        VelocityThreadUtil.asyncTask( ()-> {
            for (Channel channel : channelGroup) {
                ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
                try {
                    channelFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

}
