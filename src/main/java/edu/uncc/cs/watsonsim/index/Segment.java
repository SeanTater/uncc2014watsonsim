package edu.uncc.cs.watsonsim.index;

import java.io.Closeable;
import java.util.function.Consumer;

import edu.uncc.cs.watsonsim.Passage;

/**
 * A Segment is a part of the Indexing pipeline
 * It is just the union of Closeable and Consumer
 */
public interface Segment extends Closeable, Consumer<Passage> {

}
