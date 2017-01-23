package javaio;
import java.util.*;
public class LRUCache {
	
	  public class Node
	  {
	      String key;
	      String value;
	      Node pre;
	      Node next;
	      public Node(String key, String value)
	      {
	          this.key = key;
	          this.value = value;
	          
	      }
	  }


	      int capacity;
	      HashMap<String, Node> map = new HashMap<String,Node>();
	      Node head = null;
	      Node end = null;
	      
	      public LRUCache(int capacity) {
	          this.capacity = capacity;
	          
	      }
	      
	      public String get(String key) {
	          
	          if(map.containsKey(key)){
	              Node n = map.get(key);
	              remove(n);
	              setHead(n);
	              return n.value;
	          }
	          return "error";
	          
	      }
	      
	      public void remove(Node n){
	          if(n.pre != null){
	              n.pre.next = n.next;
	          }
	          else{
	              head = n.next;
	          }
	          if(n.next != null)
	          {
	              n.next.pre = n.pre;
	          }
	          else
	          {
	              end = n.pre;
	          }
	      }
	      
	      public void setHead(Node n)
	      {
	          n.next = head;
	          n.pre = null;
	          
	          if(head != null) // to deal with the situation that the linked list is not empty
	          {
	              head.pre = n;
	          }
	          head = n;
	          if(end == null){
	              end = head;
	          }
	      }
	      
	      public void set(String key, String value) 
	      {
	          
	          if (map.containsKey(key)){
	              Node old = map.get(key);
	              old.value =value;
	              remove(old);
	              setHead(old);
	              
	          }
	          else
	          {
	              Node created = new Node(key,value);
	              if(map.size() >= capacity )
	              {
	                  map.remove(end.key);
	                  remove(end);
	                  setHead(created);
	                  
	              }
	              else{
	                  setHead(created);
	              }
	              
	              map.put(key,created);
	          }
	          
	      }
	

}
