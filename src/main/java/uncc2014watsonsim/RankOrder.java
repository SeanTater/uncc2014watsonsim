package uncc2014watsonsim;

import java.util.Comparator;

public class RankOrder implements Comparator<Answer>{

	@Override
	public int compare(Answer arg0, Answer arg1) {
		if (arg0.passages.get(0).score("INDRI_FITB_RANK") < arg1.passages.get(0).score("INDRI_FITB_RANK"))
			return -1;
		else
			return 1;
	}

}
