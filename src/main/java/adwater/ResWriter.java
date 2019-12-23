package adwater;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ResWriter {
    public static Writer writer;
    public static CSVWriter csvWriter;

    public ResWriter(String outPath) throws IOException {
        File outfile = new File(outPath);
        outfile.createNewFile();
        writer = new FileWriter(outPath);
        csvWriter = new CSVWriter(writer);
    }
}
