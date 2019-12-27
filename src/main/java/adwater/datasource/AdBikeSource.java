package adwater.datasource;

import adwater.datatypes.BikeRide;
import adwater.srcreader.SrcReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class AdBikeSource implements SourceFunction<BikeRide> {

    private boolean isRunning;
    private long eventCount;
    private long lateEvent;
    private long currentWaterMark;
    private SimpleDateFormat dateFormat;

    public AdBikeSource() {
        this.isRunning = true;
        this.eventCount = 0L;
        this.lateEvent = 0L;
        this.currentWaterMark = 0L;
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
        this.readHead();

        String[] line;
        while ((line = SrcReader.csvReader.readNext())!=null && isRunning) {
            long ts = this.extractEventTimeStamp(line, sourceContext);

        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
