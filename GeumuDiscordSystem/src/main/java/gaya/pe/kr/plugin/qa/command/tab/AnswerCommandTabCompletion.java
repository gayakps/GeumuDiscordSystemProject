package gaya.pe.kr.plugin.qa.command.tab;

import gaya.pe.kr.plugin.qa.type.PermissionLevelType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnswerCommandTabCompletion implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ( sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            PermissionLevelType permissionLevelType = PermissionLevelType.getPermissionLevelType(player);

        }


        return null;
    }
}
