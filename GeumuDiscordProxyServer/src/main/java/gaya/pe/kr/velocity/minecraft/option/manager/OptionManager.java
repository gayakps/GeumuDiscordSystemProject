package gaya.pe.kr.velocity.minecraft.option.manager;

import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.*;
import gaya.pe.kr.velocity.minecraft.geumudiscordproxyserver;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


@Getter
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
        loadConfiguration();
    }

    public void loadConfiguration() {
        try {
            String path = "plugins/config_resources/";
            answerRankingOption = new AnswerRankingOption(load(path+"/GUI/answer_ranking.yml"));
            questionRankingOption = new QuestionRankingOption(load(path+"/GUI/question_ranking.yml"));
            commonlyUsedButtonOption = new CommonlyUsedButtonOption(load(path+"/GUI/commonly_used_button.yml"));
            playerAnswerListOption = new PlayerAnswerListOption(load(path+"/GUI/player_answer_list.yml"));
            playerQuestionListOption = new PlayerQuestionListOption(load(path+"/GUI/player_question_list.yml"));
            waitingAnswerListOption = new WaitingAnswerListOption(load(path+"/GUI/waiting_answer_list.yml"));
            answerPatternOptions = new AnswerPatternOptions(load(path+"/answer.yml"));
            configOption = new ConfigOption(load(path+"/config.yml"));
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> load(String yamlFileName) {
        Yaml yaml = new Yaml();
        FileInputStream inputStream = null;
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            String workingDirectory = System.getProperty("user.dir");
            String filePath = workingDirectory + File.separator + yamlFileName;
            inputStream = new FileInputStream(filePath);
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
