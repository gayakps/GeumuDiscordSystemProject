package gaya.pe.kr.util.option.data.options;

import gaya.pe.kr.util.option.data.anno.RequirePlaceHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@ToString
public class PatternMatcher implements Serializable {

    String match;
    String ignore;
    String message;

    public PatternMatcher(String match, String ignore, String message) {
        this.match = match;
        this.ignore = ignore;
        this.message = message;

        System.out.println(this.toString() + " 이 제작되었습니다");

    }

    public boolean matches(String input) {
        Pattern matchPattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);
        Matcher matchMatcher = matchPattern.matcher(input);

        if (ignore != null) {
            Pattern ignorePatt = Pattern.compile(ignore, Pattern.CASE_INSENSITIVE);
            Matcher ignoreMatcher = ignorePatt.matcher(input);
            return matchMatcher.find() && !ignoreMatcher.find();
        }

        return matchMatcher.find();
    }


}
