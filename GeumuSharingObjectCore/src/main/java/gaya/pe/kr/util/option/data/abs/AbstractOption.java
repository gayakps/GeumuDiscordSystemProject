package gaya.pe.kr.util.option.data.abs;

import gaya.pe.kr.util.option.type.OptionType;
import gaya.pe.kr.util.option.exception.NonExistConfigurationData;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.*;


@Getter
public abstract class AbstractOption {

    Map<String, Object> dataKeyValue;

    OptionType optionType;

    String sectionKey;

    public AbstractOption(Map<String, Object> dataKeyValue, OptionType optionType) {
        this.dataKeyValue = dataKeyValue;
        this.optionType = optionType;

        try {
            for (Method declaredMethod : getClass().getDeclaredMethods()) {
                System.out.printf("[%s] : %s\n",declaredMethod.getName() ,declaredMethod.invoke(this));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }


    @SuppressWarnings("unchecked")
    protected Map<String, Object> getNestedSectionKey(String... keys) {
        Map<String, Object> currentMap = new LinkedHashMap<>(dataKeyValue);

        for (String key : keys) {
            if (currentMap == null || !currentMap.containsKey(key)) {
                return null;
            }
            currentMap = (Map<String, Object>) currentMap.get(key);
        }

        return currentMap;
    }


    @SuppressWarnings("unchecked")
    protected List<String> getList(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = new LinkedHashMap<>(dataKeyValue);

        for (int i = 0; i < keys.length - 1; i++) {
            currentMap = (Map<String, Object>) currentMap.get(keys[i]);
        }

        return (List<String>) currentMap.get(keys[keys.length - 1]);
    }



}
