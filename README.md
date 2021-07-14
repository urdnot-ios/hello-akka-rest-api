Ongoing project to teach/learn about streaming with Scala using Akka and Circe.

Modules are
1. Basic setup with:
    - SBT
    - ScalaTest
    - AkkaHTTP
    - Circe
1.1. HTTP Connections with Akka HTTP Server and Akka Streams client
   - Throttling
   - Headers
   - SSL
   - Response handling
   - Back off and retry
   - Parallelism
2. JSON using Circe
    - Basic JSON encode, decode
    - Intermediate JSON
    - Complex JSON
    - Error handling
3. Kafka Source and Sink
   - Kafka Consumer
   - Kafka Producer
   - Kafka consume/produce with data manipulation
   - mid-stream lookup
4. Combine Sources and Sinks:
      - Source from File, HDFS, S3
      - Source from JDBC
      - Source from NoSQL
      - Kinesis - AWS Kafka
      - Firebase - Google Kafka
      - WebSocket Source
   - Sinks
      - Sink to file
      - Sink to S3
      - Sink to JDBC
      - Sink to NoSQL
      - Write to Kinesis
      - Write to Firebase
      - Write to WebSockets
5. Setup Flink
   - Flink Real Time Data Queries