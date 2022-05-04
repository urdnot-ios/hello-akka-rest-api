#INTRODUCTION
Welcome to the preface module! It has come to my attention that many schools do not teach the basics of computer networking! Since streaming data relies so 
heavily on computers being networked, it seemed appropriate to review how that happens. With this, many of the future modules will make much more sense.

1. First, we'll do a simple UDP listener and sender
2. Next, we'll make a TCP listener

#Overview
My first jobs in the late 90's were building and running computer networks. In those days there were several protocols used. Eventually everyone agreed that the "IP" stack would be the worldwide standard and the rest of the protocols fell away.

The "IP" stands for "Internet Protocol" and is a set of specifications to determine how to get data from one computer to another, no matter where in the world they are, as long as they are both on an IP network and those networks are connected to the wild world of IP.

Today, most of your code is unaware of the underlying network and can just assume it all works. However, there are many steps in between and even on the systems themselves different libraries are called.

##TCP and UDP
We typically think of the Internet as "TCP/IP". The "TCP" stands for "Transmission Control Protocol" and is the backbone of most of our connections. However, UDP is still playing an important role and is important to understand.

The "UDP" stands for User Datagram Protocol (but the "U" is often referred to as "Unreliable"). The key difference between the two is that TCP has a multi-message exchange that is used to establish the connection and send the rest of the message data. UDP is a single packet "fire and forget" model where each packet is a stand-alone message with no built-in relationship with any other message.

###Why God, why???
There are good reasons to have and use both.
####TCP
TCP is the reliable work horse of network communications. Before data is sent, a series of messages are exchanged to establish the session and optionally verify identities and establish encryption keys.

Each TCP session consists of
1. a source IP address
2. a destination IP address
3. verification of the source and destination addresses
4. a source TCP port
5. a destination TCP port
6. source and destination mutual verification and sequence

Then the messages are sent and each one is verified as having arrived. Any dropped or out of order messages will be found and a request made to re transmit.

This all takes time. Every message requires at least 2 packets and that does not include the setup messages. However, the payoff is reliability.

####UDP
UDP is the wild child with no sense of responsibility. UDP messages are each fully self-contained and do not guarantee arrival. No sequence is provided and messages can arrive in any order.

Each UDP message includes
1. the source IP address
2. the destination IP address
3. the source UDP port
4. the destination UDP port

The payoff is substantially less time to send data. Many of UDP's non-session shortcomings can be overcome at higher levels of the code where the programmer can write sequences and retries. There is even a TLS option for encryption.

##The Code
To get familiar with these protocols, let's write some code. Akka Streams has built in TCP and UDP listeners that we can use to send, receive, and process both kinds of messages.

First, open `application.conf` and notice that there is an `akka.server` section. This is where we set up the port and IP address to listen on. We can use the same for both TCP and UDP because they are different protocols.

The traits we will use are `ListenUdp` and `SendUdp` 

Open `ListenUdp` first. There are a few elements to build:
1. the Source
2. the UDP listener
3. the message processor

###The Source
This is the starting point for akka streams. For network listeners, the source is the TCP or UDP socket itself, relying on the underlying Network Framework to do the heavy lifting.

In this case, since it is UDP and can be unreliable, we use the built in `Source.maybe`

```scala
val serverSource: Source[Datagram, Promise[Option[Datagram]]] = Source.maybe
```
Once that is built, we need to set-up the address and port, called an `InetSocketAddress`. This will bind the source to our computer's IP address and a specific port. IP addresses can have multiple listeners but each TCP and UDP port can only have one bound application listening.
```scala
val bindToLocal = new InetSocketAddress("localhost", port)
```
Finally, we need an Akka Streams `Flow` to capture and process the data
```scala
val bindFlow: Flow[Datagram, Datagram, Future[InetSocketAddress]] = Udp.bindFlow(bindToLocal)
```
Once these three are built, we can process the message. The UDP message is called a `Datagram` which is the naming convention used by the TCP/IP/UDP network protocols. In Akka Streams, the `Datagram` type provides the `remote` and `message` elements. The `remote` is the IP addressa and port of the source of the message. The `message` element is the actual payload of the packet.

The Flow will run a map to process each message that arrives. Log the information about the message and the source host. 
```scala 
Flow[Datagram].map{message =>
log.info("source:  " + message.remote.getHostName + ":" + message.remote.getPort)
log.info("message: " + message.data.utf8String)
```
This will log the source host and port as well as the messages sent.

Test it with the `SendUdpSuite` tests.