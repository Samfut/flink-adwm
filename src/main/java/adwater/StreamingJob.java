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

import adwater.datasource.AdBikeSource;
import adwater.datasource.BikeSource;
import adwater.datatypes.BikeRide;
import adwater.reswriter.DisOrderResWriter;
import adwater.reswriter.LatencyResWriter;
import adwater.reswriter.WatermarkResWriter;
import adwater.srcreader.SrcReader;
import adwater.trigger.EventTimeRecordTrigger;
import org.apache.commons.cli.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

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
//        URL resultUrl = StreamingJob.class.getClassLoader().getResource("");
//        String WaterMarkOutPath = resultUrl.getFile() + "/water.csv";
//        String LatencyOutPath = resultUrl.getFile() + "/timelatency.csv";
//        String DisOrderOutPath = resultUrl.getFile() + "/disorder.csv";


//        URL bikeDataUrl = StreamingJob.class.getClassLoader().getResource("bike/CB201902/CB20190201.csv");
//        String bikeDataPath = bikeDataUrl.getFile();


        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // init input source data
//        new SrcReader(bikeDataPath);

//        // init res writer
//        new LatencyResWriter(LatencyOutPath);
//        new WatermarkResWriter(WaterMarkOutPath);
//        new DisOrderResWriter(DisOrderOutPath);

        // init datasource
        boolean isheuristic = true;
        // 延迟等待参数
        long lantency = 1000;
        // 窗口大小参数
        long windowSize = 60;

//        BikeSource bs =  new BikeSource(isheuristic, lantency);
        AdBikeSource bs =  new AdBikeSource(0.3, windowSize, 10, 4700);

        DataStream<BikeRide> bikerides = env.addSource(bs);

        // store drop data
        OutputTag<BikeRide> outputTag = new OutputTag<BikeRide>("late-data"){};
        // simple output per window count
        bikerides.keyBy(x -> x.id).window(TumblingEventTimeWindows.of(Time.seconds(windowSize)))
                .trigger(EventTimeRecordTrigger.create())
                .sideOutputLateData(outputTag)
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
                        String result = sdf.format(timeWindow.getStart()) + "<->" + sdf.format(timeWindow.getEnd()) + " \n" +
                                "窗口内元素个数: " + count;
                        System.out.println(result);
                        collector.collect(result);
                    }
                });


        // exec system
        env.execute("Flink Streaming Java API Skeleton");

        // store res(wm/la) to disk
        LatencyResWriter.csvWriter.close();
        WatermarkResWriter.csvWriter.close();
        DisOrderResWriter.csvWriter.close();
    }
}


