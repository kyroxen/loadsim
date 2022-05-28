package com.kyro.loadsim.service;

import com.kyro.loadsim.dto.RestUtilDto;
import com.kyro.loadsim.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class LoadTask {

  @Autowired private RestUtils restUtils;

  @Async("asyncExecutor1")
  public <I, O> CompletableFuture<O> executeTask(RestUtilDto<I, O> restUtilDto) {
    return restUtils.sendRequest(restUtilDto);
  }
}
