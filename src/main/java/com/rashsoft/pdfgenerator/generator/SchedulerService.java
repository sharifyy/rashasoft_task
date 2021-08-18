package com.rashsoft.pdfgenerator.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
@RequiredArgsConstructor
public class SchedulerService {

    private final Map<String,TaskScheduler> schedulers = new ConcurrentHashMap<>();

    public void creatTask(final TaskScheduler scheduler ,final String taskId){
        if(schedulers.get(taskId)!=null){
            log.info("Task already exists");
            return;
        }
        schedulers.put(taskId,scheduler);
        scheduler.schedule();
    }

    public void rescheduleTask(final TaskScheduler newScheduler, final String taskId){
        TaskScheduler scheduler = schedulers.get(taskId);
        if(scheduler == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"not task exists to reschedule");
        }
        scheduler.cancel();
        schedulers.remove(taskId);
        creatTask(newScheduler,taskId);
    }

    public void cancelTask(String taskId){
        TaskScheduler scheduler = schedulers.get(taskId);
        if(scheduler!=null){
            scheduler.cancel();
            schedulers.remove(taskId);
        }
    }
}
