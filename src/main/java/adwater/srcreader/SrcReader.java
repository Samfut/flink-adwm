package adwater.srcreader;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class SrcReader {
    public static String srcPath;
    public static Reader srcReader;
    public static CSVReader csvReader;

    public SrcReader(String filePath) throws IOException {
        srcPath = filePath;
        srcReader = new FileReader(srcPath);
        csvReader = new CSVReader(srcReader);
    }
}
