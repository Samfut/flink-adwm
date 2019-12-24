package adwater.datasource;

import adwater.datatypes.BikeRide;

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

    public BikeSource(boolean isheuristic, long lantency) throws IOException {
        this.isRunning = true;
        this.isheuristic = isheuristic;
        this.lantency = lantency;
        this.eventCount = 0;
    }

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {
        // format date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");

        // read csv head
        String[] line;
        line = SrcReader.csvReader.readNext();
        if (line == null) {
            return;
        }

        // start produce data
        long currentWaterMark = 0L;
        while ((line = SrcReader.csvReader.readNext()) != null && isRunning) {
            this.eventCount++;
            BikeRide br;
            if (line.length >= 3) {
                br = new BikeRide(line[1], line[2]);
            } else {
                br = new BikeRide(line[0], line[0]);
            }
            long ts = br.getEventTimeStamp();
            sourceContext.collectWithTimestamp(br, ts);
            if (ts < currentWaterMark) {
//                System.out.println("ts: " +
//                        String.valueOf(dateFormat.format(new Date(ts))) +
//                        " " + "wm: " +
//                        dateFormat.format(new Date(currentWaterMark)) +
//                        ": interval: " + String.valueOf(currentWaterMark - ts));
                this.lateEvent++;
                continue;
            }
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
