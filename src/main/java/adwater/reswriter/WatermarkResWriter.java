package adwater.reswriter;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WatermarkResWriter {
    public static Writer writer;
    public static CSVWriter csvWriter;

    public WatermarkResWriter(String outPath) throws IOException {
        File outfile = new File(outPath);
        outfile.createNewFile();
        writer = new FileWriter(outPath);
        csvWriter = new CSVWriter(writer);
    }
}
