# KafkaToPhoenix

This project is a demo application that takes data from Kafka, parses it, 
does some some aggregations on a different time window and sends the results to Apache Phoenix. 
Note there is also now a Phoenix Spark plugin available but in this article we will do it the hard way. 
You can use the same approach to send data to pretty much any target.

The application works as is with the HDP 2.3.4 sandbox. 
To run it you need to copy the phoenix-4.4.0.2.3.4.0-3485-client.jar from the /usr/hdp/2.3.4.0-3485/phoenix folder of your sandbox into the folder of the project. 

You can then build the application with sbt assembly

Copy the application to your execution directory cp target/scala-2.10/KafkaToPhoenix-assembly-1.0.jar xxx

Prepare Kafka:

/usr/hdp/2.3.4.0-3485/kafka/bin/kafka-topics.sh--zookeeper localhost:2181 --topic salestopic 

Prepare Phoenix:

Run SQLLINE: 

/usr/hdp/2.3.4.0-3485/phoenix/bin/sqlline.py sandbox.hortonworks.com:2181:/hbase-unsecure

Create the two tables:

CREATE TABLE SALES ( ID BIGINT NOT NULL,  
		     DATE DATE,  
     		     FIRSTNAME VARCHAR(200),  
		     LASTNAME VARCHAR(200),  
		     PRODUCT VARCHAR(200),  
		     AMOUNT FLOAT
	CONSTRAINT PK PRIMARY KEY (ID));
	
and

CREATE TABLE PRODUCTSALES ( PRODUCT VARCHAR(200) NOT NULL,
			    DATE DATE NOT NULL , 
			    AMOUNT FLOAT   
	CONSTRAINT PK PRIMARY KEY (PRODUCT, DATE));

Run the application with:

spark-submit --master local  --class org.apache.spark.ben.KafkaToPhoenix KafkaToPhoenix-assembly-1.0.jar

Test application with:

/usr/hdp/2.3.4.0-3485/kafka/bin/kafka-console-producer.sh  --broker-list sandbox.hortonworks.com:6667 --topic salestopic

And copy and paste some delimited rows into it like ( make sure the id is unique ):

01|2016-04-01 18:20:00|Klaus|Kinski|Coca-Cola|0.89

See results in SQLLINE:

SELECT * from SALES;
SELECT * FROM PRODUCTSALES;
