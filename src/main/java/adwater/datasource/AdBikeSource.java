package adwater.datasource;

import adwater.StreamingJob;
import adwater.datatypes.BikeRide;
import adwater.predictor.ClassVector;
import adwater.predictor.DecisionTreePredictor;
import adwater.reswriter.DisOrderResWriter;
import adwater.reswriter.LatencyResWriter;
import adwater.reswriter.WatermarkResWriter;
import adwater.srcreader.SrcReader;
import adwater.strategy.DiDiStrategy;
import adwater.strategy.NaiveStrategy;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdBikeSource extends BikeRideSource {

    private boolean isRunning;
    private long currentWaterMark;
    private SimpleDateFormat dateFormat;
    private Queue<Double> seq;
    private long disCount;
    private long allCount;
    private long maxTimestamp;
    private int prehour;
    private double threshold;
    private long latency;
    private long drop;
    private long windowSize;
    private long preTimeStamp;
    private long preLate;
    private long preEvent;
    private int monitorPer;
    private long maxDelayThreshold;
    private Calendar calendar;
    private String DataSet;
    private Map<String, String> CBfileMap;
    private Map<String, String> DifileMap;
    private String cbprefix;
    private String diprefix;

    public AdBikeSource(String data, double threshold, long windowSize, int monitorPer, long maxDelayThreshold) throws Exception {
        this.DataSet  = data;
        this.isRunning = true;
        this.eventCount = 0L;
        this.lateEvent = 0L;
        this.maxTimestamp = 0L;
        this.disCount = 0L;
        this.allCount = 0l;
        this.prehour = 0;
        this.currentWaterMark = 0L;
        this.drop = 0;
        this.threshold = threshold;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.windowSize = windowSize;
        this.preTimeStamp = 0;
        this.monitorPer = monitorPer;
        this.maxDelayThreshold = maxDelayThreshold;
        this.seq = new LinkedList<Double>();
        for(int i = 0; i < 11; i++) {
            this.seq.offer(0.0);
        }
        this.cbprefix = "/home/lsy/resources/bike/";
        this.diprefix = "/home/lsy/resources/didi/";
        this.CBfileMap = new HashMap<String, String>() {
            {
                put("CB201809", cbprefix+"CB201809/CB20180901.csv");
                put("CB201810", cbprefix+"CB201810/CB20181001.csv");
                put("CB201811", cbprefix+"CB201811/CB20181101.csv");
                put("CB201812", cbprefix+"CB201812/CB20181201.csv");
                put("CB201901", cbprefix+"CB201901/CB20190101.csv");
                put("CB201902", cbprefix+"CB201902/CB20190201.csv");
            }
        };
        this.DifileMap = new HashMap<String, String>() {
            {
                put("DIDI201705", diprefix+"DIDI201705/DIDI20170501.csv");
                put("DIDI201706", diprefix+"DIDI201706/DIDI20170601.csv");
                put("DIDI201707", diprefix+"DIDI201707/DIDI20170701.csv");
                put("DIDI201708", diprefix+"DIDI201708/DIDI20170801.csv");
                put("DIDI201709", diprefix+"DIDI201709/DIDI20170901.csv");
                put("DIDI201710", diprefix+"DIDI201710/DIDI20171001.csv");
            }
        };
    }

    // read csv head
    private void readHead() throws IOException, CsvValidationException {
        String[] line;
        line = SrcReader.csvReader.readNext();
        if (line == null) {
            this.cancel();
        }
    }

    private long extractEventTimeStamp(String[] line, SourceContext<BikeRide> src) {
        this.eventCount++;
        BikeRide br;
        if(line.length >= 3) {
            br = new BikeRide(line[1], line[2]);
        } else {
            br = new BikeRide(line[0], line[0]);
        }
        long ts = br.getEventTimeStamp();
        if (ts <= this.currentWaterMark) {
            this.lateEvent++;
        }
        if(ts <= LatencyResWriter.watermark) {
            this.drop++;
        }
        src.collectWithTimestamp(br, ts);
        return ts;
    }

    private double calDisorderSeq(long ts) {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(ts));
        this.allCount++;
        if(ts < this.maxTimestamp) {
            this.disCount++;
        } else {
            this.maxTimestamp = ts;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour!=this.prehour) {
            double res = this.disCount/(double)this.allCount;
            this.disCount = 0;
            this.allCount = 0;
            return res;
        } else {
            return 1.1;
        }
    }

    private List<Double> getSeqVector(double dis) {
        if(this.seq.size()==11) {
            this.seq.offer(dis);
        } else {
            this.seq.poll();
            this.seq.offer(dis);
        }
        return new LinkedList<Double>(this.seq);
    }

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {
//        DiDiStrategy strategy = new DiDiStrategy(threshold, maxDelayThreshold);
        String bikePath = this.CBfileMap.getOrDefault(this.DataSet,"/home/lsy/resources/bike/CB201810/CB20181001.csv");

        // init input source data
        new SrcReader(bikePath);

//        URL resultUrl = getClass().getResource("");
        String prePath = "/home/lsy/resources/result/";
        String WaterMarkOutPath = prePath + this.DataSet + "_water.csv";
        String LatencyOutPath = prePath + this.DataSet + "_timelatency.csv";
        String DisOrderOutPath = prePath + this.DataSet +"_disorder.csv";

        // init res writer
        new LatencyResWriter(LatencyOutPath);
        new WatermarkResWriter(WaterMarkOutPath);
        new DisOrderResWriter(DisOrderOutPath);

        NaiveStrategy strategy = new NaiveStrategy(threshold, maxDelayThreshold);
        this.readHead();
        preLate = 0;
        preEvent = 0;
        preTimeStamp = initEvent(sourceContext);

        String[] line;
        while ((line = SrcReader.csvReader.readNext())!=null && isRunning) {
            long ts = this.extractEventTimeStamp(line, sourceContext);
            List<Double> seq = this.getSeqVector(this.calDisorderSeq(ts));
            long l = strategy.make(ts, this.currentWaterMark, countLateRate(ts), seq);
            if(l==-1) {
                continue;
            }
            if (ts - l > currentWaterMark) {
                currentWaterMark = ts - l;
                String[] tmpRes = {String.valueOf(currentWaterMark), String.valueOf(ts)};
                WatermarkResWriter.csvWriter.writeNext(tmpRes);
                sourceContext.emitWatermark(new Watermark(currentWaterMark));
            }
            Thread.sleep(10);
        }
        // 保存实验结果
        String[] tmpRes1 = {String.valueOf(this.lateEvent), String.valueOf(this.eventCount)};
        String[] tmpRes2 = {String.valueOf(this.drop), String.valueOf(this.eventCount)};
        WatermarkResWriter.csvWriter.writeNext(tmpRes1);
        WatermarkResWriter.csvWriter.writeNext(tmpRes2);
    }

    public long initEvent(SourceContext<BikeRide> src) throws IOException, CsvValidationException {
        String[] line;
        line = SrcReader.csvReader.readNext();
        return this.extractEventTimeStamp(line, src);
    }

    public double countLateRate(long ts) {
        double rate = -1.0;
        // 迟到率 更新频率
        if (ts - preTimeStamp >= windowSize * 1000 * monitorPer) {
            rate = (double) (this.lateEvent - this.preLate) / (this.eventCount-this.preEvent);
            this.preLate = this.lateEvent;
            this.preEvent = this.eventCount;
            preTimeStamp = ts;
        }
        return rate;
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
