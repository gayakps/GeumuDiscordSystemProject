package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.network.packet.startDirection.client.TargetQAUserDataRequest;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import gaya.pe.kr.plugin.qa.reactor.TargetPlayerQuestionListReactor;
import gaya.pe.kr.plugin.qa.repository.QARepository;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.util.UtilMethod;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.data.QuestionAndAnswerMatch;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.qa.question.packet.client.TargetPlayerQuestionRequest;
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

                    TargetQAUserDataRequest targetQAUserDataRequest = new TargetQAUserDataRequest(new String[]{args[1]}, player, false);
                    networkManager.sendDataExpectResponse(targetQAUserDataRequest, player, QAUser[].class, (player1, qaUsers) -> {

                        if ( qaUsers == null ) {
                            player.sendMessage(configOption.getInvalidPlayerName().replace("&", "§"));
                            return;
                        }
                        QAUser qaUser = qaUsers[0];
                        TargetPlayerQuestionListReactor targetPlayerQuestionListReactor = new TargetPlayerQuestionListReactor(player, qaUser, qaRepository);
                        targetPlayerQuestionListReactor.start();

                    });

                    return false;
                }

                String questionContents = UtilMethod.getOneLineString(args, 0);
                if ( questionContents.length() == 0 ) return false;
                PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest = new PlayerTransientProceedingQuestionRequest(player.getName(), player.getUniqueId(), questionContents);
                networkManager.sendPacket(playerTransientProceedingQuestionRequest, player, player1 -> {
                    player1.sendMessage("데이터를 정상적으로 송신합니다");
                });

            } else {

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
