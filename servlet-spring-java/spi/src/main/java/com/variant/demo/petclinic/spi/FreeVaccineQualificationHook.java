package com.variant.demo.petclinic.spi;

import com.variant.server.spi.TraceEvent;
import com.variant.server.spi.QualificationLifecycleEvent;
import com.variant.server.spi.QualificationLifecycleHook;

import java.util.List;
import java.util.Optional;

/**
 * Variant CVM.
 * Bookworms demo application.
 */

public class FreeVaccineQualificationHook implements QualificationLifecycleHook {
	List<String> qualifiedUsers = List.of("Eduardo Rodriquez", "Jean Coleman", "Maria Escobito", "Carlos Estaban");
	@Override
	public Optional<Boolean> post(QualificationLifecycleEvent event) {
		final Boolean qualified =
			event.getSession().getOwnerId()
				.map(user -> qualifiedUsers.contains(user))
				.orElse(false);
		return Optional.of(qualified);
	}
}
