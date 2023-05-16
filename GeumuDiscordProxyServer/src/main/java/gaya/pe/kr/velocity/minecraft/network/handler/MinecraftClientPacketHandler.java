package gaya.pe.kr.velocity.minecraft.network.handler;

import gaya.pe.kr.network.packet.startDirection.client.*;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.non_response.ScatterServerPlayers;
import gaya.pe.kr.network.packet.startDirection.server.non_response.TargetPlayerChat;
import gaya.pe.kr.network.packet.startDirection.server.response.*;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.answer.packet.client.*;
import gaya.pe.kr.qa.answer.packet.server.AnswerListResponse;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.*;
import gaya.pe.kr.qa.packet.server.BukkitAnswerModify;
import gaya.pe.kr.qa.packet.server.BukkitQuestionModify;
import gaya.pe.kr.qa.packet.server.QAUserResponse;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.qa.question.packet.client.QuestionModifyRequest;
import gaya.pe.kr.qa.question.packet.client.TargetPlayerGetQuestionRequest;
import gaya.pe.kr.qa.question.packet.client.TargetQuestionRemoveRequest;
import gaya.pe.kr.qa.question.packet.server.QuestionListResponse;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.data.DiscordAuthentication;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.player.PlayerListHandler;
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
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashSet;
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

    public MinecraftClientPacketHandler() {
        System.out.println("MinecraftClientPacketHandler 가 추가되었습니다");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 예외 처리
        cause.printStackTrace();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String ip = channel.remoteAddress().toString();
        try {
            channelGroup.remove(channel);
            PlayerListHandler.removeChannel(channel);
            System.out.printf("Channel : %s [IP : %s] Inactive", channel.toString(), ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();

        ServerOptionManager serverOptionManager = ServerOptionManager.getInstance();
        List<AbstractOption> abstractOptionList = serverOptionManager.getAllOptions();
        sendPacket(channel, new ServerOption(abstractOptionList));

        VelocityThreadUtil.delayTask(()-> {
            BukkitQuestionModify bukkitQuestionModify = new BukkitQuestionModify( QAModifyType.ADD, questionManager.getAllQuestions().toArray(new Question[0]));
            sendPacket(channel, bukkitQuestionModify);
        }, 1500);


        VelocityThreadUtil.delayTask(()-> {
            BukkitAnswerModify bukkitAnswerModify = new BukkitAnswerModify(QAModifyType.ADD, answerManager.getAllAnswers().toArray(new Answer[0]));
            sendPacket(channel, bukkitAnswerModify);
        }, 3000);


        System.out.printf("%s Client Connection & send packet\n", channel.toString());

        channelGroup.add(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractMinecraftPacket minecraftPacket) throws Exception {
        // 패킷 타입에 따라 분기 처리
        System.out.printf("RECEIVED PACKET [FROM CLIENT] : %s\n", minecraftPacket.getType().name());

        Channel channel = channelHandlerContext.channel();

        serverOptionManager = ServerOptionManager.getInstance();

        switch (minecraftPacket.getType()) {

            case DISCORD_AUTHENTICATION_REQUEST: {
                // 버킷 서버로 부터 인증 요청이 왔을 경우
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
                            , "인증을 요청하시길 바랍니다");
                }

                sendPacket(channel, playerRequestResponseAsChat);

                break;

            }
            case PLAYER_TRANSIENT_PROCEEDING_QUESTION_REQUEST: {
                PlayerTransientProceedingQuestionRequest playerProceedingQuestionRequest = (PlayerTransientProceedingQuestionRequest) minecraftPacket;
                QARequestResult qaRequestResult = questionManager.processQuestion(playerProceedingQuestionRequest);
                String message = qaRequestResult.getMessage();
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(playerProceedingQuestionRequest.getPlayerUUID(), playerProceedingQuestionRequest.getPacketID());
                response.addMessage(message);
                sendPacket(channel, response);
                break;
            }
            case PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST: {
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
                            response.addMessage(qaRequestResult.getMessage());

                        } else {

                            int questionableAmount = questionManager.getQuestionableAmount(questioner);

                            if ( questionableAmount <= 0 ) {
                                response.addMessage(configOption.getAnswerSendFailAlreadyAnsweredRecentQuestionAndNoRemainOldQuestion());
                                //최근질문에 답변이 되어있고 해당 유저에게 남아있는 질문이 없을 경우
                            } else {
                                //최근질문에 답변이 되어있고 해당 유저에게 남아있는 질문이 있을 경우
                                PlayerRequestResponseAsClickableCommandChat playerRequestResponseAsClickableCommandChat = new PlayerRequestResponseAsClickableCommandChat(
                                        playerRecentQuestionAnswerRequest.getPlayerUUID(),
                                        playerRecentQuestionAnswerRequest.getPacketID(),
                                        "/질문 목록 "+questioner.getGamePlayerName(),
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
                                    "/질문 목록 "+questioner.getGamePlayerName(),
                                    configOption.getAnswerSendFailNotExistRecentQuestionAndRemainOldQuestion().replace("%remain_question%", Integer.toString(questionerQuestions.size())),
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
            case GET_TARGET_PLAYER_ANSWER_REQUEST: {
                // 특정 플레이어 질문 목록 확인
                TargetPlayerGetAnswerRequest targetPlayerGetAnswerRequest = (TargetPlayerGetAnswerRequest) minecraftPacket;
                String targetPlayerName = targetPlayerGetAnswerRequest.getTargetPlayerName();

                Answer[] answers = null;

                if ( qaUserManager.existUser(targetPlayerName) ) {
                    QAUser targetPlayerQAUser = qaUserManager.getUser(targetPlayerName);
                    List<Answer> answerList = answerManager.getQAUserAnswers(targetPlayerQAUser);
                    answers = answerList.toArray(new Answer[0]);
                }

                AnswerListResponse answerListResponse = new AnswerListResponse(answers, targetPlayerGetAnswerRequest.getPlayerUUID(), targetPlayerGetAnswerRequest.getPacketID());
                sendPacket(channel, answerListResponse);

                break;
            }
            case GET_TARGET_PLAYER_QUESTION_REQUEST: {

                TargetPlayerGetQuestionRequest targetPlayerGetQuestionRequest = (TargetPlayerGetQuestionRequest) minecraftPacket;
                String targetPlayerName = targetPlayerGetQuestionRequest.getTargetPlayerName();

                Question[] questionArray = null;

                if ( qaUserManager.existUser(targetPlayerName) ) {
                    QAUser targetPlayerQAUser = qaUserManager.getUser(targetPlayerName);
                    List<Question> questions = questionManager.getQAUserQuestions(targetPlayerQAUser);
                    questionArray = questions.toArray(new Question[0]);
                }

                QuestionListResponse questionListResponse = new QuestionListResponse(questionArray, targetPlayerGetQuestionRequest.getPlayerUUID(), targetPlayerGetQuestionRequest.getPacketID());
                sendPacket(channel, questionListResponse);

                break;

            // 특정 플레이어 질문 목록 확인
            }
            case DISCORD_AUTHENTICATION_USER_CONFIRM_REQUEST: {
                DiscordAuthenticationUserConfirmRequest discordAuthenticationUserConfirmRequest = (DiscordAuthenticationUserConfirmRequest) minecraftPacket;
                boolean exist = qaUserManager.existUser(discordAuthenticationUserConfirmRequest.getTargetPlayerName());
                RequestResponse response = new RequestResponse(exist, discordAuthenticationUserConfirmRequest);
                sendPacket(channel, response);
                break;
            }
            case UPDATE_PLAYER_LIST_REQUEST: {
                UpdatePlayerList updatePlayerList = (UpdatePlayerList) minecraftPacket;
                PlayerListHandler.setChannelAsPlayerList(channel,updatePlayerList.getPlayerList());
                sendPacketAllChannel(new ScatterServerPlayers(PlayerListHandler.getAllConnectionPlayers())); // 전체 서버로 전송
                break;
            }
            case ALL_QA_USER_DATA_REQUEST: {
                AllQAUserDataRequest allQAUserDataRequest = (AllQAUserDataRequest) minecraftPacket;
                HashSet<QAUser> qaUsers = qaUserManager.getAllQAUsers();
                QAUserResponse qaUserResponse = new QAUserResponse( qaUsers.isEmpty() ? null : qaUsers.toArray(new QAUser[0])
                        , allQAUserDataRequest.getPlayerUUID(), allQAUserDataRequest.getPacketID()
                );
                sendPacket(channel, qaUserResponse);
                break;
            }
            case TARGET_QA_USER_DATA_REQUEST: {
                TargetQAUserDataRequest targetQAUserDataRequest = (TargetQAUserDataRequest) minecraftPacket;

                List<QAUser> qaUsers = new ArrayList<>();

                for (String targetQAUser : targetQAUserDataRequest.getTargetQAUsers()) {

                    boolean exist = qaUserManager.existUser(targetQAUser);

                    if ( exist ) {
                        qaUsers.add(qaUserManager.getUser(targetQAUser));
                    }
                    else {
                        if (targetQAUserDataRequest.isCreatedAndReturn()) {
                            qaUsers.add(qaUserManager.getUser(targetQAUser));
                        }
                    }
                }

                QAUserResponse qaUserResponse = new QAUserResponse( qaUsers.isEmpty() ? null : qaUsers.toArray(new QAUser[0]) , targetQAUserDataRequest.getPlayerUUID(), targetQAUserDataRequest.getPacketID());
                sendPacket(channel, qaUserResponse);

                break;
            }
            case TARGET_ANSWER_BY_QUESTION_ID_REMOVE_REQUEST: {

                TargetAnswerByQuestionIdRemoveRequest targetAnswerByQuestionIdRemoveRequest = (TargetAnswerByQuestionIdRemoveRequest) minecraftPacket;

                long questId = targetAnswerByQuestionIdRemoveRequest.getQuestId();

                ConfigOption configOption = serverOptionManager.getConfigOption();
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(targetAnswerByQuestionIdRemoveRequest.getPlayerUUID(), targetAnswerByQuestionIdRemoveRequest.getPacketID());

                if ( answerManager.existAnswerByQuestId(questId) ) {
                    Answer removeAnswer = answerManager.removeByQuestId(questId);

                    response.addMessage(configOption.getRemoveASuccessRemovePerson().replace("%question_number%", Long.toString(questId)));
                    QAUser removeAnswerQAUser = removeAnswer.getAnswerPlayer();
                    Channel targetPlayerChannel = PlayerListHandler.getPlayerAsChannel(removeAnswerQAUser.getGamePlayerName());

                    if (targetPlayerChannel != null) {
                        TargetPlayerChat targetPlayerChat = new TargetPlayerChat(removeAnswerQAUser.getGamePlayerName(),
                                configOption.getRemoveASuccessHasBeenRemovedPerson()
                                        .replace("%playername%", targetAnswerByQuestionIdRemoveRequest.getPlayerName())
                                        .replace("%question_number%", Long.toString(questId))
                        );
                        sendPacket(targetPlayerChannel, targetPlayerChat); // 특정 플레이어에게 데이터 전송
                    }

                } else {
                    response.addMessage(configOption.getRemoveAFailNotExist());
                }

                sendPacket(channel, response);


                break;
            }
            case TARGET_QUESTION_REMOVE_REQUEST: {
                TargetQuestionRemoveRequest targetQuestionRemoveRequest = (TargetQuestionRemoveRequest) minecraftPacket;

                long questId = targetQuestionRemoveRequest.getQuestId();

                ConfigOption configOption = serverOptionManager.getConfigOption();
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(targetQuestionRemoveRequest.getPlayerUUID(), targetQuestionRemoveRequest.getPacketID());

                if ( questionManager.existQuest(questId) ) {
                    Question removeQuestion = questionManager.removeQuestionByQuestId(questId);

                    response.addMessage(configOption.getRemoveQSuccessRemovePerson().replace("%question_number%", Long.toString(questId)));

                    QAUser removeQuestionQAUser = removeQuestion.getQaUser();

                    Channel targetPlayerChannel = PlayerListHandler.getPlayerAsChannel(removeQuestion.getQaUser().getGamePlayerName());

                    if (targetPlayerChannel != null) {
                        TargetPlayerChat targetPlayerChat = new TargetPlayerChat(removeQuestionQAUser.getGamePlayerName(),
                                configOption.getRemoveQSuccessHasBeenRemovedPerson()
                                        .replace("%playername%", targetQuestionRemoveRequest.getPlayerName())
                                        .replace("%question_number%", Long.toString(questId))
                        );
                        sendPacket(targetPlayerChannel, targetPlayerChat); // 특정 플레이어에게 데이터 전송
                    }

                } else {
                    response.addMessage(configOption.getInvalidQuestionNumber());
                }

                sendPacket(channel, response);

                break;
            }
            case TARGET_PLAYER_REMOVE_REWARD_REQUEST: {

                TargetPlayerRemoveRewardRequest targetPlayerRemoveRewardRequest = (TargetPlayerRemoveRewardRequest) minecraftPacket;
                ConfigOption configOption = serverOptionManager.getConfigOption();
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(targetPlayerRemoveRewardRequest.getPlayerUUID(), targetPlayerRemoveRewardRequest.getPacketID());
                String targetPlayerName = targetPlayerRemoveRewardRequest.getTargetPlayerName();

                if ( qaUserManager.existUser(targetPlayerName) ) {

                    QAUser qaUser = qaUserManager.getUser(targetPlayerName);
                    List<Answer> answerList = answerManager.getQAUserAnswers(qaUser);

                    for (Answer answer : answerList) {
                        answer.setReceiveReward(true);
                        answerManager.modifyAnswer(answer);
                    }

                    BukkitAnswerModify bukkitAnswerModify = new BukkitAnswerModify(QAModifyType.MODIFY, answerList.toArray(new Answer[0]));
                    sendPacketAllChannel(bukkitAnswerModify);
                    response.addMessage(configOption.getRemoveRewardSuccess().replace("%playername%", targetPlayerName));
                } else {
                    response.addMessage(configOption.getInvalidPlayerName());
                }

                sendPacket(channel, response);

                break;
            }
            case MINECRAFT_OPTION_RELOAD_REQUEST: {
                MinecraftOptionReloadRequest minecraftOptionReloadRequest = (MinecraftOptionReloadRequest) minecraftPacket;
                PlayerRequestResponseAsChat response = new PlayerRequestResponseAsChat(minecraftOptionReloadRequest.getPlayerUUID(), minecraftOptionReloadRequest.getPacketID());
                ConfigOption configOption = serverOptionManager.getConfigOption();
                VelocityThreadUtil.asyncTask( ()-> {
                    serverOptionManager.loadConfiguration();
                    response.addMessage(configOption.getReloadSuccess());
                    sendPacket(channel, response);
                    List<AbstractOption> abstractOptionList = serverOptionManager.getAllOptions();
                    sendPacketAllChannel(new ServerOption(abstractOptionList));
                });
                break;
            }
            case QUESTION_MODIFY_REQUEST: {
                QuestionModifyRequest questionModifyRequest = (QuestionModifyRequest) minecraftPacket;
                for (Question question : questionModifyRequest.getQuestions()) {
                    questionManager.modifyQuestionData(question, questionModifyRequest.getQaModifyType());
                }
                break;
            }
            case UPDATE_QA_USER_REQUEST: {
                UpdateQAUserRequest updateQAUserRequest = (UpdateQAUserRequest) minecraftPacket;
                for (QAUser qaUser : updateQAUserRequest.getQaUsers()) {
                    qaUserManager.updateQAUser(qaUser, false);
                }
                break;
            }
            case ANSWER_MODIFY_REQUEST: {
                AnswerModifyRequest answerModifyRequest = (AnswerModifyRequest) minecraftPacket;
                for (Answer answer : answerModifyRequest.getAnswers()) {
                    answerManager.modifyAnswer(answer);
                }
                break;
            }
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }

    public void sendPacket(Channel channel, AbstractMinecraftPacket minecraftPacket) {

        VelocityThreadUtil.asyncTask( ()-> {
            if ( channel.isActive() ) {
                ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
                try {
                    channelFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
                if ( channel.isActive() ) {
                    ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
                    try {
                        channelFuture.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

}
