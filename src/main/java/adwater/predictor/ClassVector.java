package adwater.predictor;

public class ClassVector {
    public int hour;
    public int day;
    public int dayofweek;

    public ClassVector(int hour, int day, int dayofweek) {
        this.hour = hour;
        this.day = day;
        // 周末是0
        this.dayofweek = dayofweek;
    }
}
