package com.kyro.loadsim.utils.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CustomAuthType extends AbstractAuth {

  private final String authHeader;
  private final String authValue;

  public CustomAuthType(String authHeader, String authValue) {
    super(AuthTypeEnum.CUSTOM);
    this.authHeader = authHeader;
    this.authValue = authValue;
  }
}
