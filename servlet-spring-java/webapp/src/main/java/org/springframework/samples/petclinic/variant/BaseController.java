package org.springframework.samples.petclinic.variant;

import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.ui.Model;

public abstract class BaseController {

	protected final OwnerRepository owners;
	protected BaseController(OwnerRepository owners) {
		this.owners = owners;
	}

	/**
	 * Runs before descendant controller actions
	 */
	protected void before(Model model) {

		// Login user dropdown
		var users = owners.findAll().stream()
			.map(OwnerWrapper::new)
			.toList();
		VariantController.getLoggedInUser().ifPresent(owner ->
			model.addAttribute("currentUser", owner)
		);
		model.addAttribute("users", users);

		//
		model.addAttribute("isRenderPromo", true);
	}

}
