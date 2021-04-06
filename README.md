Ongoing project to teach/learn about streaming with Scala using Akka and Circe.

Modules are
1. Basic setup with:
    - SBT
    - ScalaTest
    - AkkaHTTP
    - Circe
2. JSON using Circe
    - Basic JSON encode, decode
    - Intermediate JSON
    - Complex JSON
    - Error handling
3. HTTP Connections with Akka HTTP Server and Akka Streams client
   - Throttling
   - Headers
   - SSL
   - Response handling
   - Back off and retry
   - Parallelism
4. Sourcing from a non-stream source
    - Source from File
   - Source from S3
   - Source from JDBC
   - Source from NoSQL
5. Sourcing from a stream source
    - Kafka
   - Kinesis
   - Firebase
   - WebSocket Source
6. Sinking to a non-stream sink
    - Sink to file
   - Sink to S3
   - Sink to JDBC
   - Sink to NoSQL
7. Sinking to a streaming sink
    - Write to Kafka
   - Write to Kinesis
   - Write to Firebase
   - Write to WebSockets
8. Mid-Stream Enrichment with flows and external lookups
    - Log flow
   - NoSQL flow
   - DB Flow
9. Query data as it streams