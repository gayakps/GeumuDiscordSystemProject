package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.startDirection.client.TargetQAUserDataRequest;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.reactor.AllPlayerWaitingAnswerQuestionListReactor;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerAnswerListReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.util.UtilMethod;
import gaya.pe.kr.qa.answer.packet.client.PlayerRecentQuestionAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.answer.packet.client.TargetPlayerAnswerRequest;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                        TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[] {args[0]} , player, false);
                        networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {

                            if ( qaUsers == null ) {
                                player.sendMessage(configOption.getInvalidPlayerName().replace("&","§"));
                                return;
                            }
                            TargetPlayerAnswerListReactor answerListReactor = new TargetPlayerAnswerListReactor(player, qaUsers[0], qaRepository);
                            answerListReactor.start();

                        });
                        break;
                    }
                    case "대기": {
                        TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[] {player.getName()} , player, true);
                        networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {
                            AllPlayerWaitingAnswerQuestionListReactor allPlayerWaitingAnswerQuestionListReactor
                                    = new AllPlayerWaitingAnswerQuestionListReactor(player, qaUsers[0], qaRepository);
                            allPlayerWaitingAnswerQuestionListReactor.start();
                        });
                        break;
                    }
                    case "디스코드": {
                        try {
                            String code = args[2];
                            DiscordAuthenticationRequest discordAuthenticationRequest = new DiscordAuthenticationRequest(player.getUniqueId(), player.getName(), Integer.parseInt(code));
                            networkManager.sendPacket(
                                    discordAuthenticationRequest
                                    , player,
                                    (player1 -> player1.sendMessage("데이터를 성공적으로 보냈습니다"))
                            );
                        } catch ( Exception e ) {
                            player.sendMessage(configOption.getInvalidAuthenticationCode().replace("&","§"));
                        }
                        break;
                    }
                    default: {

                        boolean questionCategory = true;

                        try {
                            Integer.parseInt(args[0]);
                        } catch ( NumberFormatException e ) {
                            questionCategory = false;
                        }

                        String answerContent = UtilMethod.getOneLineString(args, 1);

                        if ( answerContent.length() == 0 ) {
                            player.sendMessage("§c답장을 입력해주세요");
                            return false;
                        }

                        if ( questionCategory ) {
                            String nickName = args[0];

                            PlayerRecentQuestionAnswerRequest playerRecentQuestionAnswerRequest = new PlayerRecentQuestionAnswerRequest(nickName, answerContent, player);
                            networkManager.sendPacket(playerRecentQuestionAnswerRequest, player, player1 -> {
                                player1.sendMessage("전달 성공~");
                            });


                        } else {
                            long questionId = Long.parseLong(args[0]);

                            PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = new PlayerTransientProceedingAnswerRequest(questionId, answerContent, player);
                            networkManager.sendPacket(playerTransientProceedingAnswerRequest, player, player1 -> {
                                player1.sendMessage("전달 성공~");
                            });
                        }

                    }
                }



            }  else {

                switch ( permissionLevelType ) {
                    case ADMIN: {
                        for (String message : configOption.getQuestionHelpSuccessAdmin()) {
                            player.sendMessage(message);
                        }
                        break;
                    }
                    case USER: {
                        for (String message : configOption.getQuestionHelpSuccessUser()) {
                            player.sendMessage(message);
                        }
                        break;
                    }
                    case STAFF: {
                        for (String message : configOption.getQuestionHelpSuccessStaff()) {
                            player.sendMessage(message);
                        }
                        break;
                    }
                }


            }



        }

        return false;
    }
}
