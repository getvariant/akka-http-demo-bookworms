package com.variant.demo.petclinic.spi;

import com.variant.server.spi.TraceEvent;
import com.variant.server.spi.QualificationLifecycleEvent;
import com.variant.server.spi.QualificationLifecycleHook;

import java.util.Optional;

/**
 * Variant CVM.
 * Bookworms demo application.
 */

public class FreeVaccineQualificationHook implements QualificationLifecycleHook {
	@Override
	public Optional<Boolean> post(QualificationLifecycleEvent event) {
		var qualified = Boolean.valueOf(event.getSession().getAttributes().get("isInactive"));
		var traceMessage = String.format(
			"%s: %s user '%s'",
			getClass().getSimpleName(), qualified ? "Qualified" : "Disqualified", event.getSession().getOwnerId().orElse("?"));
		event.getSession().triggerEvent(TraceEvent.of(traceMessage));
		return Optional.of(qualified);
	}
}
