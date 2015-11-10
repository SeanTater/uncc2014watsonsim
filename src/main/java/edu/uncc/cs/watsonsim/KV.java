package edu.uncc.cs.watsonsim;

import static org.fusesource.lmdbjni.Constants.bytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Optional;
import java.util.function.Function;

import org.fusesource.lmdbjni.Constants;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;

public class KV {
	public Env db = new Env();
	public KV() {
		db.open("data/lmdb", org.fusesource.lmdbjni.Constants.CREATE);
	}
	
	/**
	 * Get a byte array from the database just as it was stored.
	 * @param table		Which table to retrieve it from
	 * @param key		Which key you want
	 * @return			byte[]
	 */
	public Optional<byte[]> get(String table, String key) {
		return Optional.ofNullable(db.openDatabase(table).get(bytes(key)));
	}
	
	/**
	 * Basically just does ((float[]) bytes) which is moderately complex.
	 * @param bytes
	 * @return
	 */
	public static float[] asVector(byte[] bytes) {
		FloatBuffer fb = FloatBuffer.allocate((bytes.length + 3) / 4);
		fb.put(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer());
		return fb.array();
	}
	
	/**
	 * Basically just does ((byte[]) floats) which is moderately complex.
	 * @param bytes
	 * @return
	 */
	public static byte[] asBytes(float[] floats) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[floats.length*4]).order(ByteOrder.LITTLE_ENDIAN);
		bb.asFloatBuffer().put(floats);
		return bb.array();
	}
	
	
	/**
	 * Non-atomically update an entry or return it.
	 * This is used for cases reading is common (getting a fast path
	 * with only a read lock) but writing is not (and might be run twice).
	 */
	public String quickGetOrCompute(String table, String key, Function<String, String> comp) {
		return get(table, key).map(Constants::string).orElseGet(() -> {
			try (Transaction tx = db.createWriteTransaction()){
				String o = comp.apply(key);
				db.openDatabase(tx, table, 0).put(bytes(key), bytes(o));
				return o;
			}
		});
	}
}