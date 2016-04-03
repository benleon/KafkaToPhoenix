# KafkaToPhoenix

This project is a demo application that takes data from Kafka, parses it, 
does some some aggregations on a different time window and sends the results to Apache Phoenix. 
Note there is also now a Phoenix Spark plugin available but in this article we will do it the hard way. 
You can use the same approach to send data to pretty much any target.
