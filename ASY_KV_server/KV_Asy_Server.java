package javaio;


import java.io.IOException;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import javaio.LRUCache;


public class KV_Asy_Server{

    private ServerSocketChannel server = ServerSocketChannel.open();
    private Selector selector = Selector.open();
    private SelectionKey selection;
    private Map<Channel, String> dataMap = new HashMap<Channel, String>();
    LRUCache KVpairLRU = new LRUCache(100);

    private KV_Asy_Server() throws IOException {
        logln("Starting the KV Asyn server");
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress(KVConstants.PORT));
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void start() throws IOException {
        logln("KV Aysn Server started");
        while (true) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                selection = keyIterator.next();
                keyIterator.remove();
                if (!selection.isValid()) {
                    invalid();
                } else if (selection.isAcceptable()) {
                    createClient();
                } else if (selection.isReadable()) {
                    read();
                } else if (selection.isWritable()) {
                    write();
                }
            }
        }
    }

    private void invalid() throws IOException {
        logln("Invalid selection");
        selection.channel().close();
        selection.cancel();
    }

    private void createClient() throws IOException {
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel client = (SocketChannel) selection.channel();
        int readed = client.read(buffer);
        StringBuilder sb = new StringBuilder();
        if (readed == -1) {
            logln("Connection closed by client");
            client.close();
            selection.cancel();
            return;
        } else {
            buffer.flip();
            sb.append(new String(buffer.array(), "UTF-8"));
            buffer.clear();
        }
        readed = client.read(buffer);
        while (readed != -1 && readed != 0) {
            buffer.flip();
            sb.append(new String(buffer.array(), "UTF-8"));
            buffer.clear();
            readed = client.read(buffer);
        }
        logln("Incoming:");
        
        logln("");
        log(sb.toString().trim());
        
        // put the income information to the datamap
        
        dataMap.put(client, sb.toString());
        selection.interestOps(SelectionKey.OP_WRITE);
        
        // If the instruction is put then put the key and value into the LRUCache
        
        String inputMeassage = sb.toString();
        
        String[] messageParts = inputMeassage.split(" ");
        
        String instruction = messageParts[0];
        
        if((messageParts.length == 3) && (instruction.toLowerCase().equals("put") )){
        
        String key_str = messageParts[1];
        String value_str = messageParts[2].trim();
        // put the kv pair into the LRUCache of size 100
        
        
        KVpairLRU.set(key_str,value_str);
        }
        
    }

    private void write() throws IOException {
        SocketChannel client = (SocketChannel) selection.channel();
        String data = dataMap.get(client);
        String[] messageParts = data.split(" ");
        String value_str = "Okay";
        if(messageParts.length >1){
        	String key_str = messageParts[1].trim();
            String instruction = messageParts[0];
           // String key_str = messageParts[1];
        	//value_str = putKVpair.get(key_str);
            if( messageParts.length == 2 ){
            	if(instruction.toLowerCase().equals("get")){
            		value_str = KVpairLRU.get(key_str);
            	}else{
            		value_str = "Wrong instruction";
            	}
            	
            	
            }
        }
        
        
        
        
        
        logln("Outcomming:");
        logln("");
        log(value_str);
        client.write(ByteBuffer.wrap(value_str.getBytes()));
        selection.interestOps(SelectionKey.OP_READ);
    }

    private static void logln(String s) {
        System.out.println(s);
    }

    private static void log(String s) {
        System.out.print(s);
    }

    public static void main(String[] args) throws IOException {
        new KV_Asy_Server().start();
    }
}