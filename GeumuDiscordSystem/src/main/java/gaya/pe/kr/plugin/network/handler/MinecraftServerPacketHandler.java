package gaya.pe.kr.plugin.network.handler;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.non_response.*;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.player.manager.PlayerManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.data.WaitingTicket;
import gaya.pe.kr.plugin.util.exception.IllegalResponseObjectException;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.answer.packet.client.AnswerModifyRequest;
import gaya.pe.kr.qa.answer.packet.server.ExpectQuestionAnswerResponse;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.TargetQAUserDataRequest;
import gaya.pe.kr.qa.packet.client.UpdateQAUserRequest;
import gaya.pe.kr.qa.packet.server.BukkitAnswerModify;
import gaya.pe.kr.qa.packet.server.BukkitQuestionModify;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.util.TimeUtil;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.*;
import gaya.pe.kr.util.option.type.OptionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

                System.out.printf("UUID : %s\n", abstractPlayerRequestResponse.getRequestPlayerUUID().toString());

                long requestPacketId = abstractPlayerRequestResponse.getRequestPacketId();
                if ( isWaitingTicket(requestPacketId ) ) {
                    WaitingTicket<Boolean> waitingTicket = getWaitingTicket(abstractPlayerRequestResponse.getRequestPacketId());
                    waitingTicket.setResult(true);
                    removeWaitingTicket(requestPacketId);
                }

                Player player = Bukkit.getPlayer(abstractPlayerRequestResponse.getRequestPlayerUUID());
                if ( player != null ) {
                    abstractPlayerRequestResponse.sendData(player);
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
                        throw new IllegalResponseObjectException("");
                    }
                } catch (IllegalResponseObjectException e) {
                    e.printStackTrace();
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

                        SchedulerUtil.runLaterTask(()-> {

                            int answerReceivedTitleFadeInTime = configOption.getAnswerReceiveTitleFadeInTime();
                            int answerReceivedTitleFadeOutTime = configOption.getAnswerReceiveTitleFadeOutTime();
                            int answerReceivedTitleFadeStayTime = configOption.getAnswerReceiveTitleStayTime();

                            // 답변이 도착했습니다!, 채팅창을 봐주세요
                            player.sendTitle(configOption.getAnswerReceiveSuccessIfQuestionerOnlineTitle(), configOption.getAnswerReceiveSuccessIfQuestionerOnlineSubtitle(), answerReceivedTitleFadeInTime, answerReceivedTitleFadeStayTime, answerReceivedTitleFadeOutTime);

                            String[] soundData= configOption.getAnswerReceiveSuccessSound().split(":");

                            player.playSound(player.getLocation(), Sound.valueOf(soundData[0]), Integer.parseInt(soundData[1]), Integer.parseInt(soundData[2])); // 사운드 입력

                            SchedulerUtil.runLaterTask( ()-> {

                                if ( !player.isOnline() ) return;

                                String title = configOption.getQuestionNumberAnswerReceiveSuccessIfQuestionerOnlineTitle();
                                String subTitle = configOption.getQuestionNumberAnswerReceiveSuccessIfQuestionerOnlineSubtitle();
                                String message = configOption.getQuestionNumberAnswerReceiveSuccessIfQuestionerOnline().replace("%question_number%", Long.toString(question.getId()));

                                player.sendMessage(message.replace("&", "§"));
                                player.sendTitle(title, subTitle, answerReceivedTitleFadeInTime, answerReceivedTitleFadeStayTime, answerReceivedTitleFadeOutTime);

                            }, answerReceivedTitleFadeInTime+answerReceivedTitleFadeStayTime+answerReceivedTitleFadeOutTime);
                        },1);






                    }


                }
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

                BukkitDiscordManager.getInstance().init();
                break;
            }
            case BROADCAST_MESSAGE:{

                BroadCastMessage broadCastMessage = (BroadCastMessage) minecraftPacket;
                for (String message : broadCastMessage.getMessages()) {
                    Bukkit.broadcastMessage(message.replace("&", "§"));
                }

                break;
            }
            case BROAD_CAST_CLICKABLE_MESSAGE: {

                BroadCastClickableMessage broadCastClickableMessage = (BroadCastClickableMessage) minecraftPacket;

                String hoverMessage = broadCastClickableMessage.getHoverMessage();
                String message = broadCastClickableMessage.getMessage();
                String command = broadCastClickableMessage.getCommand();

                TextComponent textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&',hoverMessage)) ));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.spigot().sendMessage(textComponent);
                }

                break;
            }
            case START_REWARD_GIVING: {

                NetworkManager networkManager = NetworkManager.getInstance();
                ConfigOption configOption = OptionManager.getInstance().getConfigOption();
                QARepository qaRepository = QAManager.getInstance().getQaRepository();

                int periodDay = Integer.parseInt(configOption.getRewardGracePeriodDay());

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[]{onlinePlayer.getName()}, onlinePlayer, true);

                    networkManager.sendDataExpectResponse(targetQAUserDataRequest, onlinePlayer, QAUser[].class, (player, qaUsers) -> {

                        QAUser qaUser = qaUsers[0];

                        List<Answer> answerList = qaRepository.getQAUserAnswers(qaUser);

                        for (Answer answer : answerList) {

                            if (answer.isReceiveReward()) {

                                Date date = answer.getAnswerDate();

                                long diffDay = TimeUtil.getTimeDiffDay(date);

                                System.out.printf("%s | %d : %d", date.toString(), diffDay, periodDay);

                                if ( diffDay >= periodDay ) {

                                    AnswerModifyRequest answerModifyRequest = new AnswerModifyRequest(QAModifyType.MODIFY, new Answer[]{answer});
                                    networkManager.sendPacket(answerModifyRequest, player1 ->  {
                                        for (String s : configOption.getRewardCommand()) {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%playername%", onlinePlayer.getName()));
                                        }
                                        player.sendMessage(configOption.getRewardPaymentSuccessBroadcast().replace("&", "§"));
                                        answer.setReceiveReward(true);
                                    }, player);
                                }

                            }

                        }

                    });




                }


                break;
            }
            case TARGET_PLAYER_CHAT: {

                TargetPlayerChat targetPlayerChat = (TargetPlayerChat) minecraftPacket;
                String targetPlayerName = targetPlayerChat.getTargetPlayerName();
                Player player = Bukkit.getPlayer(targetPlayerName);
                if ( player != null ) {
                    for (String message : targetPlayerChat.getMessages()) {
                        player.sendMessage(message.replace("&", "§"));
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
