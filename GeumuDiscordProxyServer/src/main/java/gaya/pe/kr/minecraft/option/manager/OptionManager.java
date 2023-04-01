package gaya.pe.kr.minecraft.option.manager;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class OptionManager {


    public void init() {

        try {
            Map<String, Object> obj = new Yaml().load(new FileInputStream(""));
        } catch ( Exception e) {
            e.printStackTrace();
        }



    }

}
