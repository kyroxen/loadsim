package com.kyro.loadsim.controller;

import com.kyro.loadsim.dto.InputWrapper;
import com.kyro.loadsim.dto.ResponseDto;
import com.kyro.loadsim.service.LoadSimulatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/load-sim")
@Slf4j
public class Controller {

  private final LoadSimulatorService loadSimulatorService;

  public Controller(LoadSimulatorService loadSimulatorService) {
    this.loadSimulatorService = loadSimulatorService;
  }

  @PostMapping
  public ResponseEntity<ResponseDto> execute(@RequestBody InputWrapper inputWrapper) {
    ResponseDto responseDto = loadSimulatorService.simulateLoad(inputWrapper);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  //  @PostMapping
  //  public ResponseEntity<ResponseDto> executeV2(
  //      @RequestParam Integer usersCount,
  //      @RequestParam Integer tasksCount,
  //      @RequestParam Long apiCallTimeout) {
  ////    ResponseDto responseDto =
  ////        loadSimulatorService.simulateLoadV2(usersCount, tasksCount, apiCallTimeout);
  //    return new ResponseEntity<>(null, HttpStatus.OK);
  //  }
}
