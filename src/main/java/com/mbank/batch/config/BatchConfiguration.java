package com.mbank.batch.config;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.model.TransactionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class BatchConfiguration {

  private static final String DEFAULT_CSV_PATH = "dataSource.txt";

  @Bean
  @StepScope
  public FlatFileItemReader<TransactionRecord> reader(
    @Value("#{jobParameters['csvPath']}") String csvPath) {

    var resolvedPath = (csvPath == null || csvPath.isBlank())
      ? DEFAULT_CSV_PATH
      : csvPath;

    var tokenizer = new DelimitedLineTokenizer("|");
    tokenizer.setNames("accountNumber", "trxAmount", "description", "trxDate", "trxTime",
      "customerId");

    FieldSetMapper<TransactionRecord> fieldSetMapper = fieldSet -> TransactionRecord.builder()
      .accountNumber(fieldSet.readString("accountNumber"))
      .trxAmount(fieldSet.readBigDecimal("trxAmount"))
      .description(fieldSet.readString("description"))
      .trxDate(LocalDate.parse(fieldSet.readString("trxDate"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .trxTime(LocalTime.parse(fieldSet.readString("trxTime"),
        DateTimeFormatter.ofPattern("HH:mm:ss")))
      .customerId(fieldSet.readString("customerId"))
      .build();

    LineMapper<TransactionRecord> nonEmptyLineMapper = (line, lineNumber) -> {
      if (!StringUtils.hasText(line)) {
        return null;
      }
      FieldSet fieldSet = tokenizer.tokenize(line);
      return fieldSetMapper.mapFieldSet(fieldSet);
    };

    DefaultRecordSeparatorPolicy separatorPolicy = new DefaultRecordSeparatorPolicy() {
      @Override
      public boolean isEndOfRecord(String line) {
        return StringUtils.hasText(line);
      }

      @Override
      public String postProcess(String record) {
        return record.trim();
      }
    };

    return new FlatFileItemReaderBuilder<TransactionRecord>()
      .name("trxItemReader")
      .resource(new ClassPathResource(resolvedPath))
      .linesToSkip(1)
      .recordSeparatorPolicy(separatorPolicy)
      .lineTokenizer(tokenizer)
      .lineMapper(nonEmptyLineMapper)
      .build();
  }

  @Bean
  public ItemProcessor<TransactionRecord, TransactionHistoryDto> processor() {
    return r -> TransactionHistoryDto.builder()
      .description(r.description())
      .trxAmount(r.trxAmount())
      .trxDate(r.trxDate())
      .trxTime(r.trxTime())
      .customerId(r.customerId())
      .accountNumber(r.accountNumber())
      .build();
  }

  @Bean
  public Job importTrxJob(JobRepository jobRepository, Step step1) {
    return new JobBuilder("importTrxJob", jobRepository)
      .start(step1)
      .build();
  }

  @Bean
  public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
    FlatFileItemReader<TransactionRecord> reader,
    ItemProcessor<TransactionRecord, TransactionHistoryDto> processor,
    TransactionHistoryWriter writer) {
    return new StepBuilder("step1", jobRepository)
      .<TransactionRecord, TransactionHistoryDto>chunk(3, transactionManager)
      .reader(reader)
      .processor(processor)
      .writer(writer)
      .build();
  }

  @Bean
  public CommandLineRunner runJob(JobLauncher jobLauncher, Job importTrxJob) {
    return args -> {
      log.info("RUNNING importTrxJob");

      String csvPath = null;
      for (String arg : args) {
        if (arg.startsWith("--csvPath=")) {
          csvPath = arg.substring("--csvPath=".length());
        }
      }

      var jobParametersBuilder = new JobParametersBuilder()
        .addLong("run.id", System.currentTimeMillis());

      if (csvPath != null && !csvPath.isBlank()) {
        jobParametersBuilder.addString("csvPath", csvPath);
      }

      jobLauncher.run(importTrxJob, jobParametersBuilder.toJobParameters());
    };
  }
}
