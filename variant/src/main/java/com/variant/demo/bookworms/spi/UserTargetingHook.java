package com.variant.demo.bookworms.spi;

import com.variant.server.api.Session;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleEvent;
import com.variant.server.api.lifecycle.VariationTargetingLifecycleHook;
import com.variant.share.schema.Variation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Variant Code Variation Server.
 * Bookworms demo application.
 * Targeting hook targets user for the Suggestions A/B test
 *   'WithSuggestions' -> WithSuggestions experience
 *   any other user    -> deffer to the default hook
 */

public class UserTargetingHook implements VariationTargetingLifecycleHook {
	@Override
	public Optional<Variation.Experience> post(VariationTargetingLifecycleEvent event) {
		if ("WithSuggestions".equals(event.getSession().getAttributes().get("user")))
			return event.getVariation().getExperience("WithSuggestions");
		else
			return Optional.empty();
	}
}
