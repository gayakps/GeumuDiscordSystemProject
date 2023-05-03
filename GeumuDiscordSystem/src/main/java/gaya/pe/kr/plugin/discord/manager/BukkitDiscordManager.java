package gaya.pe.kr.plugin.discord.manager;

import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;

public class BukkitDiscordManager {


    private static class SingleTon {
        private static final BukkitDiscordManager DISCORD_MANAGER = new BukkitDiscordManager();
    }

    public static BukkitDiscordManager getInstance() {
        return SingleTon.DISCORD_MANAGER;
    }

    JDA jda;
    public void init() {
        try {
            ConfigOption configOption = OptionManager.getInstance().getConfigOption();
            jda = JDABuilder.createDefault(configOption.getDiscordToken()).build();

            if ( jda == null ) {
                System.out.println("JDA 가 NULL 입니다");
            } else {
                System.out.println("JDA 널 압니다");
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public JDA getJda() {
        return jda;
    }

    public String getFullName(QAUser qaUser) {
        User user = jda.getUserById(qaUser.getDiscordPlayerUserId());

        if ( user == null ) {
            return qaUser.getGamePlayerName();
        }

        String username = user.getName(); // 사용자 이름을 가져옵니다.
        String discriminator = user.getDiscriminator(); // 사용자 태그 (예: #1234)를 가져옵니다.
        return username + "#" + discriminator;
    }


}
