package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class Application implements ApplicationRunner {

	@Autowired
	private Environment env;

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		logger.info("NonOptionArgs: {}", args.getNonOptionArgs());
		logger.info("OptionNames: {}", args.getOptionNames());

		for (String name : args.getOptionNames()){
			logger.info("arg-" + name + "=" + args.getOptionValues(name));
		}

		boolean containsImportFolder = args.containsOption("import-folder");
		logger.info("Contains import-folder: " + containsImportFolder);
		if (!containsImportFolder) {
			logger.warn("arg-import-folder will use a default value: " + env.getProperty("import-folder"));
		}

		boolean containsOutputFile = args.containsOption("output-file");
		logger.info("Contains output-file: " + containsOutputFile);
		if (!containsOutputFile) {
			logger.warn("arg-output-file will use a default value: " + env.getProperty("output-file"));
		}
	}
}

