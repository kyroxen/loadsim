package com.kyro.loadsim.utils.auth;

import lombok.Getter;

@Getter
public abstract class AbstractAuth {

  private final AuthTypeEnum authType;

  protected AbstractAuth(AuthTypeEnum authType) {
    this.authType = authType;
  }
}
