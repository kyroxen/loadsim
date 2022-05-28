package com.kyro.loadsim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputWrapper {
  private InputDto inputDto;
  private Integer threadCount;
  private Integer taskCount;
}
