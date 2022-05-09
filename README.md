# microsvcs-saga-choreography

# download kafka server utility from https://kafka.apache.org/downloads , this also include zookeeper server installed
# Need to start zookeeper server before apache kafka
  C:\softwares\kafka-3.1.0-src\bin\windows\zookeeper-server-start.bat C:\softwares\kafka-3.1.0-src\config\zookeeper.properties
# Now start kafka server
 C:\softwares\kafka-3.1.0-src\bin\windows\kafka-server-start.bat C:\softwares\kafka-3.1.0-src\config\server.properties
# create topic (optionally , not required to do this step for this project as its done by rest api's on our behalf automatically)
  C:\softwares\kafka-3.1.0-src\bin\windows\kafka-topics.bat --create --bootstrap-server  localhost:9092 --replication-factor 1 --partitions 1 -topic Aditya
  kafka-topics.bat --create --bootstrap-server  localhost:9092 --replication-factor 1 --partitions 1 -topic Aditya
# start kafka console consumer to view the pulblished event (optionally , not required to do this step for this project as this can be viewed with "Offset Explorer" tool)
C:\softwares\kafka-3.1.0-src\bin\windows\kafka-console-consumer.bat --topic Aditya --from-beginning --bootstrap-server localhost:9092

install Offset Explorer 2.2 ,to view the topics created in kafka culster
https://www.kafkatool.com/download.html
