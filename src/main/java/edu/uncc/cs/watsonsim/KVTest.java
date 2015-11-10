package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class KVTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testAsVectorAsBytes() {
		float[] f = {(float) 12.0, (float) 0.99};
		byte[] b = {0, 0, 64, 65, -92, 112, 125, 63};
		for (int i=0; i<8; i++) b[i] = KV.asBytes(f)[i];
		for (int i=0; i<2; i++) f[i] = KV.asVector(b)[i];
	}

	@Test
	public void testQuickGetOrCompute() {
		fail("Not yet implemented");
	}

}
