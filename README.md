# test-spring-batch
This repo is to test Spring Batch

### Requirements
##### Initial Data:
1. Several CSV files. The number of files can be quite large (up to 100,000).
2. Each file contains 5 columns: product ID (integer), Name (string), Condition (string, State (string), Price (float).
3. The same product IDs may occur more than once in different CSV files and in the same CSV file.

##### Task:
Write a console utility using Java programming language that allows getting a selection of the cheapest 1000 products from the input CSV files, but don’t include the same product more than 20 times.Use parallel processing to increase performance.

##### Utility Result:
Output CSV file that meets the following criteria:
+ no more than 1000 objects sorted by Price from all files;
+ no more than 20 objects for each product ID.

### Implementation

Technologies: jdk 8, maven, git, Spring Boot, Spring Batch, JUnit

The searching of elements works via the main class "ListLowestProduct".

##### Should be installed

* [jdk 8.101 or higher](www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [maven 3.*](http://maven.apache.org/install.html)

##### For building and running

To build the application, open a root directory and execute the command
```
 mvn clean package
```

To start the application, run the command 

```
java -jar target/test-spring-batch.jar <import-folder> <output-file>
```
```
Usage: java -jar test-spring-batch.jar <import-folder> <output-file>
 <import-folder> - a path to a folder with inout data in CSV format
 <output-file> - a path to a file with result data in CSV format
```


