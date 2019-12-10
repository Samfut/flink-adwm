package adwater.datasource;

import adwater.datatypes.BikeRide;

import com.opencsv.CSVReader;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import java.io.*;

public class BikeSource implements SourceFunction<BikeRide> {

    private String filePath;
    private boolean isRunning;

    public BikeSource(String filePath) {
        this.filePath = filePath;
        this.isRunning = true;
    }

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {
        String [] line;
        Reader BikeData = new FileReader(filePath);
        CSVReader BikeDataReader = new CSVReader(BikeData);
        line = BikeDataReader.readNext();
        if(line == null) {
            return;
        }
        while ((line = BikeDataReader.readNext()) != null && isRunning) {
            BikeRide br = new BikeRide(line[1], line[2]);
            Thread.sleep(100);
            sourceContext.collect(br);
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
