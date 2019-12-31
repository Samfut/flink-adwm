package adwater.datasource;

import adwater.datatypes.BikeRide;
import adwater.predictor.ClassVector;
import adwater.predictor.DecisionTreePredictor;
import adwater.reswriter.WatermarkResWriter;
import adwater.srcreader.SrcReader;
import adwater.strategy.NaiveStrategy;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class AdBikeSource implements SourceFunction<BikeRide> {

    private boolean isRunning;
    private long eventCount;
    private long lateEvent;
    private long currentWaterMark;
    private SimpleDateFormat dateFormat;
    private double threshold;
    private long latency;

    public AdBikeSource(long latency, double threshold) {
        this.isRunning = true;
        this.eventCount = 0L;
        this.lateEvent = 0L;
        this.currentWaterMark = 0L;
        this.latency = latency;
        this.threshold = threshold;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
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
        if (ts < this.currentWaterMark) {
            this.lateEvent++;
        }
        src.collectWithTimestamp(br, ts);
        return ts;
    }

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {
        NaiveStrategy strategy = new NaiveStrategy(0.3);
        this.readHead();
        String[] line;
        while ((line = SrcReader.csvReader.readNext())!=null && isRunning) {
            long ts = this.extractEventTimeStamp(line, sourceContext);
            long l = strategy.make(ts, this.currentWaterMark);
            if(l < 0) {
                continue;
            }
            if (ts - l > currentWaterMark) {
                currentWaterMark = ts - l;
                String[] tmpRes = {String.valueOf(currentWaterMark), String.valueOf(ts)};
                WatermarkResWriter.csvWriter.writeNext(tmpRes);
                sourceContext.emitWatermark(new Watermark(currentWaterMark));
            }
        }

        String[] tmpRes = {String.valueOf(this.lateEvent), String.valueOf(this.eventCount)};
        WatermarkResWriter.csvWriter.writeNext(tmpRes);
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
