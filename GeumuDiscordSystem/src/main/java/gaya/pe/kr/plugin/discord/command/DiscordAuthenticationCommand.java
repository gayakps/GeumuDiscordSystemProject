package gaya.pe.kr.plugin.discord.command;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordAuthenticationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ( sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            String code = args[0];
            DiscordAuthenticationRequest discordAuthenticationRequest = new DiscordAuthenticationRequest(player.getUniqueId(), player.getName(), Integer.parseInt(code) );
            NetworkManager.getInstance().sendPacket(discordAuthenticationRequest, player, (player1 -> GeumuDiscordSystem.msg(player1, "")));
        }

        return false;
    }
}
