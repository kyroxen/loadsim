package com.kyro.loadsim.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NotNull
public class CustomHeader {
  private String headerKey;
  private String headerValue;
}
