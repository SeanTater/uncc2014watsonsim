package uncc2014watsonsim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phani Rahul
 */
public class CombineJSON {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String path = "C:\\Users\\PhaniRahul\\Desktop\\watson\\output_json";
        String outPath="C:\\Users\\PhaniRahul\\Desktop\\watson\\comb.json";
        File dir = new File(path);
        String list[] = dir.list();
        BufferedWriter bw =null;
        
        try {
            bw = new BufferedWriter(new FileWriter(new File(outPath)));
            bw.append("{\"root\":[");
        } catch (IOException ex) {
            Logger.getLogger(CombineJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (String name : list) {
            System.out.println("name: "+name);
            File file = new File(path+"\\"+name);
                   
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line="";
                while((line=br.readLine())!=null){
                    bw.append(line);
                }
                bw.append(",");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CombineJSON.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CombineJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            bw.append("]}");
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(CombineJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
