package com.variant.demo;

import com.variant.client.StateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public abstract class BaseController {

	protected final OwnerRepository owners;

	protected static OwnerWrapper loggedInOwner;

	protected BaseController(OwnerRepository owners) {
		this.owners = owners;
		// If this is the very first page, emulate an already logged-in user.
		loggedInOwner = new OwnerWrapper(owners.findById(1));
	}

	/**
	 * Runs before descendant controller actions
	 */
	protected Optional<StateRequest> before(Model model) {

		// Target for this state, if it's instrumented.
		HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder
			.currentRequestAttributes()).getRequest();

		Optional<StateRequest> reqOpt = Variant.targetForState(httpServletRequest);
		reqOpt.ifPresent(req -> {
			// We have a state request => some experiments may be instrumented.
			if (Variant.isExperienceLive(req, "FreeVaccinationExp", "WithPromo")) {
				model.addAttribute("isRenderPromo", true);
			}
		});

		// Populate the login user dropdown
		var users = owners.findAll().stream().map(OwnerWrapper::new).toList();
		var foo = loggedInOwner;
		model.addAttribute("currentUser", loggedInOwner);
		model.addAttribute("users", users);
		model.addAttribute("userDto", new SessionUserDto());

		return reqOpt;
	}

	protected void after(Optional<StateRequest> stateRequestOpt) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			.getResponse();
		stateRequestOpt.ifPresent(req -> req.commit(response));
	}

}
