package adwater.reswriter;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DisOrderResWriter {
    public static Writer writer;
    public static CSVWriter csvWriter;
    public static long lastCount;
    public static long preEvent;
    public static long lastWindowEnd;

    public DisOrderResWriter(String outPath) throws IOException {
        File outfile = new File(outPath);
        outfile.createNewFile();
        writer = new FileWriter(outPath);
        csvWriter = new CSVWriter(writer);
        lastCount = 0;
        lastWindowEnd = 0;
        preEvent = 0;
    }
}
