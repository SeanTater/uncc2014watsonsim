/**
 * Text analyzers, for differentiating passages to improve ranking.
 * 
 * Scorers measure some aspect of the answer or passage, possibly in relation
 * to the question. Every scorer must return a primitive double.
 * <p>
 * Remember that the purpose of a scorer is not to provide a perfect rank on
 * it's own, only to differentiate "good" and "bad" passages in some meaningful
 * way. As such, the scale and sign are not very important.
 */
package edu.uncc.cs.watsonsim.scorers;