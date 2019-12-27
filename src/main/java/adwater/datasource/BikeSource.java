package adwater.datasource;

import adwater.datatypes.BikeRide;

import com.opencsv.exceptions.CsvValidationException;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import adwater.reswriter.WatermarkResWriter;
import adwater.srcreader.SrcReader;

public class BikeSource implements SourceFunction<BikeRide> {

    private boolean isRunning;
    private boolean isheuristic;
    private long lantency;
    private long eventCount;
    private long lateEvent;
    private long currentWaterMark;

    public BikeSource(boolean isheuristic, long lantency) throws IOException {
        this.isRunning = true;
        this.isheuristic = isheuristic;
        this.lantency = lantency;
        this.eventCount = 0;
        this.currentWaterMark = 0L;
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
        if (line.length >= 3) {
            br = new BikeRide(line[1], line[2]);
        } else {
            br = new BikeRide(line[0], line[0]);
        }
        long ts = br.getEventTimeStamp();
        if(ts < this.currentWaterMark) {
            this.lateEvent++;
//                System.out.println("ts: " +
//                        String.valueOf(dateFormat.format(new Date(ts))) +
//                        " " + "wm: " +
//                        dateFormat.format(new Date(currentWaterMark)) +
//                        ": interval: " + String.valueOf(currentWaterMark - ts));
        }
        src.collectWithTimestamp(br, ts);
        return ts;
    }

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {
        // format date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");

        this.readHead();

        // start produce data
        String[] line;
        while ((line = SrcReader.csvReader.readNext()) != null && isRunning) {
            long ts = this.extractEventTimeStamp(line, sourceContext);

            if (isheuristic) {
                if (ts - lantency > currentWaterMark) {
                    currentWaterMark = ts - lantency;
                    String[] tmpRes = {String.valueOf(currentWaterMark), String.valueOf(ts)};
                    WatermarkResWriter.csvWriter.writeNext(tmpRes);
                    sourceContext.emitWatermark(new Watermark(currentWaterMark));
                }
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
