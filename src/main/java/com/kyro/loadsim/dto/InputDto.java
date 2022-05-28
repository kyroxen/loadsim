package com.kyro.loadsim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputDto {
  private RestCallType type;
  private String payload;
  private String url;
  private Long timeout;
}
