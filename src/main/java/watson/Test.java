/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package watson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Phani Rahul
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            QuestionList p = new QuestionList("C:\\Users\\PhaniRahul\\Desktop\\watson\\comb.json");
            System.out.println("the answer: "+ p.getResults("This London borough is the G in GMT, squire"));
        } catch (ParseException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
