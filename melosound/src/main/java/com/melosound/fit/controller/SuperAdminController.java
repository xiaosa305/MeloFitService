package com.melosound.fit.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.melosound.fit.domain.cusenum.UserRole;
import com.melosound.fit.domain.rsp.ApiResponse;

@RestController
@RequestMapping("api-superadmin")
public class SuperAdminController {
	private static final Logger logger = LoggerFactory.getLogger(SuperAdminController.class);
	
}
