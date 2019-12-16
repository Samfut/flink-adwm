package adwater.datatypes;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BikeRide implements Serializable {

    public Date startTime;
    public Date stopTime;
    public int id;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");

    public BikeRide(){}

    public BikeRide(String startTime, String stopTime){
        try {
            Date startDate = dateFormat.parse(startTime);
            Date stopDate = dateFormat.parse(stopTime);
            this.startTime = startDate;
            this.stopTime = stopDate;
            this.id = 1;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public long getEventTimeStamp(){
        Timestamp ts = new Timestamp(this.startTime.getTime());
        return ts.getTime();
    }

    public String toString() {
        return "Event Time: " + dateFormat.format(this.startTime);
    }

}
