package edu.uncc.cs.watsonsim.scorers;

import edu.uncc.cs.watsonsim.Answer;
import edu.uncc.cs.watsonsim.Passage;
import edu.uncc.cs.watsonsim.Phrase;

public class ElliotMerschScorer extends PassageScorer{
	
	public double Scorer (Phrase q, Answer a, Passage p){
		
				String Qraw = q.text;
				String Ptext = p.text;
				String Ptitle = p.title;
				
				//test variables
				//String Qraw = "What is the tallest building?";
				//String Ptext = "The world's tallest man-made structure is the 829.8 m (2,722 ft) tall Burj Khalifa in Dubai, United Arab Emirates. The building gained the official title of Tallest Building in the World at its opening on January 4, 2010.";
				//String Ptitle = "List of tallest buildings and structures in the world";
				
				double score = 0;
				
				String[] Qsplit = Qraw.split(" ");
				String[] PtitleSplit = Ptitle.split(" ");
				String[] PtextSplit = Ptext.split(" ");
				
				//check passage title
				for (int i=0; i<Qsplit.length; i++){
					String currQWord = Qsplit[i];
					
					for (int x=0; x<PtitleSplit.length; x++){
						String currTWord = PtitleSplit[x];
						
						if (currQWord.toLowerCase().contains(currTWord.toLowerCase())){
							score++;
							
						}
					}
				}
				
				System.out.println("Occurences in title: " + score);

				
				double textOccur = 0;
				//check passage text
				for (int i=0; i<Qsplit.length; i++){
					String currQWord = Qsplit[i];
					
					for (int x=0; x<PtextSplit.length; x++){
						String currPWord = PtextSplit[x];
						
						if (currQWord.toLowerCase().contains(currPWord.toLowerCase())){
							textOccur++;
							
						}
					}
				}
				
				System.out.println("Occurences in text: " + textOccur);
				
				//title occurences worth more than text occurences
				score = score*2;
				
				double totalLength = Qsplit.length + PtextSplit.length;
				double finalscore = 0;
				finalscore = (score + textOccur)/totalLength;
				finalscore = finalscore * 100;
				System.out.println("Score:");
				System.out.println(finalscore + " / 100");
		
				return finalscore;

	}
	
	
}
