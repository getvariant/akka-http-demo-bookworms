package com.variant.demo;

import com.variant.client.StateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public abstract class BaseController {

	protected final OwnerRepository owners;

	protected static Owner loggedInUser;

	protected BaseController(OwnerRepository owners) {
		this.owners = owners;
		// If this is the very first page, emulate an already logged-in user.
		loggedInUser = owners.findById(1);
	}

	/**
	 * Runs before descendant controller actions
	 */
	protected Optional<StateRequest> before(Model model) {

		// Target for this state, if it's instrumented.
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			.getRequest();

		Optional<StateRequest> reqOpt = Variant.targetForState(request);
		reqOpt.ifPresent(req -> {
			// We have a state request => some experiments may be instrumented.
			System.out.println("******************* " + Variant.isExperienceLive(req, "FreeVaccinationExp", "WithPromo"));
			if (Variant.isExperienceLive(req, "FreeVaccinationExp", "WithPromo")) {
				model.addAttribute("isRenderPromo", true);
			}
		});

		// Populate the login user dropdown
		var users = owners.findAll().stream().map(OwnerWrapper::new).toList();
		model.addAttribute("currentUser", loggedInUser);
		model.addAttribute("users", users);

		return reqOpt;
	}

	protected void after(Optional<StateRequest> stateRequestOpt) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			.getResponse();
		stateRequestOpt.ifPresent(req -> req.commit(response));
	}

}
