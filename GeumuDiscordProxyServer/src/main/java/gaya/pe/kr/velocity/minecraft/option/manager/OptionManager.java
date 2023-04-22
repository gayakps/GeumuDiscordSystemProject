package gaya.pe.kr.velocity.minecraft.option.manager;

import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.*;
import gaya.pe.kr.velocity.minecraft.geumudiscordproxyserver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionManager {

    private static class SingleTon {
        private static final OptionManager OPTION_MANAGER = new OptionManager();
    }

    public static OptionManager getInstance() {
        return OptionManager.SingleTon.OPTION_MANAGER;
    }

    AnswerRankingOption answerRankingOption;
    CommonlyUsedButtonOption commonlyUsedButtonOption;
    PlayerAnswerListOption playerAnswerListOption;
    PlayerQuestionListOption playerQuestionListOption;
    QuestionRankingOption questionRankingOption;
    WaitingAnswerListOption waitingAnswerListOption;

    AnswerPatternOptions answerPatternOptions;
    ConfigOption configOption;

    public void init() {

        try {
            String path = "config_resources/GUI";
            answerRankingOption = new AnswerRankingOption(load(path+"/answer_ranking.yml"));
            commonlyUsedButtonOption = new CommonlyUsedButtonOption(load(path+"/commonly_used_button.yml"));
            playerAnswerListOption = new PlayerAnswerListOption(load(path+"/player_answer_list.yml"));
            playerQuestionListOption = new PlayerQuestionListOption(load(path+"/player_question_list.yml"));
            questionRankingOption = new QuestionRankingOption(load(path+"/question_ranking.yml"));
            waitingAnswerListOption = new WaitingAnswerListOption(load(path+"/waiting_answer_list.yml"));
            answerPatternOptions = new AnswerPatternOptions(load(path+"/answer.yml"));
            configOption = new ConfigOption(load(path+"/config.yml"));
//            Map<String, Object> obj = new Yaml().load(new FileInputStream(""));
        } catch ( Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, Object> load(String yamlFileName) {
        Yaml yaml = new Yaml();
        InputStream inputStream = null;
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(yamlFileName);
            if (inputStream != null) {
                result = yaml.load(inputStream);
            } else {
                System.out.println("Failed to load file " + yamlFileName + " << PATH");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
