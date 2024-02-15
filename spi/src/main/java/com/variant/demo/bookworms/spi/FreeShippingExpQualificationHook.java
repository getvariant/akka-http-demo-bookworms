package com.variant.demo.bookworms.spi;

import com.variant.server.spi.TraceEvent;
import com.variant.server.spi.lifecycle.VariationQualificationLifecycleEvent;
import com.variant.server.spi.lifecycle.VariationQualificationLifecycleHook;

import java.util.Optional;

/**
 * Variant CVM.
 * Bookworms demo application.
 */

public class FreeShippingExpQualificationHook implements VariationQualificationLifecycleHook {
	@Override
	public Optional<Boolean> post(VariationQualificationLifecycleEvent event) {
		System.out.println(event.getSession().getAttributes().get("isInactive"));
		var qualified = Boolean.valueOf(event.getSession().getAttributes().get("isInactive"));
		var traceMessage = String.format(
			"%s: %s user '%s'",
			getClass().getSimpleName(), qualified ? "Qualified" : "Disqualified", event.getSession().getOwnerId().orElse("?"));
		event.getSession().triggerEvent(TraceEvent.of(traceMessage));
		return Optional.of(qualified);
	}
}
