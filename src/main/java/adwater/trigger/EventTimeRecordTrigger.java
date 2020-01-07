package adwater.trigger;

import adwater.datasource.BikeRideSource;
import adwater.datasource.BikeSource;
import adwater.datatypes.BikeRide;
import adwater.reswriter.DisOrderResWriter;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import adwater.reswriter.LatencyResWriter;

public class EventTimeRecordTrigger<W extends Window> extends Trigger<Object, TimeWindow> {
    private static final long serialVersionUID = 1L;

    private EventTimeRecordTrigger() {
    }

    public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        if (window.maxTimestamp() <= ctx.getCurrentWatermark()) {
            String[] tmpRes = {String.valueOf(ctx.getCurrentWatermark()), String.valueOf(window.getEnd())};
            LatencyResWriter.csvWriter.writeNext(tmpRes);
            LatencyResWriter.watermark = ctx.getCurrentWatermark();
            String[] tmpRes1 = {
                    String.valueOf(window.getEnd()),
                    String.valueOf(BikeRideSource.lateEvent-DisOrderResWriter.lastCount),
                    String.valueOf(BikeRideSource.eventCount-DisOrderResWriter.preEvent)
            };
            DisOrderResWriter.lastCount = BikeRideSource.lateEvent;
            DisOrderResWriter.preEvent = BikeRideSource.eventCount;
            DisOrderResWriter.csvWriter.writeNext(tmpRes1);
            return TriggerResult.FIRE;
        } else {
            ctx.registerEventTimeTimer(window.maxTimestamp());
            return TriggerResult.CONTINUE;
        }
    }

    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) {
        String[] tmpRes = {String.valueOf(ctx.getCurrentWatermark()), String.valueOf(window.getEnd())};
        LatencyResWriter.watermark = ctx.getCurrentWatermark();
        LatencyResWriter.csvWriter.writeNext(tmpRes);
        String[] tmpRes1 = {
                String.valueOf(window.getEnd()),
                String.valueOf(BikeRideSource.lateEvent-DisOrderResWriter.lastCount),
                String.valueOf(BikeRideSource.eventCount-DisOrderResWriter.preEvent)
        };
        DisOrderResWriter.lastCount = BikeRideSource.lateEvent;
        DisOrderResWriter.preEvent = BikeRideSource.eventCount;
        DisOrderResWriter.csvWriter.writeNext(tmpRes1);
        return time == window.maxTimestamp() ? TriggerResult.FIRE : TriggerResult.CONTINUE;
    }

    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
        ctx.deleteEventTimeTimer(window.maxTimestamp());
    }

    public boolean canMerge() {
        return true;
    }

    public void onMerge(TimeWindow window, OnMergeContext ctx) {
        long windowMaxTimestamp = window.maxTimestamp();
        if (windowMaxTimestamp > ctx.getCurrentWatermark()) {
            ctx.registerEventTimeTimer(windowMaxTimestamp);
        }

    }

    public String toString() {
        return "EventTimeRecordTrigger()";
    }

    public static EventTimeRecordTrigger create() {
        return new EventTimeRecordTrigger();
    }
}