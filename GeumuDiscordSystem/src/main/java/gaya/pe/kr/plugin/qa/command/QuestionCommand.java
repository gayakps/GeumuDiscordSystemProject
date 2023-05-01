package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.plugin.util.UtilMethod;
import gaya.pe.kr.qa.data.QA;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.qa.question.packet.client.TargetPlayerQuestionRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ( commandSender instanceof Player ) {

            Player player = ((Player) commandSender).getPlayer();

            if ( player == null ) return false;

            PermissionLevelType permissionLevelType = PermissionLevelType.getPermissionLevelType(player);

            if ( permissionLevelType.equals(PermissionLevelType.NONE) ) return false;

            OptionManager optionManager = OptionManager.getInstance();

            ConfigOption configOption = optionManager.getConfigOption();

            if ( args.length > 0 ) {

                String category = args[0];
                    NetworkManager networkManager = NetworkManager.getInstance();

                if ( category.equals("목록") ) {

                    TargetPlayerQuestionRequest targetPlayerQuestionRequest = new TargetPlayerQuestionRequest(player.getName(), player.getUniqueId(), args[1]);
                    networkManager.sendDataExpectResponse(targetPlayerQuestionRequest, player, QA[].class, (player1, questions) -> {

                        if ( questions == null ) {
                            //존재하지 않는 플레이어 일 때
                            return;
                        }

                        if ( questions.length == 0 ) {
                            // 질문이 없을 떄
                            return;
                        }


                    });

                    return false;
                }

                String questionContents = UtilMethod.getOneLineString(args, 0);
                PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest = new PlayerTransientProceedingQuestionRequest(player.getName(), player.getUniqueId(), questionContents);
                NetworkManager.getInstance().sendPacket(playerTransientProceedingQuestionRequest, player, player1 -> {
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
