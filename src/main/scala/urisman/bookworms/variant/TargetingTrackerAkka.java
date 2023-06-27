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
  }

  @Override
  public Set<Entry> get() {
    return Set.of();
  }

  @Override
  public void set(Set<Entry> set) {

  }

  @Override
  public void save(Object... data) {
  }
}
