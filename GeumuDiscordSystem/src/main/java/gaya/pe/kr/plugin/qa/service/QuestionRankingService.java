package gaya.pe.kr.plugin.qa.service;

import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionRankingService {

    public static Map<QAUser, Integer> getQuestionCountMap(List<Question> questions, LocalDate startDate, LocalDate endDate) {
        Map<QAUser, Integer> questionCountMap = new HashMap<>();

        questions.stream()
                .filter(q -> {
                    LocalDate questionDate = new java.util.Date(q.getQuestionDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // 수정된 부분
                    return !questionDate.isBefore(startDate) && !questionDate.isAfter(endDate);
                })
                .distinct()
                .forEach(q -> questionCountMap.put(q.getQaUser(), questionCountMap.getOrDefault(q.getQaUser(), 0) + 1));

        return questionCountMap;
    }

    public static int countQuestionsForUser(List<Question> questions, LocalDate startDate, LocalDate endDate) {
        return (int) questions.stream()
                .filter(q -> {
                    LocalDate questionDate = new java.util.Date(q.getQuestionDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // 수정된 부분
                    return !questionDate.isBefore(startDate) && !questionDate.isAfter(endDate);
                })

                .count();
    }



    public static Map<QAUser, Integer> getWeeklyQuestionCountMap(List<Question> questions, LocalDate targetDate) {
        LocalDate weekStart = targetDate.minusWeeks(1);
        LocalDate weekEnd = targetDate.minusDays(1);

        Map<QAUser, Integer> weeklyQuestionCountMap = new HashMap<>();

        questions.stream()
                .filter(q -> {
                    LocalDate questionDate = new java.util.Date(q.getQuestionDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // 수정된 부분
                    return !questionDate.isBefore(weekStart) && !questionDate.isAfter(weekEnd);
                })
                .forEach(q -> weeklyQuestionCountMap.put(q.getQaUser(), weeklyQuestionCountMap.getOrDefault(q.getQaUser(), 0) + 1));

        return weeklyQuestionCountMap;
    }



}
