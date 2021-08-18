package com.rashsoft.pdfgenerator.controller;

import com.rashsoft.pdfgenerator.generator.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    @Value(value = "${template.path}")
    private String templatePath;

    @Value(value = "${output.directory}")
    private String outputDirectory;

    private final SchedulerService schedulerService;
    private final EmailService emailService;

    @GetMapping
    public Stream<String> reportList() throws IOException {
        return Files.list(Paths.get(outputDirectory))
                .filter(path -> path.getFileName().toString().startsWith("generatedTemplate_"))
                .map(path -> path.getFileName().toString());
    }

    @PostMapping("/schedule")
    @ResponseStatus(HttpStatus.CREATED)
    public void schedule(@Valid @RequestBody ScheduleCommand command) {
        var task = new TemplateGenerator(FakeDataService.getData(), templatePath, outputDirectory);
        var scheduler = new TaskScheduler(task, command.getGenerationPeriod(), command.getTimeUnit(), command.getMaxRetry(), emailService);
        schedulerService.creatTask(scheduler, TemplateGenerator.class.getName());
    }

    @PutMapping("/schedule")
    public void reschedule(@Valid @RequestBody ScheduleCommand command) {
        var task = new TemplateGenerator(FakeDataService.getData(), templatePath, outputDirectory);
        var scheduler = new TaskScheduler(task, command.getGenerationPeriod(), command.getTimeUnit(), command.getMaxRetry(), emailService);
        schedulerService.rescheduleTask(scheduler, TemplateGenerator.class.getName());
    }

    @DeleteMapping("/schedule")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void stopSchedule() {
        schedulerService.cancelTask(TemplateGenerator.class.getName());
    }
}
