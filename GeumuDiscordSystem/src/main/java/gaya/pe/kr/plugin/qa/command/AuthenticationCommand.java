package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AuthenticationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if ( commandSender instanceof Player ) {

            Player player = ((Player) commandSender).getPlayer();

            String code = strings[0];

            DiscordAuthenticationRequest discordAuthenticationRequest = new DiscordAuthenticationRequest(player.getUniqueId(), player.getName(), Integer.parseInt(code) );
            NetworkManager.getInstance().sendData(discordAuthenticationRequest, player, (player1 -> player1.sendMessage("데이터를 성공적으로 보냈습니다")));

        }

        return false;
    }
}