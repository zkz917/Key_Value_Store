import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tangym
 *
 */
public class Server {
	private static int MIN_SESSIONS = 4;
	private static int MAX_SESSIONS = 10;
	private static long keepAliveTime = 1000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 4444; //Integer.parseInt(args[0]);
		ExecutorService executor = Executors.newFixedThreadPool(MAX_SESSIONS);
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(
//				MIN_SESSIONS, MAX_SESSIONS, keepAliveTime, TimeUnit.MILLISECONDS, 
//				new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.DiscardPolicy());
		
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket connection = server.accept();
				executor.execute(new ClientHandler(connection));
				System.out.println("aaa");
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
		executor.shutdown();
	}
}
