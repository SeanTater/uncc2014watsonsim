package uncc2014watsonsim;

import java.util.Comparator;

public class RankOrder implements Comparator<Answer>{

	@Override
	public int compare(Answer arg0, Answer arg1) {
		if (arg0.score(Score.INDRI_PASSAGE_RETRIEVAL_RANK) < arg1.score(Score.INDRI_PASSAGE_RETRIEVAL_RANK))
			return -1;
		else
			return 1;
	}

}
