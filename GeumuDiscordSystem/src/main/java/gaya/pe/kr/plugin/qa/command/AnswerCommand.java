package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.startDirection.client.MinecraftOptionReloadRequest;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.player.manager.PlayerManager;
import gaya.pe.kr.plugin.qa.conversation.AnswerConversation;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.reactor.AllPlayerWaitingAnswerQuestionListReactor;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerAnswerListReactor;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerQuestionListReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.UtilMethod;
import gaya.pe.kr.qa.answer.packet.client.PlayerRecentQuestionAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.TargetAnswerByQuestionIdRemoveRequest;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.TargetPlayerRemoveRewardRequest;
import gaya.pe.kr.qa.packet.client.TargetQAUserDataRequest;
import gaya.pe.kr.qa.question.packet.client.TargetQuestionRemoveRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AnswerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ( commandSender instanceof Player) {

            Player player = ((Player) commandSender).getPlayer();

            if ( player == null ) return false;

            PermissionLevelType permissionLevelType = PermissionLevelType.getPermissionLevelType(player);

            if ( permissionLevelType.equals(PermissionLevelType.NONE) ) return false;

            OptionManager optionManager = OptionManager.getInstance();
            ConfigOption configOption = optionManager.getConfigOption();
            QARepository qaRepository = QAManager.getInstance().getQaRepository();
            NetworkManager networkManager = NetworkManager.getInstance();

            if ( args.length > 0 ) {

                String category = args[0];

                switch ( category ) {
                    case "목록": {

                        String targetPlayerName;

                        try {
                            targetPlayerName = args[1];
                        } catch ( ArrayIndexOutOfBoundsException e ) {
                            targetPlayerName = player.getName();
                        }

                        TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[] {targetPlayerName} , player, false);
                        networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {

                            if ( qaUsers == null ) {
                                GeumuDiscordSystem.msg(player, configOption.getInvalidPlayerName());
                                return;
                            }

                            SchedulerUtil.runLaterTask( ()-> {
                                TargetPlayerAnswerListReactor answerListReactor = new TargetPlayerAnswerListReactor(player, qaUsers[0], qaRepository);
                                answerListReactor.start();
                            }, 1);

                        });
                        break;
                    }
                    case "대기": {
                        TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[] {player.getName()} , player, true);
                        networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {
                            SchedulerUtil.runLaterTask( ()-> {
                                AllPlayerWaitingAnswerQuestionListReactor allPlayerWaitingAnswerQuestionListReactor
                                        = new AllPlayerWaitingAnswerQuestionListReactor(player, qaUsers[0], qaRepository);
                                allPlayerWaitingAnswerQuestionListReactor.start();
                            }, 1);
                        });
                        break;
                    }
                    case "디스코드": {

                        try {
                            String code = args[2];
                            DiscordAuthenticationRequest discordAuthenticationRequest = new DiscordAuthenticationRequest(player.getUniqueId(), player.getName(), Integer.parseInt(code));
                            networkManager.sendPacket(
                                    discordAuthenticationRequest
                                    , (player1 -> GeumuDiscordSystem.msg(player, "") )
                                    ,player
                            );
                        } catch ( Exception e ) {
                            GeumuDiscordSystem.msg(player, configOption.getInvalidAuthenticationCode());
                        }
                        break;
                    }

                    default: {

                        boolean number = true;

                        try {
                            Integer.parseInt(category);
                        } catch ( NumberFormatException e ) {
                            number = false;
                        }

                        String answerContent = UtilMethod.getOneLineString(args, 1);

                        if ( answerContent.length() == 0 ) {
                            GeumuDiscordSystem.msg(player, "&f[&c&l!&f] 답장을 입력해주세요");
                            return false;
                        }

                        if ( PlayerManager.getInstance().getPlayerList().contains(category) ) {
                            // 특정 플레이어가 했던 질무ㄴ
                            String nickName = args[0];
                            PlayerRecentQuestionAnswerRequest playerRecentQuestionAnswerRequest = new PlayerRecentQuestionAnswerRequest(nickName, answerContent, player);
                            networkManager.sendPacket(playerRecentQuestionAnswerRequest, player, player1 -> {
                                String[] soundData= configOption.getAnswerSendSuccessSound().split(":");
                                SchedulerUtil.runLaterTask(()-> {
                                    player1.playSound(player1.getLocation(), Sound.valueOf(soundData[0].toUpperCase(Locale.ROOT)), Integer.parseInt(soundData[1]), Integer.parseInt(soundData[2])); // 사운드 입력
                                },1);
                            });
                        } else {
                            if ( number ) {
                                long questionId = Long.parseLong(args[0]);
                                PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = new PlayerTransientProceedingAnswerRequest(questionId, answerContent, player);
                                networkManager.sendPacket(playerTransientProceedingAnswerRequest, player, player1 -> {
                                    String[] soundData = configOption.getAnswerSendSuccessSound().split(":");
                                    SchedulerUtil.runLaterTask(() -> {
                                        player1.playSound(player1.getLocation(), Sound.valueOf(soundData[0].toUpperCase(Locale.ROOT)), Integer.parseInt(soundData[1]), Integer.parseInt(soundData[2])); // 사운드 입력
                                    }, 1);
                                });
                            }
                        }

                        break;

                    }
                    case "conversation": {

                        String answerContent = UtilMethod.getOneLineString(args, 1);

                        if ( answerContent.length() == 0 ) {
                            GeumuDiscordSystem.msg(player, "&f[&c&l!&f] 답장을 입력해주세요");
                            return false;
                        }

                        long questionId = Long.parseLong(args[1]);

                        AnswerConversation answerConversation = new AnswerConversation(questionId, player);
                        AnswerConversation.startConversation(answerConversation, player);

                        break;
                    }
                }



            } else {

                switch ( permissionLevelType ) {
                    case ADMIN: {
                        for (String message : configOption.getQuestionHelpSuccessAdmin()) {
                            GeumuDiscordSystem.msg(player, message);
                        }
                        break;
                    }
                    case USER: {
                        for (String message : configOption.getQuestionHelpSuccessUser()) {
                            GeumuDiscordSystem.msg(player, message);
                        }
                        break;
                    }
                    case STAFF: {
                        for (String message : configOption.getQuestionHelpSuccessStaff()) {
                            GeumuDiscordSystem.msg(player, message);
                        }
                        break;
                    }
                }


            }



        }

        return false;
    }
}
