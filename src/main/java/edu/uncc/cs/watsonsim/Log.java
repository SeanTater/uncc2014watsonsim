package edu.uncc.cs.watsonsim;

import java.util.function.Consumer;

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
	private Consumer<String> listener;
	private final Log parent;
	private final Class<?> speaker;
	private final long start;
	
	private enum Level {ERROR, WARNING, INFO, DEBUG};
	
	public static final Log NIL = new Log(Object.class, x->{});
	
	// Start a root logger
	public Log(Object speaker, Consumer<String> listener) {
		this.parent = null;
		this.speaker = speaker.getClass();
		this.start = System.currentTimeMillis();
		this.listener = listener;
	}
	
	// Start a child logger
	private Log(Object speaker, Log parent) {
		this.parent = parent;
		this.speaker = speaker.getClass();
		this.start = parent.start;
	}
	
	/**
	 * Make a new writable subchannel.
	 */
	public Log kid(Class<?> speaker) {
		return new Log(speaker, this);
	}
	
	public void setListener(Consumer<String> listener) {
		this.listener = listener;
	}
	
	/**
	 * Push some notifications. Listeners may lose interest.
	 */
	private void push(String content, Level level) {
		if (listener != null) {
			listener.accept(String.format("%.2f [%s %s] %s",
					(System.currentTimeMillis()-start) / 1000.0,
					level.name(),
					speaker.getSimpleName(),
					content));
		} else if (parent != null) {
			parent.push(content, level);
		}
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
