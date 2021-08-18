package com.rashsoft.pdfgenerator.generator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

class TaskSchedulerTest {

    Task task = Mockito.mock(Task.class);
    EmailService emailService = Mockito.mock(EmailService.class);

    @Test
    public void testTaskExecution() throws Exception{
        TaskScheduler scheduler = new TaskScheduler(task,10, TimeUnit.SECONDS,5,emailService);
        scheduler.schedule();
        Thread.sleep(1000);
        Mockito.verify(task,Mockito.times(1)).execute();
    }

    @Test
    public void testRetryLogic() throws Exception {
        TaskScheduler scheduler =new TaskScheduler(task,10, TimeUnit.SECONDS,1,emailService);
        Mockito.doThrow(new Exception(":(")).when(task).execute();
        scheduler.schedule();
        Thread.sleep(100);
        Mockito.verify(emailService,Mockito.times(1))
                .sendHtmlMessage(Mockito.anyString(),Mockito.anyString());
    }
}