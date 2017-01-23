# KV511
A distributed key-value storage system. 
This Project contains a server side program and a client side program.
The server is written in Java and the client is written in Python.

## Server
The server is implemented in two versions: multi-threaded version and single-threaded version.
The multi-threaded version uses blocking network IO.
The single-threaded version uses asynchronous network IO.
Besides these two modules, a cache management module based on LRU algorithm is implemented.
The two versions uses the cache module to store and retrieve key-value pairs.


### KVConstant 
In KVConstant we defined the constants such as port number for our project.


### Single-threaded Version
In this version JAVA NIO is used to achieve the asynchronous no-blocking network IO.
The server contains the following parts:
 - Channel
 - Selector 
 - Buffer

####  Channels:
- Channel works like stream. From channel data can be read into a Buffer. Data can also be written from a Buffer into a Channel.
- In this Project we use SocketChannel and ServerSocketChannel to do the TCP network IO.

####  Selectors:
- A Selector allows a single thread to handle multiple Channels.
- To use a Selector we register the Channel with it. Then we call it's select() method. 
- This method will block until there is a event ready for one of the registered channels.
- Once the method returns, the thread can then process these events.

####  The working process of our program:
- Initiate the selector 
- Initiate the ServerSocketChannel 
- Set the ServerSocketChannel as no-blocking 
- Bind the ServerSocketChannel to the IP and Port
- Register a Selector to ServerSocketChannel 
- Make the ServerSocketChannel  listen to the event of accept()
- Get the SocketChannel after the connection 
- Respond to the read/wirte event from the SocketChannel 

####  KV value processing(KV pair validation):
- Read the input request from the client.
- Get the Instruction and the Key and conressponding value.
- When it is a Put instruction then put the key and value into the LRU Cache.
- When the instruction is get, then use the key to find the corresponding value stored in the LRU cache. 
- when the instruction is anything else than "get" or "put" return "Wrong Instrcution"


### Multi-threaded Version
This version accepts a parameter to define the number of threads pre-created.
The main thread creates a thread pool and handles each new session using a separate thread.
The program contains two parts:
- main thread: `Server` class
- client handler: `ClientHandler` class

### Main thread
The main thread listens to port 4444 and accepts requests if the number of sessions deos not exceed the maximum number of sessions.
When a new TCP connection is established, the main thread pass the socket as a parameter to the client handler, and runs a new thread to handle the requests.

#### Client Handler
In `ClientHandler` class, clients' requests are handled. 
Because there can be more than one request in one session, the handler first extracts request messages from the socket stream in `handleClient()`.
Then it parse each request message in `handleRequest()` and invoke `put()` or `get()` correspondingly.
The client handler uses `LRUCache` to store or retrieve key-value pairs.


### LRU Cache
#### The method supported:
 - get(string key) -- return "error"  when there is a miss 
 - put(string key, string val) -- return "okay" when the instruction is correct
 - remove(string key) -- remove the kv pair with the key value
 - when the instruction is anything else than "get" or "put" return "Wrong Instrcution"

The LRU cache is a hash table of keys and double linked list of nodes. When the the cache reaches its capacity, remove the earliest kv pair to make space for the coming kv pair.
The hash table makes the time of get() to be O(1). The double linked list of nodes make the add and remove operation with time complexity of O(1).
In our project we set the capacity of the cache with 100 when constrcut the constructor of the LRUcache class.



## Client
The client side program contains three modules:
- client main module: `client.py`
- randomness tools module: `rand.py`
- performance evaluation module: `performance_test.py`


### Client Main Module
This module offers network IO and multi-thread requests.
It also records all the responses from server into a file.


### Randomness Tools Module
This module generates random parameters for `put` and `get` requests and random requests for sessions.


### Performance Evaluation Module
This module generates different configurations for batch performance tests.
For example, it can configure the main module to send all `put` requests.
