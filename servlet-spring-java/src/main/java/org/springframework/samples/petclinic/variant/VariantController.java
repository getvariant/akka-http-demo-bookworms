/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.variant;

import jakarta.validation.Valid;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VariantController extends BaseController {

	public VariantController(OwnerRepository owners) {
		super(owners);
	}

	private static Optional<Owner> user = Optional.empty();

	public static Optional<Owner> getUser() {
		return user;
	}

	@PostMapping("/login/{ownerId}")
	public @ResponseBody String loginUser(@PathVariable("ownerId") int ownerId) {
		System.out.println("************************* " + ownerId);
		user = Optional.of(owners.findById(ownerId));
		return "OK";
	}

}
