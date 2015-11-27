package edu.uncc.cs.watsonsim.nlp;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import static edu.uncc.cs.watsonsim.nlp.DenseVectors.*;

public class DenseVectorsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSim() {
		assertEquals(sim(vectorFor("diabetes"), vectorFor("retinopathy")), 0.54, 0.01);
		assertEquals(sim(vectorFor("diabetes"), vectorFor("diabetic")), 0.78, 0.01);
		assertEquals(sim(vectorFor("(*&(*&^(*&^"), vectorFor("diabetic")), 0.00, 0.01);
		assertEquals(sim(vectorFor("diabetes"), vectorFor("")), 0.00, 0.01);
		assertEquals(sim(vectorFor("diabetes"), Optional.of(new float[300])), 0.00, 0.01);
		
		float[] X = new float[300]; X[0] = (float) 0.5;
		float[] Y = new float[300]; Y[1] = (float) 0.5;
		float[] Z = new float[300]; Z[0] = (float) 0.5; Z[1] = (float) 0.5;
		
		assertEquals(sim(X, Y), 0.0, 0.01);
		assertEquals(sim(X, Z), 0.707, 0.01);
		assertEquals(sim(X, X), 1.0, 0.01);
	}

}
