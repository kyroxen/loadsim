package com.kyro.loadsim.service;

import com.kyro.loadsim.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class LoadSimulatorService {

  public ResponseDto simulateLoad(InputWrapper inputWrapper) {

    final Integer threadCount = inputWrapper.getThreadCount();
    final Integer taskCount = inputWrapper.getTaskCount();

    ResponseDto responseDto;

    List<Details> detailsList = new ArrayList<>();

    int tasksDoneCount = 0;
    int tasksFailedCount = 0;
    int tasksSuccessCount = 0;

    ThreadPoolExecutor executorService = getThreadPoolExecutor(threadCount);
    List<Callable<Details>> callableTasks = getTaskCallables(taskCount, inputWrapper.getInputDto());

    log.info(
        "Total tasks submitted are {}, thread pool size {}",
        taskCount,
        executorService.getPoolSize());

    //    ExecutorCompletionService<Details> completionService =
    //        new ExecutorCompletionService<>(executorService);
    //
    //    callableTasks.forEach(completionService::submit);
    //    try {
    //      for (int i = callableTasks.size(); i > 0; i--) {
    //        Details details = completionService.take().get();
    //        if (details != null) {
    //          tasksSuccessCount++;
    //          detailsList.add(details);
    //        }
    //      }
    //    } catch (InterruptedException e) {
    //      throw new RuntimeException(e);
    //    } catch (ExecutionException e) {
    //      throw new RuntimeException(e);
    //    } finally{
    //      executorService.shutdownNow();
    //      responseDto =
    //          ResponseDto.builder()
    //              .completedTasks(tasksSuccessCount)
    //              .failedTasks(tasksFailedCount)
    //              .totalTasksSubmitted(taskCount)
    //              .totalTasksDone(tasksDoneCount)
    //              .details(detailsList)
    //              .build();
    //    }
    try {
      List<Future<Details>> futures = executorService.invokeAll(callableTasks);
      while (tasksDoneCount != taskCount) {
        for (Future<Details> future : futures) {
          if (future.isDone()) {
            final Details details = future.get();
            detailsList.add(details);
            if (Boolean.TRUE.equals(details.getSuccess())) {
              tasksSuccessCount++;
            } else {
              tasksFailedCount++;
            }
            tasksDoneCount++;
          }
        }
      }
      log.info("All tasks done! Active thread count {}", executorService.getActiveCount());
    } catch (InterruptedException | ExecutionException e) {
      log.info("Failed execution! Error: {}", e.getMessage());
    } finally {
      executorService.shutdownNow();
      responseDto =
          ResponseDto.builder()
              .completedTasks(tasksSuccessCount)
              .failedTasks(tasksFailedCount)
              .totalTasksSubmitted(taskCount)
              .totalTasksDone(tasksDoneCount)
              .details(detailsList)
              .avgTimeTaken(
                  detailsList.stream().mapToLong(Details::getTimeTaken).summaryStatistics())
              .build();
    }
    return responseDto;
  }

  @NotNull
  private ThreadPoolExecutor getThreadPoolExecutor(Integer threadCount) {
    return new ThreadPoolExecutor(
        threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
  }

  @NotNull
  private List<Callable<Details>> getTaskCallables(Integer taskCount, InputDto inputDto) {
    List<Callable<Details>> callableTasks = new ArrayList<>(taskCount);
    for (int i = 0; i < taskCount; i++) {
      callableTasks.add(createCallable(i, inputDto));
    }
    return callableTasks;
  }

  @NotNull
  private Callable<Details> createCallable(int id, InputDto inputDto) {
    return () -> {
      String error = null;
      boolean success;

      final LocalDateTime before = LocalDateTime.now();
      LocalDateTime after;
      try {
        sendRequest(inputDto);
        after = LocalDateTime.now();
        success = true;
      } catch (Exception exception) {
        after = LocalDateTime.now();
        error = exception.getMessage();
        success = false;
      }
      return new Details(id, success, error, Duration.between(before, after).toMillis());
    };
  }

  private void sendRequest(InputDto inputDto) {
    if (inputDto.getType().equals(RestCallType.GET)) {
      sendGetRequest(inputDto.getTimeout(), inputDto.getUrl());
    } else {
      sendPostRequest(inputDto.getTimeout(), inputDto.getPayload(), inputDto.getUrl());
    }
  }

  private void sendGetRequest(Long timeout, String url) {
    new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(timeout))
        .setReadTimeout(Duration.ofMillis(timeout))
        .build()
        .getForObject(url, String.class);
  }

  private void sendPostRequest(long timeout, String requestJson, String url) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

    new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(timeout))
        .setReadTimeout(Duration.ofMillis(timeout))
        .build()
        .postForObject(url, entity, String.class);
  }
}
