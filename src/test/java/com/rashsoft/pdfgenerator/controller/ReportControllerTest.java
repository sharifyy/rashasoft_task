package com.rashsoft.pdfgenerator.controller;

import com.rashsoft.pdfgenerator.generator.EmailService;
import com.rashsoft.pdfgenerator.generator.SchedulerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value(value = "${output.directory}")
    private String outputDirectory;

    @MockBean
    SchedulerService schedulerService;

    @MockBean
    EmailService emailService;

    @Test
    public void testGettingGeneratedPdfFileNames() throws Exception {
        Files.list(Paths.get(outputDirectory))
                .filter(path -> !Files.isDirectory(path))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .map(Path::toFile)
                .forEach(File::delete);
        Files.createFile(Paths.get(outputDirectory + "/generatedTemplate_someRandomName.pdf"));

        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.[0]").value("generatedTemplate_someRandomName.pdf"));
    }

    @Test
    public void testSchedulingTask() throws Exception {
        String requestBody = """
                {
                    "maxRetry":3,
                    "generationPeriod":3,
                    "timeUnit":"SECONDS"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/reports/schedule")
                .contentType("application/json")
                .content(requestBody)
        ).andExpect(status().isCreated());

        Mockito.verify(schedulerService,Mockito.times(1)).creatTask(Mockito.any(),Mockito.any());
    }

    @Test
    public void testReSchedulingTask() throws Exception {
        String requestBody = """
                {
                    "maxRetry":3,
                    "generationPeriod":3,
                    "timeUnit":"SECONDS"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.put("/reports/schedule")
                .contentType("application/json")
                .content(requestBody)
        ).andExpect(status().isOk());

        Mockito.verify(schedulerService,Mockito.times(1)).rescheduleTask(Mockito.any(),Mockito.any());
    }

    @Test
    public void testDeletingTask() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reports/schedule")).andExpect(status().isNoContent());

        Mockito.verify(schedulerService,Mockito.times(1)).cancelTask(Mockito.anyString());
    }
}
