package com.rashsoft.pdfgenerator.generator;

import com.rashsoft.pdfgenerator.controller.ScheduleCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerServiceTest {

    SchedulerService schedulerService;
    ScheduleCommand scheduleCommand;
//    EmailService emailService = Mockito.mock(EmailService.class);
    Task task = Mockito.mock(Task.class);
    TaskScheduler scheduler = Mockito.mock(TaskScheduler.class);

    @BeforeEach
    public void initEach() {
        schedulerService = new SchedulerService();
        scheduleCommand = new ScheduleCommand();
        scheduleCommand.setMaxRetry(3);
        scheduleCommand.setGenerationPeriod(5);
        scheduleCommand.setTimeUnit(TimeUnit.SECONDS);
    }

    @Test
    public void testScheduledTaskIsCreatedAndExecuted() throws Exception {
        schedulerService.creatTask(scheduler, task.getClass().getName());
//        Thread.sleep(100);
        Mockito.verify(scheduler, Mockito.times(1)).schedule();
    }

    @Test
    public void testReschedulingTaskThatDoesNotExistsThrowsException() {
        assertThrows(ResponseStatusException.class, () -> schedulerService.rescheduleTask(scheduler, task.getClass().getName()));
    }

    @Test
    public void testReschedulingTask() throws Exception {
        schedulerService.creatTask(scheduler,task.getClass().getName());
        schedulerService.rescheduleTask(scheduler,task.getClass().getName());
//        Thread.sleep(100);
        Mockito.verify(scheduler, Mockito.times(2)).schedule();
    }

}