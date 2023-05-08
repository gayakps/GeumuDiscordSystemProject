package gaya.pe.kr.plugin.qa.service;

import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerRankingService {

    public static int countAnswersForUser(List<Answer> answers, LocalDate startDate, LocalDate endDate) {
        return (int) answers.stream()
                .filter(answer -> {
                    LocalDate answerDate = LocalDateTime.ofInstant(answer.getAnswerDate().toInstant(), ZoneId.systemDefault()).toLocalDate();
                    return !answerDate.isBefore(startDate) && !answerDate.isAfter(endDate);
                })
                .count();
    }

    public static Map<QAUser, Integer> getAnswerCountMap(List<Answer> answers, LocalDate startDate, LocalDate endDate) {
        Map<QAUser, Integer> answerCountMap = new HashMap<>();

        answers.stream()
                .filter(a -> {
                    LocalDate answerDate = LocalDateTime.ofInstant(a.getAnswerDate().toInstant(), ZoneId.systemDefault()).toLocalDate();
                    return !answerDate.isBefore(startDate) && !answerDate.isAfter(endDate);
                })
                .distinct()
                .forEach(a -> answerCountMap.put(a.getAnswerPlayer(), answerCountMap.getOrDefault(a.getAnswerPlayer(), 0) + 1));


        return answerCountMap;
    }

}
