/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package watson;

import java.util.Objects;

/**
 *
 * @author Phani Rahul
 */
public class ResultSet {

    private String title;
    private double score;
    private boolean correct;
    private int rank;

    public ResultSet(String title, double score, boolean correct, int rank) {
        this.title = title;
        this.score = score;
        this.correct = correct;
        this.rank = rank;
    }

    public ResultSet() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultSet other = (ResultSet) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResultSet{" + "title=" + title + ", score=" + score + ", correct=" + correct + ", rank=" + rank + '}';
    }
    
    
}
