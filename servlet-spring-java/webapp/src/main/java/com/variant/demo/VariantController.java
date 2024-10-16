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
package com.variant.demo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

	/**
	 * Login a user.
	 */
	@PostMapping("/login/{ownerId}")
	public @ResponseBody String loginUser(@PathVariable("ownerId") int ownerId, HttpServletResponse response) {
		loggedInUser = owners.findById(ownerId);
		Cookie ssnIdTracker = new Cookie("variant-ssnid", "");
		ssnIdTracker.setPath("/");
		ssnIdTracker.setMaxAge(0);
		response.addCookie(ssnIdTracker);
		return "OK";
	}

}
