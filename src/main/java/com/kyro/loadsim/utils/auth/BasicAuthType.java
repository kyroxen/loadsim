package com.kyro.loadsim.utils.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BasicAuthType extends AbstractAuth {

  private final String username;
  private final String password;

  public BasicAuthType(String username, String password) {
    super(AuthTypeEnum.BASIC);
    this.username = username;
    this.password = password;
  }
}
