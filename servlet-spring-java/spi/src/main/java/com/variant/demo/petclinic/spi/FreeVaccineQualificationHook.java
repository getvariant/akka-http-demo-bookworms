package com.variant.demo.petclinic.spi;

import com.variant.server.spi.Session;
import com.variant.server.spi.TraceEvent;
import com.variant.server.spi.QualificationLifecycleEvent;
import com.variant.server.spi.QualificationLifecycleHook;

import java.util.List;
import java.util.Optional;

/**
 * Variant CVM.
 * Bookworms demo application.
 * Qualify user session for the Free Vaccination experiment.
 * In order to qualify, both must be true:
 * 1. User is on the list of those who has a dog or a cat. In practice, this would likely
 *    be a call to the database.
 * 2. User has not already scheduled the vaccination.
 */

public class FreeVaccineQualificationHook implements QualificationLifecycleHook {
	List<String> qualifiedUsers = List.of("George Franklin", "Eduardo Rodriquez", "Jean Coleman", "Maria Escobito", "Carlos Estaban");
	@Override
	public Optional<Boolean> post(QualificationLifecycleEvent event) {

		final Session variantSession = event.getSession();

		final Boolean isVaccinationScheduled =
			Boolean.parseBoolean(variantSession.getAttributes().get("isVaccinationScheduled"));

		final Optional<String> ownerOpt = event.getSession().getOwnerId();

		final Boolean qualified =
			!isVaccinationScheduled &&
			ownerOpt.map(user -> qualifiedUsers.contains(user)).orElse(false);

		// Signal disqualification as a custom trace event.
		final TraceEvent traceEvent = TraceEvent.of(FreeVaccineQualificationHook.class.getSimpleName());
		final String comment = "%s user %s"
			.formatted(qualified ? "Qualified" : "Disqualified", ownerOpt.orElse("Unknown"));
		traceEvent.getAttributes().put("Comment", comment);
		event.getSession().triggerEvent(traceEvent);
		return Optional.of(qualified);
	}
}
