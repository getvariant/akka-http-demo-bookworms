package urisman.bookworms.variant;

import akka.http.scaladsl.model.HttpRequest;
import akka.http.scaladsl.model.HttpResponse;
import com.variant.client.SessionIdTracker;
import com.variant.client.TargetingTracker;

import java.util.Set;

public class TargetingTrackerAkka implements TargetingTracker {

  private final String cookieName = "variant-target";
  private String sid;

  public TargetingTrackerAkka(Object... data) {
    HttpRequest req = (HttpRequest) data[0];
    sid = req.cookies().find(cookie->cookieName.equals(cookie.name())).get().value();
    System.out.println("****************** " + sid);
  }

  @Override
  public Set<Entry> get() {
    return null;
  }

  @Override
  public void set(Set<Entry> set) {

  }

  @Override
  public void save(Object... data) {
    HttpResponse resp = (HttpResponse) data[0];
    //resp.addHeader(HttpCookie$.MODULE$.apply(cookieName, sid));
  }
}
