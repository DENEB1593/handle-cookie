package io.study.deneb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.study.deneb.handler.CookieHandler;
import io.study.deneb.model.User;
import io.study.deneb.model.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("api/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final CookieHandler cookieHandler;
  private final ObjectMapper om;

  public UserController(CookieHandler cookieHandler, ObjectMapper om) {
    this.cookieHandler = cookieHandler;
    this.om = om;
  }

  @GetMapping
  public ResponseEntity<?> find(@CookieValue(name = "id") String id, HttpServletRequest request, HttpSession session) {
    log.info("cookie id : {}", id);

    cookieHandler.find(request, "id")
      .ifPresent(val -> {
        User saved = (User) session.getAttribute("id");
        log.info("saved user name: {}", saved.getName());
      });

    return null;

  }

  @PostMapping
  public ResponseEntity<?> save(@RequestBody UserDto userDto, HttpServletRequest request, HttpSession session) {
    log.info("request : {}", userDto);

    Long id = ThreadLocalRandom.current().nextLong(1, 100);

    cookieHandler.find(request, String.valueOf(id)).ifPresent(u ->{
      throw new RuntimeException("already exist");
    });

    User user = new User(id, userDto.name());

    ResponseCookie cookie = ResponseCookie
      .from("id", String.valueOf(id))
      .maxAge(Duration.ofDays(1))
      .build();

    session.setAttribute("id", user);

    return ResponseEntity
      .ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .body(user);
  }

}
