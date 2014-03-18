/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uncc2014watsonsim;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phani Rahul
 */
public class MergeResults {

    public static boolean isSame(String one, String two) {

        //if the string are the same
        if (one.equalsIgnoreCase(two)) {
            return true;
        } else {
            //is one string completely contained in another
            if(isCompletelyContained(one, two)){
                return true;                
            }
            else{
                return isCompletelyContained(two, one);
            }
           
        }      
    }
    
    private static boolean isCompletelyContained(String subText, String fullText){
        
            //removing stop words from the strings
            try {
                subText = StopFilter.filtered(subText.replaceAll("[^0-9a-zA-Z ]+", "").trim());
                fullText = StopFilter.filtered(fullText.replaceAll("[^0-9a-zA-Z ]+", "").trim());
            } catch (IOException ex) {
                Logger.getLogger(MergeResults.class.getName()).log(Level.SEVERE, null, ex);
            }
            String subs[] = subText.split(" ");
            String fulls[] = fullText.split(" ");

            //is one completely in two?
            boolean there = true;
            for (String o : subs) {
                boolean subInFulls = false;
                for (String t : fulls) {
                    if(t.equalsIgnoreCase(o)){
                        subInFulls = true;
                        break;
                    }                    
                }
                
                if(!subInFulls){
                    there=false;
                    break;
                }
            }
            if(there){
                return true;
            }
            return false;
    }

}