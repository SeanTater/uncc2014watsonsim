/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package watsondemo;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Phani Rahul
 */
public class WatsonDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final String indri = "/home/sean/deepqa/indri_index";
        final String lucene = "/home/sean/deepqa/lucene_index";
        final String luceneSearchField = "text";
        final int maxDocs = 10;

        //read from the command line
        System.out.println("Enter the jeopardy question: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String question = "";
        try {
            question = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(WatsonDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
        question = question.replaceAll("[^0-9a-zA-Z ]+", "").trim();

        //initialize indri and query
        IndriSearch in = new IndriSearch();
        in.setIndex(indri);
        in.runQuery(question);

        HashSet<String> ignoreSet = new HashSet<String>();
        ignoreSet.add("J! Archive");
        ignoreSet.add("Jeopardy");

        //initialize an query lucene
        LuceneSearch lu = new LuceneSearch(luceneSearchField);
        lu.setIndex(lucene);
        lu.runQuery(question);

        //initialize google search engine and query.
        WebSearchGoogle go = new WebSearchGoogle();
        go.runQuery(question);

        //build the result objects
        ArrayList<ResultSet> indriResult = new ArrayList<>();
        ArrayList<ResultSet> luceneResult = new ArrayList<>();
        ArrayList<ResultSet> googleResult = new ArrayList<>();
        double indMax = 0, indMin = 0, lucMax = 0, lucMin = 0, goMin = 0, goMax = 0;
        for (int i = 0; i < LocalSearch.MAX_RESULTS; i++) {
            ResultSet ob1 = new ResultSet();
            ob1.setRank(i + 1);
            ob1.setTitle(in.getTitle(i));
            ob1.setScore(in.getScore(i));
            indriResult.add(ob1);
            

            //to normalize the scores
            if (indMax < in.getScore(i)) {
                indMax = in.getScore(i);
            }
            if (indMin > in.getScore(i)) {
                indMin = in.getScore(i);
            }

            ResultSet ob2 = new ResultSet();
            ob2.setRank(i + 1);

            ob2.setTitle(lu.getTitle(i));

            ob2.setScore(lu.getScore(i));
            luceneResult.add(ob2);
            System.out.println(lu.getDocument(i));

            //to normalize the scores
            if (lucMax < lu.getScore(i)) {
                lucMax = lu.getScore(i);
            }
            if (lucMin > lu.getScore(i)) {
                lucMin = lu.getScore(i);
            }

            //
            ResultSet ob3 = new ResultSet();

            ob3.setRank(i + 1);
            ob3.setTitle(go.getTitle(i));
            ob3.setScore(maxDocs - i);
            boolean there = false;
            for (String ig : ignoreSet) {
                if (ob3.getTitle().contains(ig)) {
                    there = true;
                    break;
                }
            }
            if (!there) {
                googleResult.add(ob3);

                //to normalize the scores
                if (goMax < maxDocs - i) {
                    goMax = maxDocs - i;
                }
                if (goMin > maxDocs - i) {
                    goMin = maxDocs - i;
                }
            }

        }
        //normalize the scores
        double ind = Math.sqrt((indMax * indMax) + (indMin * indMin));
        for (ResultSet rs : indriResult) {
            rs.setNormalizedScore(-1 * rs.getScore() / ind);
        }
        double luc = Math.sqrt((lucMax * lucMax) + (lucMin * lucMin));
        for (ResultSet rs : luceneResult) {
            rs.setNormalizedScore(rs.getScore() / luc);
        }
        double goo = Math.sqrt((goMax * goMax) + (goMin * goMin));
        for (ResultSet rs : googleResult) {
            rs.setNormalizedScore(rs.getScore() / goo);
        }

        //merge the result sets.
        HashSet<CombinedResult> merged = new HashSet<>();
        HashSet<ResultSet> intersect = new HashSet<>();
        intersect.addAll(indriResult);
        intersect.retainAll(luceneResult);
//        intersect.retainAll(googleResult);

        for (ResultSet rsInd : intersect) {
            ResultSet rsLuc = luceneResult.get(luceneResult.indexOf(rsInd));
            merged.add(new CombinedResult(rsInd.getTitle(),
                    rsInd.getScore(), rsLuc.getScore(), rsInd.getRank(),
                    rsLuc.getRank(), rsInd.getNormalizedScore(),
                    rsLuc.getNormalizedScore()));
        }
        for (ResultSet rs : indriResult) {
            merged.add(new CombinedResult(rs.getTitle(),
                    rs.getScore(), 0, rs.getRank(),
                    -1, rs.getNormalizedScore(), 0));
        }
        for (ResultSet rs : luceneResult) {
            merged.add(new CombinedResult(rs.getTitle(),
                    0, rs.getScore(), -1, rs.getRank(), 0,
                    rs.getNormalizedScore()));
        }

        HashSet<ResultSet> googleLeftovers = new HashSet<>();
        for (ResultSet rs : googleResult) {
            String[] goTitle = rs.getTitle().split(" ");
            for (CombinedResult every : merged) {
                String[] eveTitle = every.getTitle().split(" ");
                boolean merge = true;
                for (String eT : eveTitle) {
                    boolean exists = false;
                    for (String gT : goTitle) {

                        if (gT.equalsIgnoreCase(eT)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        merge = false;
                    }
                }
                if (merge) {
                    every.setGoogleText(rs.getTitle());
                    every.setGoogleScore(rs.getScore());
                    every.setGoogleRank(rs.getRank());
                    every.setNormalizedGoogleScore(rs.getNormalizedScore());
                } else {
                    googleLeftovers.add(rs);
                }
            }
        }
        for (ResultSet rs : googleLeftovers) {
            CombinedResult c = new CombinedResult(rs.getTitle(), 0, 0, -1, -1, 0, 0);
            c.setGoogleText(rs.getTitle());
            c.setGoogleScore(rs.getScore());
            c.setGoogleRank(rs.getRank());
            c.setNormalizedGoogleScore(rs.getNormalizedScore());
            merged.add(c);

        }

        //now finally sort the merged result
        ArrayList<CombinedResult> mergedList = new ArrayList<>(merged);
        Collections.sort(mergedList);

        //display results
//        for (CombinedResult c : mergedList) {
//            System.out.println(c);
//        }
        //System.out.println(indriResult);
        //System.out.println(luceneResult);
        //System.out.println(googleResult);
        //System.out.println("Done");

    }
}
