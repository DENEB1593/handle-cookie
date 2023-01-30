package io.study.deneb.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Stream;

@Component
public class CookieHandler {


  public Optional<String> find(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();

    if (ObjectUtils.isEmpty(cookies))
      return Optional.empty();

    return Arrays.stream(cookies)
      .filter(cookie->name.equals(cookie.getName()))
      .map(Cookie::getValue)
      .findAny();
  }
}
