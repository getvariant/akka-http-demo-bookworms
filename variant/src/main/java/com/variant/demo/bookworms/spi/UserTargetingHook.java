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
 * Targeting hook targets user for the ReputationFF feature flag
 *   'WithReputation'    -> WithReputation experience
 *   any other user      -> WithoutReputation experience
 */

public class UserTargetingHook implements VariationTargetingLifecycleHook {

	private final Logger logger = LoggerFactory.getLogger(UserTargetingHook.class);

	@Override
	public Optional<Variation.Experience> post(VariationTargetingLifecycleEvent event) {

		Optional<String> userOpt = Optional.ofNullable(event.getSession().getAttributes().get("user"));

		Optional<Variation.Experience> result =
			switch (event.getVariation().getName()) {
			case "Suggestions" -> {
				yield userOpt
					.flatMap(
						user -> {
							switch (user) {
								case "WithSuggestions" -> {
									return event.getVariation().getExperience("WithSuggestions");
								}
								case "NoSuggestions" -> {
									return event.getVariation().getExperience("NoSuggestions");
								}
								default -> {
									return Optional.empty();
								}
							}
						}
					);
			}
			case "ReputationFF" -> {
				yield userOpt
					.flatMap(
						user -> {
							switch (user) {
								case "WithReputation" -> {
									return event.getVariation().getExperience("WithReputation");
								}
								default -> {
									return event.getVariation().getExperience("NoReputation");
								}
							}
						}
					);

			}
			default -> Optional.empty();
		};

		logger.info("Targeted user " + userOpt + " to experience " + result);
		return result;
	}

}
