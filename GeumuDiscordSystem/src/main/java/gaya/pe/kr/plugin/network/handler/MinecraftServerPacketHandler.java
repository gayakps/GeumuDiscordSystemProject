package gaya.pe.kr.plugin.network.handler;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.non_response.ScatterServerPlayers;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.player.manager.PlayerManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.util.data.WaitingTicket;
import gaya.pe.kr.plugin.util.exception.IllegalResponseObjectException;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.answer.packet.server.ExpectQuestionAnswerResponse;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.server.BukkitAnswerModify;
import gaya.pe.kr.qa.packet.server.BukkitQuestionModify;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.*;
import gaya.pe.kr.util.option.type.OptionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 서버로 부터 전송된 패킷을 처리 하는 곳
 */
public class MinecraftServerPacketHandler extends SimpleChannelInboundHandler<AbstractMinecraftPacket> {

    HashMap<Long, WaitingTicket<?>> packetWaitingResponseAsObjectHashMap = new HashMap<>();

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

                AbstractPlayerRequestResponse abstractPlayerRequestResponse = (AbstractPlayerRequestResponse) minecraftPacket;
                removeWaitingTicket(abstractPlayerRequestResponse.getRequestPacketId());

                Player player = Bukkit.getPlayer(abstractPlayerRequestResponse.getRequestPlayerUUID());
                if ( player != null ) {
                    abstractPlayerRequestResponse.sendData(player);
                }

                break;
            }

            case BUKKIT_ANSWER_MODIFY: {

                QARepository qaRepository = QAManager.getInstance().getQaRepository();

                BukkitAnswerModify bukkitAnswerModify = (BukkitAnswerModify) minecraftPacket;

                for (Answer answer : bukkitAnswerModify.getAnswers()) {
                    if ( bukkitAnswerModify.getQaModifyType().equals(QAModifyType.ADD) ) {
                        qaRepository.addAnswer(answer);
                    } else {
                        qaRepository.removeAnswer(answer);
                    }
                }

                break;
            }

            case BUKKIT_QUESTION_MODIFY: {

                QARepository qaRepository = QAManager.getInstance().getQaRepository();

                BukkitQuestionModify bukkitQuestionModify = (BukkitQuestionModify) minecraftPacket;

                for (Question question : bukkitQuestionModify.getQuestions()) {
                    if ( bukkitQuestionModify.getQaModifyType().equals(QAModifyType.ADD) ) {
                        qaRepository.addQuestion(question);
                    } else {
                        qaRepository.removeQuestion(question);
                    }
                }

                break;
            }

            case EXPECT_QUESTION_ANSWER_RESPONSE: {

                ExpectQuestionAnswerResponse expectQuestionAnswerResponse = (ExpectQuestionAnswerResponse) minecraftPacket;

                Question question = expectQuestionAnswerResponse.getQuestion();
                Answer answer = expectQuestionAnswerResponse.getAnswer();
                QAUser questioner = expectQuestionAnswerResponse.getTargetUser();
                QAUser answerer = expectQuestionAnswerResponse.getAnswerUser();

                String questionerGamePlayerName = questioner.getGamePlayerName();
                if ( questionerGamePlayerName != null ) {

                    ConfigOption configOption = OptionManager.getInstance().getConfigOption();

                    Player player = Bukkit.getPlayer(questioner.getGamePlayerName());

                    if ( player != null ) {

                        int answerReceivedTitleFadeInTime = configOption.getAnswerReceiveTitleFadeInTime();
                        int answerReceivedTitleFadeOutTime = configOption.getAnswerReceiveTitleFadeOutTime();
                        int answerReceivedTitleFadeStayTime = configOption.getAnswerReceiveTitleStayTime();

                        // 답변이 도착했습니다!, 채팅창을 봐주세요
                        player.sendTitle(configOption.getAnswerReceiveSuccessIfQuestionerOnlineTitle(), configOption.getAnswerReceiveSuccessIfQuestionerOnlineSubtitle(), answerReceivedTitleFadeInTime, answerReceivedTitleFadeStayTime, answerReceivedTitleFadeOutTime);


                    }


                }
                break;
            }

            case PLAYER_REQUEST_RESPONSE_AS_OBJECT: {

                AbstractPlayerRequestResponseAsObject<?> abstractPlayerRequestResponseAsObject = (AbstractPlayerRequestResponseAsObject<?>) minecraftPacket;
                long requestPacketId = abstractPlayerRequestResponseAsObject.getRequestPacketId();


                Object tObject = abstractPlayerRequestResponseAsObject.getT();

                try {
                    if (isWaitingTicket(requestPacketId)) {
                        handleWaitingTicket(requestPacketId, tObject);
                    } else {
                        //TODO 문제 발생
                    }
                } catch (IllegalResponseObjectException e) {
                    e.printStackTrace();
                }


//                    if ( objectClazz == Answer[].class ) {
//                        WaitingTicket<Answer[]> waitingTicket = getWaitingTicket(requestPacketId);
//                        Answer[] answers = (Answer[]) tObject;
//                        waitingTicket.setResult(answers);
//                        waitingTicket.executeAndGetResult();
//                    }
//                    else if ( objectClazz == Question[].class ) {
//                        WaitingTicket<Question[]> waitingTicket = getWaitingTicket(requestPacketId);
//                        Question[] questions = (Question[]) tObject;
//                        waitingTicket.setResult(questions);
//                        waitingTicket.executeAndGetResult();
//                    }
//                    else if ( objectClazz == QA[].class ) {
//                        WaitingTicket<QA[]> waitingTicket = getWaitingTicket(requestPacketId);
//                        QA[] qas = (QA[]) tObject;
//                        waitingTicket.setResult(qas);
//                        waitingTicket.executeAndGetResult();
//                    } else {
//                        //TODO 문제 발생
//                    }


                break;
            }

            case SCATTER_SERVER_PLAYERS: {
                PlayerManager playerManager = PlayerManager.getInstance();
                ScatterServerPlayers scatterServerPlayers = (ScatterServerPlayers) minecraftPacket;
                playerManager.setPlayerList(scatterServerPlayers.getPlayers());
                break;
            }

            case SERVER_OPTION: {

                ServerOption serverOption = (ServerOption) minecraftPacket;

                OptionManager optionManager = OptionManager.getInstance();

                for (AbstractOption abstractOption : serverOption.getAbstractOptionList()) {

                    OptionType optionType = abstractOption.getOptionType();

                    GeumuDiscordSystem.log(String.format("%s Type Received ------------------------------------------", optionType));

                    try {

                        Class<?> clazz = abstractOption.getClass();

                        for (Method declaredMethod : clazz.getDeclaredMethods()) {
                            System.out.printf("[%s] : %s\n",declaredMethod.getName() ,declaredMethod.invoke(abstractOption));
                        }
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }

                    switch ( optionType ) {
                        case ANSWER_PATTEN: {
                            optionManager.setAnswerPatternOptions((AnswerPatternOptions) abstractOption);
                            break;
                        }
                        case CONFIG: {
                            optionManager.setConfigOption((ConfigOption) abstractOption);
                            break;
                        }
                        case ANSWER_RANKING_GUI: {
                            optionManager.setAnswerRankingOption((AnswerRankingOption) abstractOption);
                            break;
                        }
                        case COMMONLY_USED_BUTTON_GUI:{
                            optionManager.setCommonlyUsedButtonOption((CommonlyUsedButtonOption) abstractOption);
                            break;
                        }
                        case WAITING_ANSWER_LIST_GUI: {
                            optionManager.setWaitingAnswerListOption((WaitingAnswerListOption) abstractOption);
                            break;
                        }
                        case PLAYER_ANSWER_LIST_GUI: {
                            optionManager.setPlayerAnswerListOption((PlayerAnswerListOption) abstractOption);
                            break;
                        }
                        case PLAYER_QUESTION_LIST_GUI: {
                            optionManager.setPlayerQuestionListOption((PlayerQuestionListOption) abstractOption);
                            break;
                        }
                        case QUESTION_RANKING_GUI: {
                            optionManager.setQuestionRankingOption((QuestionRankingOption) abstractOption);
                            break;
                        }
                    }

                }
                break;
            }


                
            default:
                // 알 수 없는 패킷 처리
                break;
        }
    }

    public boolean isWaitingTicket(long requestPacketId) {
        return packetWaitingResponseAsObjectHashMap.containsKey(requestPacketId);
    }


    @SuppressWarnings("unchecked")
    public <T> WaitingTicket<T> getWaitingTicket(long requestPacketId) {
        return (WaitingTicket<T>) packetWaitingResponseAsObjectHashMap.get(requestPacketId);
    }

    public void removeWaitingTicket(long requestPacketId) {
        packetWaitingResponseAsObjectHashMap.remove(requestPacketId);
    }

    public void addWaitingTicket(AbstractMinecraftPacket abstractMinecraftPacket, WaitingTicket<?> waitingTicket ) {
        packetWaitingResponseAsObjectHashMap.put(abstractMinecraftPacket.getPacketID(), waitingTicket);
    }

    private <T> void handleWaitingTicket(long requestPacketId, T tObject) throws IllegalResponseObjectException {
        WaitingTicket<T> waitingTicket = getWaitingTicket(requestPacketId);
        waitingTicket.setResult(tObject);
        removeWaitingTicket(requestPacketId);
    }



}
