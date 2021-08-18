package com.rashsoft.pdfgenerator.generator;

import lombok.RequiredArgsConstructor;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class TemplateGenerator implements Task {

    private final Map<String, String> templateData;
    private final String templatePath;
    private final String outputDirectory;


    @Override
    public void execute() throws Exception {
        FileOutputStream os = null;
        try {
            InputStream templateInputStream = new FileInputStream(templatePath);

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

            VariablePrepare.prepare(wordMLPackage);
            if(true) throw new Exception(":(");
            documentPart.variableReplace(templateData);
            os = new FileOutputStream(outputDirectory + "/generatedTemplate_" + UUID.randomUUID() + ".pdf");
            Docx4J.toPDF(wordMLPackage, os);
            os.flush();
            os.close();
        } finally {
            if (os != null) os.close();
        }
    }
}
