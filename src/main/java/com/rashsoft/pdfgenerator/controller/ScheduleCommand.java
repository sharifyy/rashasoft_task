package com.rashsoft.pdfgenerator.controller;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.concurrent.TimeUnit;

@Data
public class ScheduleCommand {

    @Min(0) @Max(5)
    private int maxRetry;
    @Min(1)
    private long generationPeriod;
    private TimeUnit timeUnit;

}
