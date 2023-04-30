package gaya.pe.kr.velocity.minecraft.network.handler;

import com.velocitypowered.api.proxy.Player;
import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.non_response.BroadCastMessage;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsChat;
import gaya.pe.kr.network.packet.startDirection.server.response.PlayerRequestResponseAsClickableCommandChat;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.answer.packet.client.PlayerRecentQuestionAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.TargetPlayerAnswerRequest;
import gaya.pe.kr.qa.answer.packet.server.AnswerListResponse;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.qa.question.packet.client.TargetPlayerQuestionRequest;
import gaya.pe.kr.qa.question.packet.server.QuestionListResponse;
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
                ConfigOption configOption = serverOptionManager.getConfigOption();
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(playerRecentQuestionAnswerRequest.getPlayerUUID(), playerRecentQuestionAnswerRequest.getPacketID());

                if ( qaUserManager.existUser(playerRecentQuestionAnswerRequest.getTargetPlayerName())) {


                    //TODO 질문자가 존재하기 떄문에 최근 질문 등등을 가져와야함

                    QAUser questioner = qaUserManager.getUser(playerRecentQuestionAnswerRequest.getTargetPlayerName()); // 질문자

                    Question recentQuestion = questionManager.getTargetQAUserRecentQuestion(questioner);

                    if ( recentQuestion != null ) {

                        QAUser answerer = qaUserManager.getUser(playerRecentQuestionAnswerRequest.getPlayerName()); // 답변자

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
                        //최근 질문이 존재하지 않을 경우
                        List<Question> questionerQuestions = questionManager.getQAUserQuestions(questioner);

                        if ( !questionerQuestions.isEmpty() ) {

                            PlayerRequestResponseAsClickableCommandChat playerRequestResponseAsClickableCommandChat = new PlayerRequestResponseAsClickableCommandChat(
                                    playerRecentQuestionAnswerRequest.getPlayerUUID(),
                                    playerRecentQuestionAnswerRequest.getPacketID(),
                                    "/질문 ~~",
                                    configOption.getAnswerSendFailNotExistRecentQuestionAndRemainOldQuestion(),
                                    configOption.getAnswerSendFailNotExistRecentQuestionAndRemainOldQuestionHoverMessage()
                            );
                            sendPacket(channel, playerRequestResponseAsClickableCommandChat);

                            /**
                             *                          *   answer_send_fail_not_exist_recent_question_and_remain_old_question: '&f[&c답변&f] 해당 플레이어가 질문한지 60초가 지나 최근 질문이 존재하지 않습니다. 하지만 답변 받지 못한 질문 %remain_question%개가 남아 있습니다. (메시지 클릭 시 해당 유저의 남은 질문들을 확인합니다.)'
                             *                          *   # ↑ 답변을 했는데 이미 '최근 질문'의 만료 시간인 60초가 지나 '최근 질문'이 남아 있는 상태가 아니고 해당 유저에게 남아있는 질문이 있는 경우
                             */
                            return;
                        } else {
                            /**
                             * answer_send_fail_not_exist_recent_question_and_no_remain_old_question: '&f[&c답변&f] 해당 플레이어는 남은 질문이 없습니다!'
                             *   # ↑ 답변을 했는데 이미 '최근 질문'의 만료 시간인 60초가 지나 '최근 질문'이 남아 있는 상태가 아니고 해당 유저에게 남아 있는 질문이 없는 경우
                             */
                            response.addMessage(configOption.getAnswerSendFailNotExistRecentQuestionAndNoRemainOldQuestion());
                        }


                    }

                } else {
                    //질문자가 존재하지 않음
                    response.addMessage(configOption.getInvalidPlayerName());
                }

                sendPacket(channel, response);

                break;

            }
            case TARGET_PLAYER_ANSWER_REQUEST: {
                // 특정 플레이어 질문 목록 확인
                TargetPlayerAnswerRequest targetPlayerAnswerRequest = (TargetPlayerAnswerRequest) minecraftPacket;
                String targetPlayerName = targetPlayerAnswerRequest.getTargetPlayerName();

                Answer[] answers = null;

                if ( qaUserManager.existUser(targetPlayerName) ) {
                    QAUser targetPlayerQAUser = qaUserManager.getUser(targetPlayerName);
                    List<Answer> answerList = answerManager.getQAUserAnswers(targetPlayerQAUser);
                    answers = answerList.toArray(new Answer[0]);
                }

                AnswerListResponse answerListResponse = new AnswerListResponse(answers, targetPlayerAnswerRequest.getPlayerUUID(), targetPlayerAnswerRequest.getPacketID());
                sendPacket(channel, answerListResponse);

                break;
            }

            case TARGET_PLAYER_QUESTION_REQUEST: {

                TargetPlayerQuestionRequest targetPlayerQuestionRequest = (TargetPlayerQuestionRequest) minecraftPacket;
                String targetPlayerName = targetPlayerQuestionRequest.getTargetPlayerName();

                Question[] questionArray = null;

                if ( qaUserManager.existUser(targetPlayerName) ) {
                    QAUser targetPlayerQAUser = qaUserManager.getUser(targetPlayerName);
                    List<Question> questions = questionManager.getQAUserQuestions(targetPlayerQAUser);
                    questionArray = questions.toArray(new Question[0]);
                }

                QuestionListResponse questionListResponse = new QuestionListResponse(questionArray, targetPlayerQuestionRequest.getPlayerUUID(), targetPlayerQuestionRequest.getPacketID());
                sendPacket(channel, questionListResponse);

            // 특정 플레이어 질문 목록 확인
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
