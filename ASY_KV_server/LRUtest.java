package javaio;
import javaio.LRUCache;


public class LRUtest {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
         LRUCache test1 = new LRUCache(10);
         test1.set("1", "10");
         test1.set("1", "11");
         test1.set("10","33");
         System.out.println(test1.get("10"));
               
	}

}
