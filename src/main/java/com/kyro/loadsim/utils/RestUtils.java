package com.kyro.loadsim.utils;

import com.kyro.loadsim.dto.RestUtilDto;
import com.kyro.loadsim.utils.auth.AbstractAuth;
import com.kyro.loadsim.utils.auth.BasicAuthType;
import com.kyro.loadsim.utils.auth.CustomAuthType;
import com.kyro.loadsim.utils.auth.TokenAuthType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RestUtils {

  private static final int longTimeOut = 1000;
  private static final int shortTimout = 500;

  private static final String EXTENDED_WAIT_TIME =
      "Extended wait time configured for the API call. The url {} The " + "timeout {}";
  private static final String SHORT_WAIT_TIME =
      "Short wait time configured for the API call. The url {} The " + "timeout {}";

  private final RestTemplate shortRestTemplate;
  private final RestTemplate longRestTemplate;

  public RestUtils() {
    this.shortRestTemplate =
        new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(shortTimout))
            .setReadTimeout(Duration.ofMillis(shortTimout))
            .build();
    this.longRestTemplate =
        new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(longTimeOut))
            .setReadTimeout(Duration.ofMillis(longTimeOut))
            .build();
  }

  public AbstractAuth buildAuth(String userName, String password) {
    return new BasicAuthType(userName, password);
  }

  public AbstractAuth buildAuth(String token) {
    return new TokenAuthType(token);
  }

  public AbstractAuth buildAuthCustom(String authHeaderKey, String authHeaderValue) {
    return new CustomAuthType(authHeaderKey, authHeaderValue);
  }

  public URI buildUri(
      String url, MultiValueMap<String, String> parametersMap, String... pathSegments) {
    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(url).pathSegment(pathSegments).queryParams(parametersMap);
    return builder.build(false).toUri();
  }

  public <I> HttpEntity<I> buildHttpEntity(I payload, HttpHeaders headers) {
    return new HttpEntity<>(payload, headers);
  }

  public <O> O sendRequestWithoutRetry(URI uri, Class<O> responseType, long timeout)
      throws RestClientException {
    ResponseEntity<O> responseEntity;
    RestTemplate restTemplate =
        new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(timeout))
            .setReadTimeout(Duration.ofMillis(timeout))
            .build();
    responseEntity = restTemplate.getForEntity(uri, responseType);
    return responseEntity.getBody();
  }

  public <I, O> O sendPostRequestWithoutRetry(
      URI uri, I request, Class<O> responseType, boolean extendedWaitTime)
      throws RestClientException {
    ResponseEntity<O> responseEntity;
    if (extendedWaitTime) {
      log.debug(EXTENDED_WAIT_TIME, uri, longTimeOut);
      responseEntity = this.longRestTemplate.postForEntity(uri, request, responseType);
    } else {
      log.debug(SHORT_WAIT_TIME, uri, shortTimout);
      responseEntity = this.shortRestTemplate.getForEntity(uri, responseType);
    }
    return responseEntity.getBody();
  }

  public <I, O> O sendRequestWithoutRetry(RestUtilDto<I, O> restUtilDto) {
    I payload = restUtilDto.getPayload();
    String url = restUtilDto.getUrl();
    AbstractAuth auth = restUtilDto.getAuth();
    List<CustomHeader> customHeaders = restUtilDto.getCustomHeaders();
    MediaType mediaType = restUtilDto.getMediaType();
    HttpMethod httpMethod = restUtilDto.getHttpMethod();
    Class<O> responseType = restUtilDto.getResponseType();
    boolean extendedWaitTime = restUtilDto.isExtendedWaitTime();
    Long timeout = restUtilDto.getTimeout();

    HttpHeaders headers = constructHeaders(mediaType, auth, customHeaders);
    HttpEntity<I> httpEntity = new HttpEntity<>(payload, headers);
    ResponseEntity<O> responseEntity;
    if (timeout != null) {
      RestTemplate restTemplate =
          new RestTemplateBuilder()
              .setConnectTimeout(Duration.ofMillis(timeout))
              .setReadTimeout(Duration.ofMillis(timeout))
              .build();
      responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, responseType);
    } else {
      if (extendedWaitTime) {
        log.info(EXTENDED_WAIT_TIME, url, longTimeOut);
        responseEntity = longRestTemplate.exchange(url, httpMethod, httpEntity, responseType);
      } else {
        log.info(SHORT_WAIT_TIME, url, shortTimout);
        responseEntity = shortRestTemplate.exchange(url, httpMethod, httpEntity, responseType);
      }
    }
    return responseEntity.getBody();
  }

  public HttpHeaders constructHeaders(
      MediaType mediaType, AbstractAuth auth, List<CustomHeader> customHeaders) {
    HttpHeaders headers;
    if (mediaType == null && auth == null && CollectionUtils.isEmpty(customHeaders)) headers = null;
    else {
      headers = new HttpHeaders();
      headers.setContentType(mediaType);
      setAuthInHeaders(auth, headers);
      if (CollectionUtils.isNotEmpty(customHeaders)) {
        for (CustomHeader customHeader : customHeaders) {
          if (customHeader != null
              && StringUtils.isNotBlank(customHeader.getHeaderKey())
              && StringUtils.isNotBlank(customHeader.getHeaderValue())) {
            headers.set(customHeader.getHeaderKey(), customHeader.getHeaderValue());
          }
        }
      }
    }
    return headers;
  }

  private void setAuthInHeaders(AbstractAuth auth, HttpHeaders headers) {
    log.info("[setAuthInHeaders] called with auth {} and headers {}", auth, headers);

    if (auth == null) return;

    if (auth instanceof BasicAuthType) {
      BasicAuthType basicAuthType = (BasicAuthType) auth;
      headers.setBasicAuth(basicAuthType.getUsername(), basicAuthType.getPassword());
    } else if (auth instanceof TokenAuthType) {
      TokenAuthType tokenAuthType = (TokenAuthType) auth;
      headers.setBearerAuth(tokenAuthType.getToken());
    } else if (auth instanceof CustomAuthType) {
      CustomAuthType customAuthType = (CustomAuthType) auth;
      headers.set(customAuthType.getAuthHeader(), customAuthType.getAuthValue());
    } else {
      throw new IllegalStateException("Unexpected auth type");
    }
  }

  public <I, O> CompletableFuture<O> sendRequest(RestUtilDto<I, O> restUtilDto) {
    return CompletableFuture.completedFuture(sendRequestWithoutRetry(restUtilDto));
  }
}
