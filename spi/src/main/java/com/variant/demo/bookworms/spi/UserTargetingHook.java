package com.variant.demo.bookworms.spi;

import com.variant.server.spi.lifecycle.VariationTargetingLifecycleEvent;
import com.variant.server.spi.lifecycle.VariationTargetingLifecycleHook;
import com.variant.share.schema.Variation;

import java.util.Optional;

/**
 * Variant Code Variation Server.
 * Bookworms demo application.
 */

public class UserTargetingHook implements VariationTargetingLifecycleHook {
	@Override
	public Optional<Variation.Experience> post(VariationTargetingLifecycleEvent event) {
		final String user = event.getSession().getAttributes().get("user");
		final Variation variation = event.getVariation();
		return switch (variation.getName()) {
			case "Suggestions" -> {
				yield "WithSuggestions".equals(user) ?
					variation.getExperience("WithSuggestions") : Optional.empty();
			}
			case "ReputationFF" -> {
				yield "WithReputation".equals(user) ?
					variation.getExperience("WithReputation") : variation.getExperience("NoReputation");
			}
			default -> throw new RuntimeException(String.format("Unknown variation %s", variation));
		};
	}
}
