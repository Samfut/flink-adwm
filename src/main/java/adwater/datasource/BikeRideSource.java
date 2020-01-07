package adwater.datasource;

import adwater.datatypes.BikeRide;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class BikeRideSource implements SourceFunction<BikeRide> {

    public static long lateEvent;
    public static long eventCount;

    @Override
    public void run(SourceContext<BikeRide> sourceContext) throws Exception {

    }

    @Override
    public void cancel() {

    }
}
