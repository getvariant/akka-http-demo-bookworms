package com.variant.demo.bookworms.spi;

import com.variant.server.api.lifecycle.VariationQualificationLifecycleEvent;
import com.variant.server.api.lifecycle.VariationQualificationLifecycleHook;

import java.util.Optional;

/**
 * Variant Code Variation Server.
 * Bookworms demo application.
 * Targeting hook targets user for the ReputationFF feature flag
 *   'WithReputation'    -> WithReputation experience
 *   any other user      -> WithoutReputation experience
 */

public class UserQualificationHook implements VariationQualificationLifecycleHook {
	@Override
	public Optional<Boolean> post(VariationQualificationLifecycleEvent event) {
		if("WithReputation".equals(event.getSession().getAttributes().get("user")))
			return Optional.of(true);
		else
			return Optional.of(false);
	}

}
