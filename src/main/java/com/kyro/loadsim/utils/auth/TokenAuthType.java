package com.kyro.loadsim.utils.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TokenAuthType extends AbstractAuth {

  private final String token;

  public TokenAuthType(String token) {
    super(AuthTypeEnum.TOKEN_BEARER);
    this.token = token;
  }
}
