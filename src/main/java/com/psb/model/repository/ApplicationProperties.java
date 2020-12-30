package com.psb.model.repository;

import lombok.Data;

@Data
public class ApplicationProperties {
	
	final private String clientId;
	final private String redirectUrl;
	final private String scope;
	
}
