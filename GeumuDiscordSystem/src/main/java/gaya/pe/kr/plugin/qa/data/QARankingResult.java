package gaya.pe.kr.plugin.qa.data;


import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;


@Getter
public class QARankingResult<T> implements Comparable<QARankingResult<T>> {
    private QAUser qaUser;
    public int yesterdayCount;
    public int dailyCount;
    public int weeklyCount;
    public int monthlyCount;
    public int totalCount;
    private int rank;

    public QARankingResult(QAUser qaUser, int yesterdayCount, int dailyCount, int weeklyCount, int monthlyCount, int totalCount) {
        this.qaUser = qaUser;
        this.yesterdayCount = yesterdayCount;
        this.dailyCount = dailyCount;
        this.weeklyCount = weeklyCount;
        this.monthlyCount = monthlyCount;
        this.totalCount = totalCount;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(QARankingResult<T> other) {
        return Integer.compare(other.totalCount, this.totalCount);
    }

}