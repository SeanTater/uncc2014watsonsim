/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uncc2014watsonsim;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Phani Rahul
 */
public class JSON {

    private String question;
    private String answer;
    private ArrayList<ResultSet> indri;
    private ArrayList<ResultSet> lucene;
    private HashSet<CombinedResult> all;

    public JSON() {
        indri = new ArrayList<>();
        lucene = new ArrayList<>();
        all = new HashSet<>();
    }

    public HashSet<CombinedResult> getAll() {
        if (all.isEmpty()) {
            merge();
        }
        return all;
    }

    private void merge() {
        HashSet<ResultSet> common = new HashSet<>();
        common.addAll(indri);
        common.retainAll(lucene);

        for (ResultSet rsInd : common) {
            ResultSet rsLuc = lucene.get(lucene.indexOf(rsInd));
            all.add(new CombinedResult(rsInd.isCorrect(), rsInd.getTitle(),
                    rsInd.getScore(), rsLuc.getScore(), rsInd.getRank(),
                    rsLuc.getRank()));
        }
        for (ResultSet rs : indri) {
            all.add(new CombinedResult(rs.isCorrect(), rs.getTitle(),
                    rs.getScore(), 0, rs.getRank(),
                    -1));
        }
        for (ResultSet rs : lucene) {
            all.add(new CombinedResult(rs.isCorrect(), rs.getTitle(),
                    0, rs.getScore(), -1, rs.getRank()));
        }
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<ResultSet> getIndri() {
        return indri;
    }

    public void setIndri(ArrayList<ResultSet> indri) {
        this.indri = indri;
    }

    public ArrayList<ResultSet> getLucene() {
        return lucene;
    }

    public void setLucene(ArrayList<ResultSet> lucene) {
        this.lucene = lucene;
    }

    @Override
    public String toString() {
        return "JSON{" + "text=" + question + ", answer=" + answer + ", indri=" + indri + ", lucene=" + lucene + ", all=" + all + '}';
    }
    
}
