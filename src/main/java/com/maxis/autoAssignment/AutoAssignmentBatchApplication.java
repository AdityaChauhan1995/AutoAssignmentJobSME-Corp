package com.maxis.autoAssignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.maxis.autoAssignment.utils.AutoAssingmentController;



@SpringBootApplication
@ComponentScan({"com.maxis.autoAssignment"})
public class AutoAssignmentBatchApplication implements CommandLineRunner{
	@Autowired
	AutoAssingmentController controller;
	

	public static void main(String[] args) {
		SpringApplication.run(AutoAssignmentBatchApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		controller.getData();
		
	}
}
