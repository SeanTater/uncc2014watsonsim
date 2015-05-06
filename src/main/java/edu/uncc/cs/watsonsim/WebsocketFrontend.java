package edu.uncc.cs.watsonsim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WebsocketFrontend extends WebSocketServer {
	private static final int CORES
		= Runtime.getRuntime().availableProcessors();
	private final ExecutorService es = Executors.newWorkStealingPool();
	private final ConcurrentHashMap<WebSocket, Future<List<Answer>>> tasks
		= new ConcurrentHashMap<>();
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
	
	/**
	 * Wrapper for allocating a pipe and asking something of it.
	 * @return The answers
	 */
	private List<Answer> ask(String qtext) {
		try {
			DefaultPipeline pipe = free_pipes.poll(1, MINUTES);
			if (pipe != null) {
				return pipe.ask(qtext);
			}
			// Give up silently? (Not sure what's better here)
		} catch (InterruptedException e) {
			// Not much we can do here..
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	@Override
	/**
	 * Respond to a question like "ask:blahblah"
	 */
	public void onMessage( WebSocket conn, String message ) {
		String[] parts = message.split(":", 1);
		if (parts.length == 2) {
			String type = parts[0];
			String content = parts[1];
			switch (type) {
			case "ask":
				tasks.computeIfAbsent(conn,
						(sock) -> es.submit(() -> ask(content)));
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