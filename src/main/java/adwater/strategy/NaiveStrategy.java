package adwater.strategy;

import adwater.DiStreamingJob;
import adwater.predictor.ClassVector;
import adwater.predictor.DecisionTreePredictor;
import adwater.predictor.LSTMPredictor;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class NaiveStrategy {

    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private DecisionTreePredictor Predictor;
    private LSTMPredictor lstmPredictor;
    private long maxDelay;
    private double latency;
    private double threshold;
    public double[] disorders;
    private double lastDisorder;
    private long maxDelayThreshold;

    public long lateEvent;
    public long eventCount;

    public NaiveStrategy(double threshold, long maxDelayThreshold) {
        this.threshold = threshold;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
        URL modelURL = NaiveStrategy.class.getClassLoader().getResource("model/citybike/treemodel.pmml");
        this.Predictor = new DecisionTreePredictor(modelURL.getPath());
        this.lstmPredictor = new LSTMPredictor(modelURL.getPath());
        this.lateEvent = 0;
        this.eventCount = 0;
        this.latency = 0;
        this.lastDisorder = 0.0;
        this.maxDelayThreshold = maxDelayThreshold;
    }

    private ClassVector extracrVector(long timestamp, List<Double> seq) {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        return new ClassVector(hour, day, dayofweek);
    }

    private double predict(ClassVector vector, List<Double> seq) {
        return this.Predictor.predict(vector.hour, vector.day, vector.dayofweek, seq);
    }

    private double predictl(ClassVector vector, List<Double> seq) {
        return this.lstmPredictor.predict(vector.hour, vector.day, vector.dayofweek, seq);
    }

    public long make(long timestamp, long watermark, double lateRate, List<Double> seq) {

        // lateRate用来监控延迟率，如果是-1表示没到监控周期
        if(lateRate < 0) {
            if(latency < 0) {
                latency = 0;
            }
            return (long)latency;
        }
        // 开始预测
        maxDelay = Math.max(maxDelay, timestamp - watermark);
        if(maxDelay > maxDelayThreshold) {
            maxDelay = maxDelayThreshold;
        }
        ClassVector vector = this.extracrVector(timestamp, seq);
        double disorder = this.predict(vector, seq);

        //  如果监控出来迟到率比较低的时候
        if(lateRate <= threshold) {
            // 当前乱序率较低，那么继续以较低的latency发放水位线
            if(disorder<=threshold) {
                latency = disorder * Math.min(latency, maxDelay);;
            }
            // 否则就是在较高的乱序率下降低，说明延迟较高 要缓缓降低
            else {
                latency = latency - (1-disorder)*latency;
            }
            if(ThreadLocalRandom.current().nextDouble() > threshold - lateRate) {
                if(latency<0) {
                    latency = 0;
                }
                return (long) latency;
            }
            else {
                return -1;
            }
        }
        //  如果监控出来迟到率比较高的时候
        else {
            // 当前乱序率较低，说明速度太快，那么需要增加latency
            if(disorder<=threshold) {
                latency = latency + disorder * Math.min(latency, maxDelay);
            }
            // 否则就是在较高的乱序率
            else {
                latency = disorder * maxDelay;
            }
            if(ThreadLocalRandom.current().nextDouble() > lateRate) {
                if(latency<0) {
                    latency = 0;
                }
                return (long) latency;
            }
            else {
                return -1;
            }
        }
    }
}
