#Kafka Source and Sink
   - Kafka Producer
   - Kafka Consumer
   - Kafka consume/produce with data manipulation

Kafka is a distributed streaming application that allows asynchronus, stateful, once-and-only-once delivery of messages from any number of producers to any number of consumers. It is sometimes compared to Akka Streams however the two have very different uses.

Kafka is also different from other queuing systems because it keeps the messages until either a storage or time constraint is reached. Other queuing systems will have each message consumed once, after which it is lost. With Kafka you are able to replay messages and have multiple consumers of the same message. 

Kafka is a distributed system which means that each node must maintain information about other nodes so that they can route traffic correctly. To do this, Kafka uses Zookeeper. There is also an RFP under development to provide Kafka a native cluster management.

Each Kafka node relies on Zookeeper to keep a mapping of two categories of information:

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

The first step in connecting to Kafka is to create an `application.conf` file with the appropriate settings. One has been provided for you. Look it over and answer the following:

What information is missing that might be needed?

With the `application.conf` ready, build 3 objects for the class:
1. KafkaLauncher
2. Consumers
3. Producers

The KafkaLauncher is the starting point for the flow. This is where you set up your Kafka parameters and call the other objects. Create the Actor and config loaders as follows:
1. Create the actor system
```  
implicit val system: ActorSystem = ActorSystem.create("kafka_producer")
```
2. Create the producer and consumer configurations from the application.conf file:
```
val producerConfig: Config = system.settings.config.getConfig("akka.kafka.producer")
val consumerConfig: Config = system.settings.config.getConfig("akka.kafka.consumer")
```
3. Create the producer and consumer settings from those configurations:
```
val producerSettings: ProducerSettings[String, String] = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)
val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)
```
4. Finally, create a topic variable that you will use for each of the object. This can be any string. You normally add this separate from the rest of the Kafka configuration.

###Producers
Now you are ready to create a Producer and a Consumer. In the Producers object,

First, Produce a message using the Akka Streams Kafka client by creating a `produce` function with two parameters: 
1. The topic as a `String`
2. The producer settings as a `ProducerSettings[String, String`
3. It will return a `Future[Done]`

Inside, build an Akka Stream sourcing the number range `1 to 100`
Next, map those numbers so that each number is a record produced to Kafka
Finally, run it with the simple `Producer.plainSink()` and pass it your producer settings.

```  
def produce(topic: String, producerSettings: ProducerSettings[String, String]): Future[Done] = {
    Source(1 to 100)
      .map(value => new ProducerRecord[String, String](topic, value.toString))
      .runWith(Producer.plainSink(producerSettings))
  }
```
Questions:
1. Why does the `ProducerSettings` take two type parameters? What happens if you change them?
2. What `implicit val`s are needed to make this work?

###Consumers
Consumers usually do some kind of work with the records and so you will need two methods in the object:
1. `consume`
2. `business`

First, make the `consume` method and pass it two parameters: `topic` and `ConsumerSettings`. It will return `Source[Int, Consumer.Control]`

Because it is returning a Source, you must call it differently:

`<your consumer>.toMat(Sink.ignore)(DrainingControl.apply)
.run()`

Inside the `consume` method, you must source from Kafka and then run the business logic on the records:
```
<the Akka Kafka Consumer>.plainSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(10) { msg =>
        business(msg.key, msg.value)
      }
```
Question: What is `mapAsync` and how is it different from `map`?

Next, create a `business` method that returns the value as an int:
```    
log.info(value.mkString)
Future.successful(value.toInt)
```

Questions:
1. What are the types of the key and the value? Can they be changed?
2. What other information can you get from a kafka message?

At this point the IntegrationSpec tests should all run and pass!

