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
            for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
                System.out.printf("[%s] : %s%n",this.getClass().getSimpleName(), declaredMethod.invoke(this));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public AbstractOption(Map<String, Object> dataKeyValue, OptionType optionType,  String sectionKey) {
        this.dataKeyValue = dataKeyValue;
        this.optionType = optionType;
        this.sectionKey = sectionKey;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getSectionKey(String key) {
        return (Map<String, Object>) dataKeyValue.get(key);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getNestedSectionKey(String... keys) {
        Map<String, Object> currentMap = dataKeyValue;

        for (String key : keys) {
            currentMap = (Map<String, Object>) currentMap.get(key);
        }

        return currentMap;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getSectionKey() {
        return (Map<String, Object>) dataKeyValue.get(sectionKey);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getList(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = dataKeyValue;

        for (int i = 0; i < keys.length - 1; i++) {
            currentMap = (Map<String, Object>) currentMap.get(keys[i]);
        }

        return (List<String>) currentMap.get(keys[keys.length - 1]);
    }



}
