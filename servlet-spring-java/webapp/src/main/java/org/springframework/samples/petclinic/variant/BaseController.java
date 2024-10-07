package org.springframework.samples.petclinic.variant;

import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.ui.Model;

public abstract class BaseController {

	protected final OwnerRepository owners;
	protected BaseController(OwnerRepository owners) {
		this.owners = owners;
	}
	protected void before(Model model) {
		var list = owners.findAll();
		VariantController.getUser().ifPresent(owner ->
			model.addAttribute("currentUser", owner)
		);
		model.addAttribute("users", list);
	}

}
