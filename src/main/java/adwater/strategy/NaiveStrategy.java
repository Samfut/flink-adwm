package adwater.strategy;

import adwater.predictor.ClassVector;
import adwater.predictor.DecisionTreePredictor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;


public class NaiveStrategy {

    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private DecisionTreePredictor decisionTreePredictor;
    private long maxDelay;
    private double latency;
    private double delta;
    public double[] disorders;
    private double lastDisorder;

    public long lateEvent;
    public long eventCount;

    public NaiveStrategy(double delta) {
        this.delta = delta;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
        this.decisionTreePredictor = new DecisionTreePredictor();
        this.lateEvent = 0;
        this.eventCount = 0;
        this.latency = 0;
        this.lastDisorder = 0.0;
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

    // lateRate用来监控延迟率，如果是-1表示没到监控周期
    public long make(long timestamp, long watermark, double lateRate) {

        maxDelay = Math.max(maxDelay, timestamp - watermark);
        ClassVector vector = this.extracrVector(timestamp);
        double disorder = this.predict(vector.hour, vector.day, vector.dayofweek);

        if(ThreadLocalRandom.current().nextDouble()>1-disorder) {
            return -1;
        }

        if(lateRate < 0) {
            if(latency < 0) {
                latency = 0;
            }
            return (long)latency;
        }

        // 每次更新预测值的时候表示阶段更新了，就更新一下maxDelay
        if(disorder != lastDisorder) {
            maxDelay = 0;
            lastDisorder = disorder;
        }

        int i = 0;
        if(lateRate > delta + 0.05) {
            i = 1;
        } else {
            i = -1;
        }

        latency = latency + maxDelay * disorder * i;
        long res = (long) latency;
        if(res < 0) {
            res = 0;
        }
        return res;
    }
}
