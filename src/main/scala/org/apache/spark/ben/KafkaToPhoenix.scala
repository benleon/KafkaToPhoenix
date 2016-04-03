/**
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
package org.apache.spark.ben

import java.util.Calendar

import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions
import org.apache.spark.streaming.kafka.KafkaUtils

import kafka.serializer.StringDecoder

/**
 * @author bleonhardi
 * Sample Application that takes delimited data from Kafka,
 * parses it into a Scala object, aggregates it by product
 * and writes the result to Apache Phoenix
 */
object KafkaToPhoenix {

  val logger: Logger = Logger.getLogger("org.apache.spark.ben.KafkaToPhoenix");

  logger.info(s"Starting up");

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KafkaToPhoenix")

    
    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.checkpoint("/tmp/checkpoint");

    // create a kafka stream
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "sandbox:6667", "group.id" -> "kafka_phoenix");
    val topics = Array("salestopic").toSet;

    var directKafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    // first we need to remove the empty key
    var inputStream = directKafkaStream.map(_._2); 

    inputStream = inputStream.repartition(12);

    // now we parse the object, note the use of mapPartitions to only instantiate the Parser object
    // once for each partition and not for every row.
    var parsedStream = inputStream.mapPartitions {
      rows =>
        // parser is a simple Parser Scala class.
        val parser = new Parser();
        rows.map { row => parser.parse(row) }
    }

    // Now we need to write the results to Phoenix. 
    // Note the three dimensions 
    // - foreachRDD ( DSTreams can have multiple batches )
    // - foreachPartition ( we need to use this to create our Phoenix Connection since he object
    //   would otherwise be serialized and sent over the network
    // - foreach execute the upsert for each row
    parsedStream.foreachRDD(
      rdd => {
        rdd.foreachPartition(rows => {
          val phoenixConn = new PhoenixForwarder();
          rows.foreach(row => phoenixConn.sendSales(row))
          phoenixConn.closeCon();
        })
      })

    // now we want to aggregate the data on a 60 sec level by product
    // to do this DStreams provide the reduceByKeyAndWindow function.
    var reducedStream = parsedStream
      .map(record => (record.product, record.amount))
      .reduceByKeyAndWindow((acc: Double, x: Double) => acc + x, Seconds(60), Seconds(60))

    // Similar to the send above, only difference is that we now need a 
    // current date. Note that we create it in the foreachRDD part and not in foreachPartition
    // this ensures we have the same date for all products which may be in different partitions
    // This means the curDate object is serialized and sent over the network to each RDD
    reducedStream.foreachRDD(
      rdd => {
        val curDate = Calendar.getInstance().getTime();
        rdd.foreachPartition(rows => {
          val phoenixConn = new PhoenixForwarder();
          rows.foreach { row => phoenixConn.sendAgg(row._1, curDate, row._2) }
          phoenixConn.closeCon();
        })
      })
    //parsedStream.print();

    ssc.start()
    ssc.awaitTermination()

  }

}