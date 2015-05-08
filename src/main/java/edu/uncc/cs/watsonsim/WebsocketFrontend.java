package edu.uncc.cs.watsonsim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WebsocketFrontend extends WebSocketServer {
	private static final int CORES
		= Runtime.getRuntime().availableProcessors();
	private final ExecutorService es = Executors.newWorkStealingPool();
	private final BlockingQueue<DefaultPipeline> free_pipes =
			new ArrayBlockingQueue<DefaultPipeline>(CORES);

	/**
	 * Create the frontend, populating pipelines
	 * @param address
	 */
	public WebsocketFrontend( InetSocketAddress address ) {
		super( address );
		for (int i=0; i < CORES; i++) {
			free_pipes.offer(new DefaultPipeline());
		}
	}
	
	public WebsocketFrontend( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	
	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		// nothing interesting
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		
	}
	
	private void send(WebSocket conn, String flag, Object message) {
		JSONObject jo = new JSONObject();
		jo.put("flag", flag);
		jo.put("message", message);
		conn.send(jo.toJSONString());
	}
	
	/**
	 * Wrapper for allocating a pipe and asking something of it.
	 * @return The answers
	 */
	private List<Answer> ask(WebSocket conn, String qtext) {
		System.out.println("waiting on " + qtext);
		DefaultPipeline pipe = new DefaultPipeline();
		System.out.println("Asking " + qtext);
		if (pipe != null) {
			List<Answer> answers = pipe.ask(new Question(qtext), a -> send(conn, "log", a));
			List<JSONObject> json = answers
					.stream()
					.map(a -> a.toJSON())
					.collect(Collectors.toList());
			send(conn, "result", json);
		}
		// Give up silently? (Not sure what's better here)
		return Collections.emptyList();
	}
	
	@Override
	/**
	 * Respond to a question like "ask:blahblah"
	 */
	public void onMessage( WebSocket conn, String message ) {
		System.out.println(message);
		
		String[] parts = message.split(":", 2);
		
		System.out.println(Arrays.toString(parts));
		if (parts.length == 2) {
			String type = parts[0];
			String content = parts[1];
			switch (type) {
			case "ask":
				es.submit(() -> ask(conn, content));
				break;
			}
			
		}
	}

	/*@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		System.out.println( "received fragment: " + fragment );
	}*/

	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = true;
		int port = 8887; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		WebsocketFrontend s = new WebsocketFrontend( port );
		s.start();
		System.out.println( "Watsonsim Question Server active on : "
				+ s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			if( in.equals( "exit" ) ) {
				s.stop();
				break;
			} else if( in.equals( "restart" ) ) {
				s.stop();
				s.start();
				break;
			}
		}
	}
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
}