package com.rashsoft.pdfgenerator.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerIntegrationTest {

    @Value(value = "${output.directory}")
    private String outputDirectory;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() throws IOException {
        restTemplate.delete("/reports/schedule");
        Files.list(Paths.get(outputDirectory))
                .filter(path -> !Files.isDirectory(path))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .map(Path::toFile)
                .forEach(File::delete);
    }


    @Test
    public void testGettingListOfPdfFileNames(){
        ResponseEntity<String[]> response = restTemplate.getForEntity("/reports", String[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testPDFGenerationAccordingToSchedule() throws IOException {

        ScheduleCommand command = new ScheduleCommand();
        command.setGenerationPeriod(4);
        command.setMaxRetry(3);
        command.setTimeUnit(TimeUnit.SECONDS);
        restTemplate.postForObject("/reports/schedule",command,Void.class);

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long numberOfFiles = Files.list(Paths.get(outputDirectory))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .count();


        assertEquals(3, numberOfFiles);
    }

    @Test
    public void testCancelingPDFGeneration() throws IOException {
        ScheduleCommand command = new ScheduleCommand();
        command.setGenerationPeriod(4);
        command.setMaxRetry(3);
        command.setTimeUnit(TimeUnit.SECONDS);
        restTemplate.postForObject("/reports/schedule",command,Void.class);

        Timer time = new Timer();
        TimerTask st = new TimerTask() {
            @Override
            public void run() {
                restTemplate.delete("/reports/schedule");
            }
        };
        time.schedule(st, 6*1000 );

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long numberOfFiles = Files.list(Paths.get(outputDirectory))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .count();


        assertEquals(2, numberOfFiles);
    }

    @Test
    public void testReSchedulingPDFGeneration() throws IOException {

        ScheduleCommand command = new ScheduleCommand();
        command.setGenerationPeriod(6);
        command.setMaxRetry(3);
        command.setTimeUnit(TimeUnit.SECONDS);
        restTemplate.postForObject("/reports/schedule",command,Void.class);

        Timer time = new Timer();
        TimerTask st = new TimerTask() {
            @Override
            public void run() {
                command.setGenerationPeriod(10);
                restTemplate.put("/reports/schedule", command,Void.class);
            }
        };
        time.schedule(st, 3*1000 );


        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long numberOfFiles = Files.list(Paths.get(outputDirectory))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .count();


        assertEquals(2, numberOfFiles);
    }
}