package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.network.packet.startDirection.client.MinecraftOptionReloadRequest;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import gaya.pe.kr.qa.answer.packet.client.TargetAnswerByQuestionIdRemoveRequest;
import gaya.pe.kr.qa.packet.client.TargetPlayerRemoveRewardRequest;
import gaya.pe.kr.qa.question.packet.client.TargetQuestionRemoveRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AnswerAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ( commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();

            if ( args.length > 0 ) {

                if ( player == null ) return false;

                String category = args[0];
                PermissionLevelType permissionLevelType = PermissionLevelType.getPermissionLevelType(player);
                NetworkManager networkManager = NetworkManager.getInstance();
                OptionManager optionManager = OptionManager.getInstance();
                ConfigOption configOption = optionManager.getConfigOption();

                switch ( category) {
                    case "removeq": {
                        //해당 질문을 제거함

                        if ( !permissionLevelType.equals(PermissionLevelType.STAFF) ) return false;

                        try {
                            int questionId = Integer.parseInt(args[0]);
                            TargetQuestionRemoveRequest targetQuestionRemoveRequest = new TargetQuestionRemoveRequest(questionId, player.getName(), player.getUniqueId());
                            networkManager.sendPacket(targetQuestionRemoveRequest, player, player1 -> {
                                player1.sendMessage("성공적으로 질문 제거 요청을 함");
                            });
                        } catch ( NumberFormatException | ArrayIndexOutOfBoundsException e ) {
                            player.sendMessage(configOption.getInvalidQuestionNumber());
                        }
                        break;
                    }
                    case "removea": {

                        if ( !permissionLevelType.equals(PermissionLevelType.STAFF) ) return false;

                        //해당 질문의 답변을 제거함
                        try {
                            int questionId = Integer.parseInt(args[0]);
                            TargetAnswerByQuestionIdRemoveRequest targetAnswerByQuestionIdRemoveRequest = new TargetAnswerByQuestionIdRemoveRequest(questionId, player.getName(), player.getUniqueId());
                            networkManager.sendPacket(targetAnswerByQuestionIdRemoveRequest, player, player1 -> {
                                player1.sendMessage("성공적으로 답변 제거 요청을 함");
                            });
                        } catch ( NumberFormatException | ArrayIndexOutOfBoundsException e ) {
                            player.sendMessage(configOption.getInvalidQuestionNumber());
                        }
                        break;
                    }
                    case "removereward": {

                        if ( !permissionLevelType.equals(PermissionLevelType.ADMIN) ) return false;

                        TargetPlayerRemoveRewardRequest targetPlayerRemoveRewardRequest = new TargetPlayerRemoveRewardRequest(args[0], player);
                        networkManager.sendPacket(targetPlayerRemoveRewardRequest, player, player1 -> {
                            player1.sendMessage("전송성공 얏호");
                        });

                    }
                    case "reload": {

                        if ( !permissionLevelType.equals(PermissionLevelType.ADMIN) ) return false;

                        MinecraftOptionReloadRequest minecraftOptionReloadRequest = new MinecraftOptionReloadRequest(player);

                        networkManager.sendPacket(minecraftOptionReloadRequest, player, player1 -> {
                            player1.sendMessage("전송성공 얏호");
                        });

                    }
                }



            } else {

            }


        }
        return false;
    }
}
