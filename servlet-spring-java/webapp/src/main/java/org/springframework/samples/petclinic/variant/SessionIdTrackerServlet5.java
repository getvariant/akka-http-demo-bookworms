package org.springframework.samples.petclinic.variant;

 import com.variant.client.SessionIdTracker;
 import jakarta.servlet.http.Cookie;
 import jakarta.servlet.http.HttpServletRequest;
 import jakarta.servlet.http.HttpServletResponse;

 import java.util.Arrays;
 import java.util.Optional;

public class SessionIdTrackerServlet5 implements SessionIdTracker {

  private Optional<String> sid = Optional.empty();

  public SessionIdTrackerServlet5(Object data) {
    HttpServletRequest req = (HttpServletRequest) data;
    sid = Arrays.stream(req.getCookies())
      .filter(cookie -> getCookieName().equals(cookie.getName()))
      .findAny()
      .map(Cookie::getValue);
  }

  @Override
  public Optional<String> get() {
    return sid;
  }

  @Override
  public void set(String sid) {
    this.sid = Optional.of(sid);
  }

  @Override
  public Object save(Object data) {
    HttpServletResponse resp = (HttpServletResponse) data;
    Cookie cookie = new Cookie(getCookieName(), sid.get());
    cookie.setHttpOnly(false);
    cookie.setPath("/");
    resp.addCookie(cookie);
    return resp;
  }
}