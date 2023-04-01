package gaya.pe.kr.util.option.data.abs;

import gaya.pe.kr.util.option.type.OptionType;
import gaya.pe.kr.util.option.exception.NonExistConfigurationData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractOption {

    HashMap<String, String> stringAsData = new HashMap<>();
    HashMap<String, List<String>> stringAsArrayListData = new HashMap<>();

    OptionType optionType;

    abstract protected void load();

    public String getString(String key) {

        if ( stringAsArrayListData.containsKey(key) ) {
            return stringAsArrayListData.get(key).get(0);
        }

        if ( stringAsData.containsKey(key) ) {
            return stringAsData.get(key);
        }

        throw new NonExistConfigurationData(String.format("Key-%s 값은 %s Config 에서 존재하지 않습니다", key, optionType.name()));

    }

    public List<String> getStringList(String key) {

        if ( stringAsArrayListData.containsKey(key) ) {
            return new ArrayList<>(stringAsArrayListData.get(key));
        }

        if ( stringAsData.containsKey(key) ) {
            return Collections.singletonList(stringAsData.get(key));
        }

        throw new NonExistConfigurationData(String.format("Key-%s 값은 %s Config 에서 존재하지 않습니다", key, optionType.name()));

    }

}
