package com.variant.demo.bookworms.spi;

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
		return Optional.of(Boolean.valueOf(event.getSession().getAttributes().get("isInactive")));
	}
}
