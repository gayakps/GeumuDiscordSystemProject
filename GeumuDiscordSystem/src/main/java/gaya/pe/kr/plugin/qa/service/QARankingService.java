package gaya.pe.kr.plugin.qa.service;

import gaya.pe.kr.plugin.qa.data.QARankingResult;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class QARankingService {


    public static <T> List<QARankingResult<T>> calculateRankings(List<QAUser> qaUsers, List<T> items, LocalDate today) {
        Map<QAUser, QARankingResult<T>> resultMap = new HashMap<>();

        for (QAUser qaUser : qaUsers) {
            resultMap.put(qaUser, new QARankingResult<>(qaUser, 0, 0, 0, 0, 0));
        }

        for (T item : items) {
            LocalDateTime itemDate = null;
            QAUser itemUser = null;

            if (item instanceof Question) {
                itemDate = LocalDateTime.ofInstant(((Question) item).getQuestionDate().toInstant(), ZoneId.systemDefault());
                itemUser = ((Question) item).getQaUser();
            } else if (item instanceof Answer) {
                itemDate = LocalDateTime.ofInstant(((Answer) item).getAnswerDate().toInstant(), ZoneId.systemDefault());
                itemUser = ((Answer) item).getAnswerPlayer();
            }

            if (itemDate != null && itemUser != null) {
                QARankingResult<T> result = resultMap.get(itemUser);

                LocalDate itemLocalDate = itemDate.toLocalDate();
                if (itemLocalDate.equals(today.minusDays(1))) {
                    result.yesterdayCount++;
                }
                if (itemLocalDate.equals(today)) {
                    result.dailyCount++;
                }
                if (!itemLocalDate.isBefore(today.minusWeeks(1))) {
                    result.weeklyCount++;
                }
                if (!itemLocalDate.isBefore(today.minusMonths(1))) {
                    result.monthlyCount++;
                }
                result.totalCount++;
            }
        }

        List<QARankingResult<T>> rankingResults = new ArrayList<>(resultMap.values());
        Collections.sort(rankingResults);

        for (int i = 0; i < rankingResults.size(); i++) {
            rankingResults.get(i).setRank(i + 1);
        }

        return rankingResults;
    }

}
