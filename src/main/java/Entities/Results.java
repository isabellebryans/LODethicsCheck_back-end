package Entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Results {
    private final Test test;
    private Set<String> results = new HashSet<>(List.of());

    public Results(Test test){
        this.test=test;

    }

    public void setResults(List<String> results) {
        this.results.addAll(results);
    }

    public String[] getResults() {
        return results.toArray(new String[0]);
    }

    public Test getTest() {
        return test;
    }
}
