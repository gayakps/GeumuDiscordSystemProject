package gaya.pe.kr.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GeumuDiscordSystem extends JavaPlugin implements CommandExecutor {


    String pattern = "(여행용|도구|여행자|여행용 도구|여행용도구|여행).*(가방|세트|셋트|상자|키트).*";
    String ignore = "지닌도구상자|지닌 도구 상자|지닌도구 상자|지닌 도구상자";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("test123").setExecutor(this);
        JDA jda = null;

        TextChannel textChannel = jda.getTextChannelById(123);

        textChannel.sendMessage("messageContent").queue(message -> {
            // message sent successfully
            String messageId = message.getId();
            System.out.println("Sent message with ID: " + messageId);
        }, error -> {
            // message failed to send
            System.out.println("Error sending message: " + error.getMessage());
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {

        String text = "여행용 가방 세트와 함께 여행을 떠나보세요 나는 도구 상자 중 지닌도구상자 를 가지고있어";

        System.out.println("BEFORE : " + text);

        text = text.replaceAll(ignore, "");
        System.out.println("[1] AFTER : " + text);

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);

        System.out.println("RESULT : " + m.matches());

        return false;

    }

}
