package com.seveneleven.springbatch.config;


import com.seveneleven.springbatch.model.User;
import com.seveneleven.springbatch.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private UserRepository userRepository;

    
    @Bean
    public FlatFileItemReader<User> reader(){
    	FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
    	itemReader.setResource(new FileSystemResource("src/main/resources/users.csv"));
    	itemReader.setName("csvReader");
    	itemReader.setLinesToSkip(1);
    	itemReader.setLineMapper(lineMapper());
    	return itemReader;
    }
   
    private LineMapper<User> lineMapper(){
    	DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
    	
    	DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    	lineTokenizer.setDelimiter(",");
    	lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    	
    }
    
   
    @Bean
    public UserProcessor processor() {
    	return new UserProcessor();
    }
    
 
    @Bean
    public RepositoryItemWriter<User> writer() {
        RepositoryItemWriter<User> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("save");
        return writer;
    }
  
    public Step step1() {
    	return stepBuilderFactory.get("csv-step").<User, User>chunk(10)
    			.reader(reader())
    			.processor(processor())
    			.writer(writer())
    			.taskExecutor(taskExecutor())
    			.build();
    }


    @Bean
    public Job job() {
    	return jobBuilderFactory.get("importUsers")
              .flow(step1()).end().build();
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}
