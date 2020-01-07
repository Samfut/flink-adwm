package adwater.strategy;

import adwater.predictor.ClassVector;
import adwater.predictor.DecisionTreePredictor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NaiveStrategy {

    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private DecisionTreePredictor decisionTreePredictor;
    private long maxDelay;
    private double delta;

    public NaiveStrategy(double delta) {
        this.delta = delta;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
        this.decisionTreePredictor = new DecisionTreePredictor();
    }

    private ClassVector extracrVector(long timestamp) {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        return new ClassVector(hour, day, dayofweek);
    }

    private double predict(int hour, int day, int dayofweek) {
        return this.decisionTreePredictor.predict(hour, day, dayofweek);
    }

    public long make(long timestamp, long watermark) {
        // 记得更新这个值 不然会一直维持最大值
        maxDelay = Math.max(maxDelay, timestamp - watermark);
        ClassVector vector = this.extracrVector(timestamp);
        double disorder = this.predict(vector.hour, vector.day, vector.dayofweek);
        double latency = maxDelay * disorder;
        long res = (long) latency;
        return res;
    }
}
