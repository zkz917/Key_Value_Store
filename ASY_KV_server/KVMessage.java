package javaio;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;



public class KVMessage {
	int min;
	int max;
	int counter;
	
	
	public String message(){
		
		int key= ThreadLocalRandom.current().nextInt(min, max + 1);
		int value = ThreadLocalRandom.current().nextInt(min, max + 1);
		int instructionSelect = ThreadLocalRandom.current().nextInt(0, 1 + 1);
		
		String message_string;
		String key_string = Integer.toString(key);
		String value_string = Integer.toString(value);
		
		
		if(instructionSelect == 0){
			message_string = "GET " +key_string;
			
		}else{
			message_string = "POST " + key_string + " " + value_string;
			
		}

		return message_string;
					
	}
		
	public static void main(String [ ] args){
		KVMessage kvm = new KVMessage();
		kvm.min = 0;
		kvm.max = 10;
		
	   for(int i =0; i < 10; i++){
		   System.out.println(kvm.message());
	   }
		
	}
	
}
