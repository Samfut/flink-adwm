package adwater.strategy;

import adwater.datatypes.BikeRide;
import adwater.predictor.ClassVector;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NaiveStrategy {

    public SimpleDateFormat dateFormat;
    public Calendar calendar;

    public NaiveStrategy() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    }

    public ClassVector extracrVector(long timestamp) {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        int hour  = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        ClassVector classVector = new ClassVector(hour, day, dayofweek);
        return classVector;
    }

//    public static void main(String[] args) throws ParseException {
//
//        BikeRide bikeRide = new BikeRide("2018-09-03 13:00:05.269", "2018-09-01 23:00:05.269");
//
//        long ts = bikeRide.startTime.getTime();
//
//        NaiveStrategy naiveStrategy = new NaiveStrategy();
//        ClassVector classVector = naiveStrategy.extracrVector(ts);
//
//        System.out.println(classVector.hour);
//        System.out.println(classVector.day);
//        System.out.println(classVector.dayofweek);
//    }
}
