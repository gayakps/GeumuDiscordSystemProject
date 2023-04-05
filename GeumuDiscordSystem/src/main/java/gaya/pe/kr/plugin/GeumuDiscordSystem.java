package gaya.pe.kr.plugin;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import io.netty.channel.ChannelFuture;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GeumuDiscordSystem extends JavaPlugin implements CommandExecutor {


    static Plugin plugin;
    String pattern = "(여행용|도구|여행자|여행용 도구|여행용도구|여행).*(가방|세트|셋트|상자|키트).*";
    String ignore = "지닌도구상자|지닌 도구 상자|지닌도구 상자|지닌 도구상자";

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        NetworkManager networkManager = NetworkManager.getInstance();
        networkManager.init();
        getCommand("test123").setExecutor(this);
//        JDA jda = null;
//
//        TextChannel textChannel = jda.getTextChannelById(123);
//
//        textChannel.sendMessage("messageContent").queue(message -> {
//            // message sent successfully
//            String messageId = message.getId();
//            System.out.println("Sent message with ID: " + messageId);
//        }, error -> {
//            // message failed to send
//            System.out.println("Error sending message: " + error.getMessage());
//        });
//
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {

        if ( var1 instanceof Player) {
            Player player = (Player) var1;
            String code = var4[0];

            DiscordAuthenticationRequest discordAuthenticationRequest = new DiscordAuthenticationRequest(player.getUniqueId(), player.getName(), Integer.parseInt(code) );
            NetworkManager.getInstance().sendData(discordAuthenticationRequest, player, (player1 -> player1.sendMessage("데이터를 성공적으로 보냈습니다")));


        }

        return false;

    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void log(String... messages) {

        for (String message : messages) {
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', message));
        }


    }

}
