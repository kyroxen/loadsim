package com.kyro.loadsim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Details {
  private Integer id;
  private Boolean success;
  private String error;
  private Long timeTaken;
}
