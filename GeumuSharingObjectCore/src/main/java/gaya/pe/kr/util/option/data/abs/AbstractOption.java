package gaya.pe.kr.util.option.data.abs;

import gaya.pe.kr.util.option.type.OptionType;
import gaya.pe.kr.util.option.exception.NonExistConfigurationData;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;


@Getter
public abstract class AbstractOption {

    HashMap<String, Object> dataKeyValue = new HashMap<>();

    OptionType optionType;

    public AbstractOption(HashMap<String, Object> dataKeyValue, OptionType optionType) {
        this.dataKeyValue = dataKeyValue;
        this.optionType = optionType;
    }



}
