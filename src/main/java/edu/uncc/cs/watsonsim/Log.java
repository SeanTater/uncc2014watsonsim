package edu.uncc.cs.watsonsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Wrapper logger
 * 
 * Loggers already allow many modules to log to many places.
 * But we need each module to log to some (but not all) places. So basically,
 * want to pass around a fancy many-to-many channel. 
 * 
 * @author Sean
 *
 */
public class Log {
	// A concurrent map of listeners to the mailboxes they own.
	private final BlockingQueue<String> mailbox;
	private Consumer<List<String>> listener;
	//private final Optional<Log> parent;
	private final Class<?> speaker;
	private final long start;
	
	private enum Level {ERROR, WARNING, INFO, DEBUG};
	
	public static final Log NIL = new Log(Object.class, x->{});
	
	// Start a root logger
	public Log(Class<?> speaker, Consumer<List<String>> listener) {
		//this.parent = Optional.empty();
		this.speaker = speaker;
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.submit(() -> daemon(exec));
		this.mailbox = new ArrayBlockingQueue<String>(1000);
		this.start = System.currentTimeMillis();
		this.listener = listener;
	}
	
	// Start a child logger
	public Log(Class<?> speaker, Log parent) {
		//this.parent = Optional.of(parent);
		this.speaker = speaker;
		this.mailbox = parent.mailbox;
		this.start = parent.start;
	}
	
	/**
	 * Background notification-push thread
	 */
	private void daemon(ExecutorService exec) {
		List<String> packet = new ArrayList<>();
		while (true) {
			// Wait until at least one message needs to be sent
			String message = null;
			try { message = mailbox.poll(5, MINUTES); }
			catch (InterruptedException e) {}
			// Stop pushing messages if interrupted or queue is dead
			if (message == null) break;
			else {
				// Send all the remaining messages 
				mailbox.drainTo(packet);
				listener.accept(packet);
				packet.clear();
			}
		}
	}
	
	/**
	 * Make a new writable subchannel.
	 */
	public Log kid(Class<?> speaker) {
		return new Log(speaker, this);
	}
	
	public void setListener(Consumer<List<String>> listener) {
		this.listener = listener;
	}
	
	/**
	 * Push some notifications. Listeners may lose interest.
	 */
	private void push(String content, Level level) {
		mailbox.offer(String.format("%.2f [%s %s]",
				(System.currentTimeMillis()-start) / 1000.0,
				level.name(),
				speaker.getSimpleName(),
				content));
	}
	
	public void error(String message) {
		push(message, Level.ERROR);
	}
	
	public void warn(String message) {
		push(message, Level.WARNING);
	}
	
	public void info(String message) {
		push(message, Level.INFO);
	}
	
	public void debug(String message) {
		push(message, Level.DEBUG);
	}
}
