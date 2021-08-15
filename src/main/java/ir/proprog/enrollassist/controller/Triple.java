package ir.proprog.enrollassist.controller;

import lombok.Getter;

@Getter
public class Triple {
    private final String first;
    private final String second;
    private final String third;

    public Triple(String first, String second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
