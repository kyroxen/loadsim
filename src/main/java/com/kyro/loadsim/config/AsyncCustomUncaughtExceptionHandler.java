package com.kyro.loadsim.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncCustomUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
  @Override
  public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
    log.error(
        "AsyncCustomUncaughtExceptionHandler: error in method {} {}",
        method.getName(),
        throwable.getMessage(),
        throwable);
  }
}
