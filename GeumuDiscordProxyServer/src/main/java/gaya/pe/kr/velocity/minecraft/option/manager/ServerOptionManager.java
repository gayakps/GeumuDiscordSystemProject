package gaya.pe.kr.velocity.minecraft.option.manager;

import gaya.pe.kr.util.option.data.abs.AbstractOption;
import gaya.pe.kr.util.option.data.options.AnswerPatternOptions;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.util.option.data.options.gui.*;
import gaya.pe.kr.velocity.database.DBConnection;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.network.manager.NetworkManager;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Getter
public class ServerOptionManager {

    private static class SingleTon {
        private static final ServerOptionManager OPTION_MANAGER = new ServerOptionManager();
    }

    public static ServerOptionManager getInstance() {
        return ServerOptionManager.SingleTon.OPTION_MANAGER;
    }

    AnswerRankingOption answerRankingOption;
    CommonlyUsedButtonOption commonlyUsedButtonOption;
    PlayerAnswerListOption playerAnswerListOption;
    PlayerQuestionListOption playerQuestionListOption;
    QuestionRankingOption questionRankingOption;
    WaitingAnswerListOption waitingAnswerListOption;

    AnswerPatternOptions answerPatternOptions;
    ConfigOption configOption;

    DiscordManager discordManager = DiscordManager.getInstance();
    NetworkManager networkManager = NetworkManager.getInstance();

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
            DBConnection.init(configOption);

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

    public List<AbstractOption> getAllOptions() {

        return Arrays.asList(getConfigOption(), getAnswerPatternOptions(), getAnswerRankingOption(), getQuestionRankingOption()
        , getPlayerAnswerListOption(), getPlayerQuestionListOption(), getWaitingAnswerListOption(), getCommonlyUsedButtonOption());

    }

}
