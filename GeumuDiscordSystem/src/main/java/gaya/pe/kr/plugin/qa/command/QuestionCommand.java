package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerQuestionListReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.UtilMethod;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.TargetQAUserDataRequest;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestionCommand implements CommandExecutor {

    NetworkManager networkManager = NetworkManager.getInstance();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ( commandSender instanceof Player ) {

            Player player = ((Player) commandSender).getPlayer();

            if ( player == null ) return false;

            PermissionLevelType permissionLevelType = PermissionLevelType.getPermissionLevelType(player);

            if ( permissionLevelType.equals(PermissionLevelType.NONE) ) return false;

            OptionManager optionManager = OptionManager.getInstance();

            ConfigOption configOption = optionManager.getConfigOption();

            QARepository qaRepository = QAManager.getInstance().getQaRepository();

            if ( args.length > 0 ) {

                String category = args[0];

                if ( category.equals("목록") ) {

                    String targetPlayerName;

                    try {
                        targetPlayerName = args[1];
                    } catch ( ArrayIndexOutOfBoundsException e ) {
                        targetPlayerName = player.getName();
                    }

                    TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[]{targetPlayerName}, player, false);
                    networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {

                        if ( qaUsers == null ) {
                            GeumuDiscordSystem.msg(player, configOption.getInvalidPlayerName());
                            return;
                        }

                        SchedulerUtil.runLaterTask( ()-> {
                            QAUser qaUser = qaUsers[0];
                            TargetPlayerQuestionListReactor targetPlayerQuestionListReactor = new TargetPlayerQuestionListReactor(player, qaUser, qaRepository);
                            targetPlayerQuestionListReactor.start();
                        }, 1);


                    });

                    return false;
                }

                String questionContents = UtilMethod.getOneLineString(args, 0);
                if ( questionContents.length() == 0 ) return false;
                PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest = new PlayerTransientProceedingQuestionRequest(player.getName(), player.getUniqueId(), questionContents);
                networkManager.sendPacket(playerTransientProceedingQuestionRequest, player, player1 -> {
                    GeumuDiscordSystem.msg(player1, "");
                });

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
