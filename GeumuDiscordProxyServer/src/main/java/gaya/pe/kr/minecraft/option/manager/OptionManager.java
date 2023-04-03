package gaya.pe.kr.minecraft.option.manager;

import gaya.pe.kr.minecraft.discord.manager.DiscordManager;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class OptionManager {

    private static class SingleTon {
        private static final OptionManager OPTION_MANAGER = new OptionManager();
    }

    public static OptionManager getInstance() {
        return OptionManager.SingleTon.OPTION_MANAGER;
    }
    public void init() {

        try {
            Map<String, Object> obj = new Yaml().load(new FileInputStream(""));
        } catch ( Exception e) {
            e.printStackTrace();
        }

    }

}
