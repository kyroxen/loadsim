package com.kyro.loadsim.dto;

import com.kyro.loadsim.utils.CustomHeader;
import com.kyro.loadsim.utils.auth.AbstractAuth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

@Getter
@AllArgsConstructor
public class RestUtilDto<I, O> {
  private I payload;
  private String url;
  private AbstractAuth auth;
  private List<CustomHeader> customHeaders;
  private MediaType mediaType;
  private HttpMethod httpMethod;
  private Class<O> responseType;
  private boolean extendedWaitTime;
  private Long timeout;
}
