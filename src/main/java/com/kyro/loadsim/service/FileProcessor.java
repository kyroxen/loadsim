package com.kyro.loadsim.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LongSummaryStatistics;
import java.util.Optional;

@Slf4j
public class FileProcessor {

  private static final String COMMA = ",  ";

  private FileProcessor() {
    throw new IllegalArgumentException("Utility class");
  }

  private static final String FILE_NAME = "stats.txt";

  public static void writeToFile(LongSummaryStatistics summaryStatistics) {
    try {
      getFile().ifPresent(file -> write(summaryStatistics, file));
    } catch (Exception e) {
      log.warn("Could not access file {}", e.getMessage(), e);
    }
  }

  private static Optional<File> getFile() throws IOException {
    File file = new File(FILE_NAME);
    boolean isFileCreated = file.exists() || file.createNewFile();
    return isFileCreated ? Optional.of(file) : Optional.empty();
  }

  private static void write(LongSummaryStatistics summaryStatistics, File file) {
    String[] dataLine =
        new String[] {
          String.valueOf(summaryStatistics.getCount()),
          String.valueOf(summaryStatistics.getSum()/1000),
          String.valueOf(summaryStatistics.getMin()),
          String.valueOf(summaryStatistics.getMax()),
          String.valueOf(summaryStatistics.getAverage())
        };

    try (PrintWriter output = new PrintWriter(new FileWriter(file, true))) {
      output.println(String.join(COMMA, dataLine));
    } catch (IOException e) {
      log.warn("Could not write to the file {}", e.getMessage(), e);
    }
  }
}
