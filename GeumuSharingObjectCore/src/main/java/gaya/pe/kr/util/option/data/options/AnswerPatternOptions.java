package gaya.pe.kr.util.option.data.options;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.type.OptionType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class AnswerPatternOptions extends AbstractOption {

    List<PatternMatcher> patternMatcherList = new ArrayList<>();

    public AnswerPatternOptions(Map<String, Object> dataKeyValue) {
        super(dataKeyValue, OptionType.ANSWER_PATTEN);

        for (int a = 1; a <= 1000; a++) {
            String path = a+"A";
            Map<String, Object> nestedSection = getNestedSectionKey(path);
            if (nestedSection != null) {
                String matchPattern = (String) nestedSection.get("match");
                String ignore = nestedSection.containsKey("ignore") ? (String) nestedSection.get("ignore") : "";
                String message = (String) nestedSection.get("message");
                PatternMatcher patternMatcher = new PatternMatcher(matchPattern, ignore, message);
                patternMatcherList.add(patternMatcher);
                System.out.println("PATTERN ADD : " + patternMatcher);
            } else {
                System.out.println("더 이상 데이터가 없습니다 : " + path);
                break;
            }
        }

    }




}
