package edu.uncc.cs.watsonsim.nlp;

import static org.junit.Assert.*;

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
	}

}
