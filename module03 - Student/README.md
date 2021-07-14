#Kafka Source and Sink
   - Kafka Producer
   - Kafka Consumer
   - Kafka consume/produce with data manipulation

Kafka is a distributed streaming application that allows asynchronus, stateful, once-and-only-once delivery of messages from any number of producers to any number of consumers. It is sometimes compared to Akka Streams however the two have very different uses.

Kafka is different then other Queue systems because it keeps the messages until either a storage or time constraint is reached. Other Queuing systems will have each messages consumed once and then it is lost. With Kafka you are able to replay messages and have multiple consumers of the same message. 

Kafka is a distributed system which means that each node must maintain information about other nodes so that they can route traffic correctly. To do this, Kafka uses Zookeeper. There is also an RFP under development to provide Kafka a native cluster management.

Each Kafka node relies on Zookeeper to keep a mapping of two main things:

1. The Brokers of the cluster and information about them:
   1. In Kafka, each node is called a "Broker"
   2. Each Broker needs a unique IP address
   3. Each Broker must also have security information, including SSL and authentication parameters
   4. Each Broker may have JMX ports enabled for Java Metrics Collection
2. Topic information:
   1. In Kafka, data streams are separated into "Topics"
   2. Topics are used to identify what messages are produced and consumed
   3. Each topic is further broken down into "Partitions"
   4. Partitions are numbered and used to spread the reading and writing of Topic messages in parallel
   5. Zookeeper maintains a mapping of topics, their Partitions, and their Broker leaders
## Topics and Brokers
For reading and writing to and from Topics you must know two things:
1. At least one Broker IP address or the Zookeeper IP address
2. The Security information for your Broker

The Broker will assign your client to a partition.

Producing and Consuming messages with Kafka requires three things:
1. At least one Broker IP address and port number
2. A Topic name
3. Security credentials as configured

Producing messages is as simple as connecting to the Broker and sending the data. However, the Kafka Cluster has additional work to do. In order to maintain redundancy, Kafka will have replication for each Topic and partition. So when a message is produced, it must be copied to the replica location before a "success" message can be sent to the producer. This can lead to performance issues if there are system or network latencies.

Consuming messages from Kafka requires the same information as producing, but you must also have a "Committable Offset" working. In order to process messages in parallel but not send the same message to two different consumers, Kafka clients will request a message based on three things:
1. The Topic name
2. The Partition they are assigned
3. The next message in the queue, as indicated by the Committable Offset

This Committable Offset number must be maintained somewhere that the client can access. Once the client has successfully processed the message they must update the Committable Offset storage with the "committed" message and thereby allow the shared Committable Offset to increment.

This Committable Offset can either be an external storage location or you can use Kafka's built in version.
##Exercises
For these exercises an in-memory Kafka cluster is spun up by the testing framework. Your code will be called and either asked to Produce a message or Consume one from this testing cluster.

First, Produce a message using the Akka Streams Kafka client:
