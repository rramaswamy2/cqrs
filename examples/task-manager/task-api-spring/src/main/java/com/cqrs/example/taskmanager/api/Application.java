package com.cqrs.example.taskmanager.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@EnableJpaRepositories
@EntityScan
public class Application implements CommandLineRunner {


	@Value("${replay.aggregate.id}")
	private String replayId;
	
	//@Autowired
	//Repository<Task> aggregateRepository;
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	 @Override
	public void run(String... args) throws Exception {
		/* System.out.println("aggregate ID to be replayed " + replayId);
		if(replayId != null) {
			aggregateRepository.replay(ID.fromObject(replayId));
			
		} */
		
	} 
}
