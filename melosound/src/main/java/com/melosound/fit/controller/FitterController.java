package com.melosound.fit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api-fitter")
public class FitterController {
	private static final Logger logger = LoggerFactory.getLogger(FitterController.class);
}
