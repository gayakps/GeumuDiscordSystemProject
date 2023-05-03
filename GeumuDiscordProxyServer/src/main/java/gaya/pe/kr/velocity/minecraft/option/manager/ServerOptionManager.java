package gaya.pe.kr.velocity.minecraft.option.manager;

import gaya.pe.kr.network.packet.startDirection.server.non_response.StartRewardGiving;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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

    Timer timer = new Timer();

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
            Date now = new Date();
            Date startDate = getStartTime(configOption.getRewardGracePeriodTime());

            if (now.before(startDate)) {

                System.out.println("보상 지급이 :" + startDate.toString() + " 에 예정 되어있습니다");

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("보상 지급 시작");
                        networkManager.sendPacketAllChannel(new StartRewardGiving());
                    }
                }, startDate);

            }

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

    private Date getStartTime(String getTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date time = null;
        try {
            time = timeFormat.parse(getTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 현재 날짜를 가져옵니다.
        Calendar calendar = Calendar.getInstance();

        // 시간 문자열에서 시간 정보를 가져옵니다.
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(time);

        // 날짜와 시간을 조합합니다.
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));

        return calendar.getTime();
    }

}
