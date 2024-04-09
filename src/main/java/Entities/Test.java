package Entities;

public class Test {
    private final String name;
    private final String test_places;
    private final String[] test_terms;
    public Test(String name, String test_places, String[] terms){
        this.name = name;
        this.test_places = test_places;
        this.test_terms = terms;
    }

    public String getName() {
        return name;
    }

    public String getTest_places() {
        return test_places;
    }

    public String[] getTest_terms() {
        return test_terms;
    }
}
