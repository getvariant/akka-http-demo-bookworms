package urisman.bookworms.variant;

import akka.http.scaladsl.model.HttpRequest;
import akka.http.scaladsl.model.HttpResponse;
import akka.http.scaladsl.model.headers.HttpCookie;
import com.variant.client.SessionIdTracker;

public class SessionIdTrackerAkka implements SessionIdTracker {

  private final String cookieName = "variant-ssnid";
  private String sid;

  public SessionIdTrackerAkka(Object... data) {
    HttpRequest req = (HttpRequest) data[0];
    sid = req.cookies().find(cookie->cookieName.equals(cookie.name())).get().value();
    System.out.println("****************** " + sid);
  }

  @Override
  public String get() {
    return sid;
  }

  @Override
  public void set(String sid) {
    this.sid = sid;
  }

  @Override
  public void save(Object... data) {
    HttpResponse resp = (HttpResponse) data[0];
    //resp.addHeader(HttpCookie$.MODULE$.apply(cookieName, sid));
  }
}
