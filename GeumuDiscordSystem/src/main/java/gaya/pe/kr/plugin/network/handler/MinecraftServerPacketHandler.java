package gaya.pe.kr.plugin.network.handler;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.qa.answer.data.Answer;
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
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * 서버로 부터 전송된 패킷을 처리 하는 곳
 */
public class MinecraftServerPacketHandler extends SimpleChannelInboundHandler<AbstractMinecraftPacket> {

    HashSet<Long> waitingResponseTicketHashSet = new HashSet<>();

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
                waitingResponseTicketHashSet.remove(abstractPlayerRequestResponse.getRequestPacketId());
                Player player = Bukkit.getPlayer(abstractPlayerRequestResponse.getRequestPlayerUUID());
                if ( player != null ) {
                    abstractPlayerRequestResponse.sendData(player);
                }

                break;
            }

            case PLAYER_REQUEST_RESPONSE_AS_OBJECT: {

                AbstractPlayerRequestResponseAsObject<?> abstractPlayerRequestResponseAsObject = (AbstractPlayerRequestResponseAsObject<?>) minecraftPacket;
                waitingResponseTicketHashSet.remove(abstractPlayerRequestResponseAsObject.getRequestPacketId());

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

    public HashSet<Long> getWaitingResponseTicketHashSet() {
        return waitingResponseTicketHashSet;
    }
}
