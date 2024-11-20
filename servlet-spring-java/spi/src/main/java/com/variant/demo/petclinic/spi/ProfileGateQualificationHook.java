package com.variant.demo.petclinic.spi;

import com.variant.server.spi.QualificationLifecycleEvent;
import com.variant.server.spi.QualificationLifecycleHook;
import com.variant.server.spi.TraceEvent;
import com.variant.share.yaml.YamlList;
import com.variant.share.yaml.YamlNode;
import com.variant.share.yaml.YamlScalar;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Variant CVM.
 * Bookworms demo application.
 */

public class ProfileGateQualificationHook implements QualificationLifecycleHook {
	private final List<String> whiteList;
	public ProfileGateQualificationHook(YamlNode<?> init) {
		whiteList = ((YamlList)init).value().stream()
			.map(yscalar -> ((YamlScalar<String>)yscalar).value())
			.toList();
	}
	@Override
	public Optional<Boolean> post(QualificationLifecycleEvent event) {
		final var owner = event.getSession().getOwnerId().orElse(null);
		final var qualified = whiteList.contains(owner);
		final String comment = "%s user '%s'"
			.formatted(qualified ? "Qualified" : "Disqualified", owner);
		final TraceEvent traceEvent = TraceEvent.of(getClass().getSimpleName());
		traceEvent.getAttributes().put("Comment", comment);
		event.getSession().triggerEvent(traceEvent);
		return Optional.of(qualified);
	}
}
