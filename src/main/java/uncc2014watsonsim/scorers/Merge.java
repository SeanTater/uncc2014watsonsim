package uncc2014watsonsim.scorers;

/**
 * These are the ways to merge a score.
 * 
 * Keep in mind that Mean doesn't have the information to weigh the operands.
 * So, (M (M 1 3) 5) = 3.5; But (M 1 (M 3 5)) = 2.5  
 * @author Sean
 *
 */
public enum Merge {
	Mean, Or, Min, Max
}
