package com.rashsoft.pdfgenerator;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PdfGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfGeneratorApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(){
		return args -> {

/*
			final Task documentGeneratorTask = new DocGeneratorTask();

			TaskManger instance = TaskManger.INSTANCE.getInstance();
			instance.setMaxRetry(3);
			instance.schedule(documentGeneratorTask,8,TimeUnit.SECONDS);

			Timer time = new Timer();
			TimerTask st = new TimerTask() {
				@Override
				public void run() {
					instance.cancel();
				}
			};
			time.schedule(st, 20*1000 );
*/

		};
	}



}
