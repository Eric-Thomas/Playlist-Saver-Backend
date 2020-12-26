package com.psb.model.repository;

import java.io.Serializable;

import lombok.Data;

@Data
public class Artist implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 9000290933672871662L;
	
	private String name;
}
