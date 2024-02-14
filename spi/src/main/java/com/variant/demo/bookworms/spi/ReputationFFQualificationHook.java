package com.variant.demo.bookworms.spi;

import com.variant.server.spi.TraceEvent;
import com.variant.server.spi.lifecycle.VariationQualificationLifecycleEvent;
import com.variant.server.spi.lifecycle.VariationQualificationLifecycleHook;

import java.util.Optional;

/**
 * Variant CVM.
 * Bookworms demo application.
 */

public class ReputationFFQualificationHook implements VariationQualificationLifecycleHook {
	@Override
	public Optional<Boolean> post(VariationQualificationLifecycleEvent event) {
		var qualified = "WithReputation".equals(event.getSession().getOwnerId().get());
		var traceMessage = String.format("%s user '%s'", qualified ? "Qualified" : "Disqualified", event.getSession().getAttributes().get("user"));
		event.getSession().triggerEvent(TraceEvent.of(traceMessage));
		return Optional.of(qualified);
	}
}
