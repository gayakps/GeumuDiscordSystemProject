package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.network.packet.startDirection.client.MinecraftOptionReloadRequest;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
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

                        if ( !PermissionLevelType.canAccess(permissionLevelType, PermissionLevelType.STAFF) ) return false;

                        try {
                            int questionId = Integer.parseInt(args[1]);
                            TargetQuestionRemoveRequest targetQuestionRemoveRequest = new TargetQuestionRemoveRequest(questionId, player.getName(), player.getUniqueId());
                            networkManager.sendPacket(targetQuestionRemoveRequest, player, player1 -> {
                                GeumuDiscordSystem.msg(player1, "");
                            });
                        } catch ( NumberFormatException | ArrayIndexOutOfBoundsException e ) {
                            GeumuDiscordSystem.msg(player, configOption.getInvalidQuestionNumber());

                        }
                        break;
                    }
                    case "removea": {

                        if ( !PermissionLevelType.canAccess(permissionLevelType, PermissionLevelType.STAFF) ) return false;

                        //해당 질문의 답변을 제거함
                        try {
                            int questionId = Integer.parseInt(args[1]);
                            TargetAnswerByQuestionIdRemoveRequest targetAnswerByQuestionIdRemoveRequest = new TargetAnswerByQuestionIdRemoveRequest(questionId, player.getName(), player.getUniqueId());
                            networkManager.sendPacket(targetAnswerByQuestionIdRemoveRequest, player, player1 -> {
                                GeumuDiscordSystem.msg(player1, "");
                            });
                        } catch ( NumberFormatException | ArrayIndexOutOfBoundsException e ) {
                            GeumuDiscordSystem.msg(player, configOption.getInvalidQuestionNumber());

                        }
                        break;
                    }
                    case "removereward": {

                        if ( !PermissionLevelType.canAccess(permissionLevelType, PermissionLevelType.ADMIN) ) return false;

                        TargetPlayerRemoveRewardRequest targetPlayerRemoveRewardRequest = new TargetPlayerRemoveRewardRequest(args[1], player);
                        networkManager.sendPacket(targetPlayerRemoveRewardRequest, player, player1 -> {
                            GeumuDiscordSystem.msg(player1, "");

                        });
                        break;
                    }
                    case "reload": {

                        if ( !PermissionLevelType.canAccess(permissionLevelType, PermissionLevelType.ADMIN) ) return false;

                        MinecraftOptionReloadRequest minecraftOptionReloadRequest = new MinecraftOptionReloadRequest(player);

                        networkManager.sendPacket(minecraftOptionReloadRequest, player, player1 -> {
                            GeumuDiscordSystem.msg(player1, "");
                        });

                    }
                }



            } else {

            }


        }
        return false;
    }
}
