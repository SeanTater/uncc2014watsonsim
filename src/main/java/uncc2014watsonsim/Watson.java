/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phani Rahul
 */
public class Watson {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader br = null;
        String filename = "C:\\Users\\PhaniRahul\\Desktop\\watson\\output.txt";
        try {
            int num = 1;
            br = new BufferedReader(new FileReader(new File(filename)));
            String line = null;
            ExecutorService pool = Executors.newFixedThreadPool(15);
            while ((line = br.readLine()) != null) {
                pool.execute(new AnswerJson(line, num++));
            }
            // master.put("list", list);
            pool.shutdown();
            try {
                pool.awaitTermination(2, TimeUnit.DAYS);
            } catch (InterruptedException ex) {
                Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Main again
            System.out.println("MAIN AGAIN");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Watson.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
