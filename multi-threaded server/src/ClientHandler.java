import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author tangym
 *
 */
public class ClientHandler implements Runnable {
	private volatile static LRUCache cache = new LRUCache(10);
	
	Thread t;
	Socket connection;
	
	public ClientHandler(Socket connection) {
		this.connection = connection;
		t = new Thread(this, "ClientHandler");
		t.start();
	}
	
	@Override
	public void run() {
			this.handleClient(this.connection);
	}
	
	private void handleClient(Socket connection) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			String line = in.readLine();
			while (line != null) {
				out.println(this.handleRequest(line));
				line = in.readLine();
			}
		} catch (IOException e) {
			// connection closed
			// e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String handleRequest(String msg) {
		System.out.println(msg);
		
		String[] tokens = msg.split(" ");
		switch (tokens[0].toUpperCase()) {
			case "PUT": 
				try {
					return this.put(tokens[1], tokens[2]);
				} catch (ArrayIndexOutOfBoundsException e) {
					return "error";
				}
			case "GET": 
				return this.get(tokens[1]);
			default:
				// TODO: handle error
				try {
					if (t.getId() % 10 == 0) {
						Thread.sleep(30*t.getId());
					} else {
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return t.getId() + ": " + msg;
		}
	}
	
	private synchronized String put(String key, String value) {
		cache.set(key, value);
		return "ok";
	}
	
	private synchronized String get(String key) {
		return cache.get(key);
	}
}
