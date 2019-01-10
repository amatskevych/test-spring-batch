package demo.config;

import demo.model.Product;
import demo.tasks.TaskToWriteReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    @Value("${import-folder}")
    private Resource[] importFiles;

    @Value("${output-file}")
    private Resource outputFile;

    @Value("${not-use-resources:false}")
    private boolean notUseResources;

    @Value("${import-folder}")
    private String csvFilesDirectory;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private Environment env;

    @Bean
    public Step stepReadData() {
        return steps.get("stepReadData")
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        log.info("The value \"importFiles\" = " + csvFilesDirectory);
                        log.info("Amount of import files = " + importFiles.length);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.info("Amount of read items = " + stepExecution.getReadCount());
                        return null;
                    }
                })
                .<Product, Product>chunk(5)
                .reader(multiResourceItemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Step stepWriteResultToCSVFile() {
        return steps.get("stepWriteResultToCSVFile")
                .tasklet(new TaskToWriteReport(outputFile))
                .build();
    }

    @Bean
    public Job readCSVFilesToCreateReportJob() {
        return jobs.get("readCSVFilesToCreateReportJob")
                .incrementer(new RunIdIncrementer())
                .start(stepReadData())
                .next(stepWriteResultToCSVFile())
                .build();
    }

    @Bean
    public MultiResourceItemReader<Product> multiResourceItemReader() {
        MultiResourceItemReader<Product> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(getCSVInputFiles());
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    @Bean
    public FlatFileItemReader<Product> reader() {
        FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    @Bean
    public ItemProcessor<Product, Product> processor() {
        return new ProductItemProcessor();
    }

    @Bean
    public ConsoleItemWriter<Product> writer() {
        return new ConsoleItemWriter<>();
    }

    @Bean
    public LineMapper<Product> lineMapper() {
        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
        lineTokenizer.setNames("productId", "name", "condition", "state", "price");
        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    private Resource[] getCSVInputFiles() {
        if (notUseResources) {
            File dir = new File(csvFilesDirectory);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
            Resource[] resources  = new Resource[files.length];
            for (int i = 0; i < files.length; i++) {
                resources[i] = new FileSystemResource(files[i]);
            }
            return resources;
        } else {
            return importFiles;
        }
    }
}
