package adwater.datasource;

import adwater.datatypes.BikeRide;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BikeSource implements SourceFunction<BikeRide> {

    private String filePath;
    private String outPath;
    private boolean isRunning;
    private boolean isheuristic;
    private long lantency;
    private long eventCount;
    private long lateEvent;

    public BikeSource(String filePath, String outPath, boolean isheuristic, long lantency) throws IOException {
        this.filePath = filePath;
        this.outPath = outPath;
        this.isRunning = true;
        this.isheuristic = isheuristic;
        this.lantency = lantency;
        this.eventCount = 0;
        File outfile = new File(this.outPath);
        outfile.createNewFile();
    }

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {
        // 格式化时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");

        // 从csv读取数据
        Reader BikeData = new FileReader(filePath);
        CSVReader BikeDataReader = new CSVReader(BikeData);

        // 将结果写入csv文件
        Writer writer = new FileWriter(outPath);
        CSVWriter csvWriter = new CSVWriter(writer);

        String[] line;
        // 先读取csv head
        line = BikeDataReader.readNext();
        if (line == null) {
            return;
        }
        long currentWaterMark = 0L;
        while ((line = BikeDataReader.readNext()) != null && isRunning) {
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
                System.out.println("ts: " +
                        String.valueOf(dateFormat.format(new Date(ts))) +
                        " " + "wm: " +
                        dateFormat.format(new Date(currentWaterMark)) +
                        ": interval: " + String.valueOf(currentWaterMark - ts));
                this.lateEvent++;
                continue;
            }
            if (isheuristic) {
                if (ts - lantency > currentWaterMark) {
                    currentWaterMark = ts - lantency;
                    String[] tmpRes = {String.valueOf(currentWaterMark), String.valueOf(ts)};
                    csvWriter.writeNext(tmpRes);
                    sourceContext.emitWatermark(new Watermark(currentWaterMark));
                }
            }
        }
        String[] tmpRes = {String.valueOf(this.lateEvent), String.valueOf(this.eventCount)};
        csvWriter.writeNext(tmpRes);
        csvWriter.close();
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
