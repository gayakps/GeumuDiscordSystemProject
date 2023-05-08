package gaya.pe.kr.plugin;

import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.player.manager.PlayerManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.plugin.qa.manager.QAManager;
import io.netty.channel.ChannelFuture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class GeumuDiscordSystem extends JavaPlugin implements CommandExecutor {

    static Plugin plugin;

    NetworkManager networkManager = NetworkManager.getInstance();
    QAManager qaManager = QAManager.getInstance();
    PlayerManager playerManager = PlayerManager.getInstance();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        networkManager.init();
        qaManager.init();
        playerManager.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {



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

    public static void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        log(String.format("&f[&6&l%s&f]의 클래스가 정상적으로 이벤트 핸들러에 등록됐습니다", listener.getClass().getName()));

    }

    public static void registerCommand(String command, CommandExecutor commandExecutor) {
        Bukkit.getPluginCommand(command).setExecutor(commandExecutor);
        log(String.format("&f[&6&l%s&f]의 클래스가 정상적으로 커맨드 핸들러에 등록됐습니다 커맨드 : &f[&6&l%s&f]", commandExecutor.getClass().getName(), command));
    }

    public static void registerTabCommand(String command, TabCompleter tabCompleter) {
        Bukkit.getPluginCommand(command).setTabCompleter(tabCompleter);
    }

    public static BukkitScheduler getBukkitScheduler() {
        return Bukkit.getScheduler();
    }

    public static void msg(Player player, String... s) {

        for (String s1 : s) {
            if ( !s1.isEmpty() ) {
                player.sendRawMessage(String.format("%s", s1).replace("&", "§"));
            }
        }
    }

}
