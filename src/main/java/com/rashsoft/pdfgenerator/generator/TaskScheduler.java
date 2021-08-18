package com.rashsoft.pdfgenerator.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Log4j2
@RequiredArgsConstructor
public class TaskScheduler {

    private final Task task;

    private final long generationPeriod;
    private final TimeUnit timeUnit;
    private final int maxRetry;
    private final EmailService emailService;

    private boolean generating;
    private TimerTask timerTask;


    public void schedule() {
        if (generating) return;
        this.generating = true;
        final Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    task.execute();
                } catch (Exception e) {
                    log.error("task execution failed: " + e.getMessage());
                    retry();
                }
            }
        };
        timer.schedule(timerTask, 0, timeUnit.toMillis(generationPeriod));
    }

    private void retry() {
        int retryCounter = 0;
        log.info("retrying the task");
        while (retryCounter < maxRetry) {
            retryCounter++;
            try {
                this.task.execute();
                break;
            } catch (Exception e) {
                if (retryCounter >= maxRetry) {
                    log.error("retry failed");
                    emailService.sendHtmlMessage(new String[]{"rashasoft_operation@gmail.com"},"Scheduler failed",e.getMessage());
                }
            }
        }
    }

    public void cancel() {
        log.info("canceling the task");
        this.generating = false;
        if (timerTask != null)
            timerTask.cancel();
    }

    @Override
    public String toString() {
        return "TaskScheduler{" +
                "generationPeriod=" + generationPeriod +
                '}';
    }

    //    public boolean isGenerating() {
//        return this.generating;
//    }
}
