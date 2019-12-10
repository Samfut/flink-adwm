/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adwater;

import adwater.datasource.BikeSource;
import adwater.datatypes.BikeRide;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

	public static void main(String[] args) throws Exception {
		// set filePath
		URL bikeDataUrl = StreamingJob.class.getClassLoader().getResource("bike/201810-citibike-tripdata.csv");
		String bikeDataPath = bikeDataUrl.getFile();
		System.out.println(bikeDataPath);
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		// 提取时间戳
		DataStream<BikeRide> bikerides =  env.addSource(new BikeSource(bikeDataPath))
                .assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<BikeRide>() {

                    Long currentMaxTimestamp = 0L;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                    @Nullable
                    @Override
                    public Watermark getCurrentWatermark() {
                        return new Watermark(currentMaxTimestamp);
                    }

                    @Override
                    public long extractTimestamp(BikeRide bikeRide, long l) {
                        long ts = bikeRide.getEventTimeStamp();
                        currentMaxTimestamp = Math.max(ts, currentMaxTimestamp);
                        return ts;
                    }
                });

		bikerides.keyBy(x -> x.id).window(TumblingEventTimeWindows.of(Time.minutes(1)))
                .apply(new WindowFunction<BikeRide, String, Integer, TimeWindow>() {
                    @Override
                    public void apply(Integer integer, TimeWindow timeWindow, Iterable<BikeRide> iterable, Collector<String> collector) throws Exception {
                        Iterator<BikeRide> it = iterable.iterator();
                        int count = 0;
                        while (it.hasNext()) {
                            count++;
                            it.next();
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String result = sdf.format(timeWindow.getStart()) + "<->" + sdf.format(timeWindow.getEnd()) + " \n"+
                                "窗口内元素个数: " + count;
                        System.out.println(result);
                        collector.collect(result);
                    }
                });
        bikerides.print();
		env.execute("Flink Streaming Java API Skeleton");
	}
}


