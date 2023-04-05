package gaya.pe.kr.velocity.minecraft.option.manager;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
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
//            Map<String, Object> obj = new Yaml().load(new FileInputStream(""));
        } catch ( Exception e) {
            e.printStackTrace();
        }

    }

}
