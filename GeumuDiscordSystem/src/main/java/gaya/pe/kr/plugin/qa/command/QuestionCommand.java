package gaya.pe.kr.plugin.qa.command;

import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.qa.question.packet.client.PlayerProceedingQuestionRequest;
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
            PlayerProceedingQuestionRequest playerProceedingQuestionRequest = new PlayerProceedingQuestionRequest(player.getName(), args[0]);
            NetworkManager.getInstance().sendData(playerProceedingQuestionRequest, player, player1 -> player1.sendMessage("데이터를 정상적으로 송신합니다"));

        }

        return false;
    }
}
