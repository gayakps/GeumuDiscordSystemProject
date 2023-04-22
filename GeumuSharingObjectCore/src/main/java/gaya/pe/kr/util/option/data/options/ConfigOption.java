package gaya.pe.kr.util.option.data.options;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;

import java.util.HashMap;
import java.util.Map;

public class ConfigOption extends AbstractOption {
    public ConfigOption(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.CONFIG);
    }
}
