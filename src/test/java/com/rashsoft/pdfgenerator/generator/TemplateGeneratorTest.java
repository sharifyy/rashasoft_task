package com.rashsoft.pdfgenerator.generator;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateGeneratorTest {

    @Test
    public void testPdfIsCreatedFromTemplate() throws Exception {
        Files.list(Paths.get("/tmp"))
                .filter(path -> !Files.isDirectory(path))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .map(Path::toFile)
                .forEach(File::delete);

        Map<String, String> data = Map.of("user", "Mohamad", "event", "Java Conf", "company", "Rashasoft");
        TemplateGenerator templateGenerator = new TemplateGenerator(data, "/home/sharifi/template.docx", "/tmp");
        templateGenerator.execute();
        long numberOfFiles = Files.list(Paths.get("/tmp"))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .count();

        assertEquals(1, numberOfFiles);

    }

}