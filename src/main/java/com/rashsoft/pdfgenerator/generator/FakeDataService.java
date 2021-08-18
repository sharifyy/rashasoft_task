package com.rashsoft.pdfgenerator.generator;


import java.util.Map;

public class FakeDataService {

    public static Map<String, String> getData() {
        return Map.of("user", "Happy Developer", "event", "Java Conf","company","Rashasoft");
    }
}
