package demo.tasks;

import demo.config.ListLowestProduct;
import demo.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class TaskToWriteReport implements Tasklet {
    private static final Logger log = LoggerFactory.getLogger(TaskToWriteReport.class);

    private static final String CSV_SEPARATOR = ";";

    private String outputFilename;

    public TaskToWriteReport(Resource outputFile) {
        outputFilename = outputFile.getFilename();
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("TaskToWriteReport start...");

        log.info("ListLowestProduct.size()  = " + ListLowestProduct.getInstance().getProducts().size());
        log.info("Write the report into CSV file..." + outputFilename);
        ListLowestProduct.getInstance().sort();
        writeToCSV(ListLowestProduct.getInstance().getProducts());

        log.info("TaskToWriteReport done...");
        return RepeatStatus.FINISHED;
    }

    private void writeToCSV(Collection<Product> productList) throws IOException {
        if (outputFilename == null) {
            log.warn("outputFile.getFilename() == null");
            return;
        }
        Path outputFilePath = Paths.get(outputFilename);
        if (Files.notExists(outputFilePath)) {
            Files.createFile(outputFilePath);
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath.toFile()),
                StandardCharsets.UTF_8));
        for (Product product : productList) {
            String oneLine = product.getProductId() +
                    CSV_SEPARATOR +
                    (product.getName().trim().length() == 0 ? "" : product.getName()) +
                    CSV_SEPARATOR +
                    (product.getCondition().trim().length() == 0 ? "" : product.getCondition()) +
                    CSV_SEPARATOR +
                    (product.getState().trim().length() == 0 ? "" : product.getState()) +
                    CSV_SEPARATOR +
                    (product.getPrice() < 0 ? "" : product.getPrice());
            bw.write(oneLine);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
