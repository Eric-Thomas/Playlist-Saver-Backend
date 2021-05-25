package com.psb.model.repository;

import java.io.Serializable;

import lombok.Data;

@Data
public class Album implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -6345748435010321882L;

	private String name;
}
