package com.psb.model.repository;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Track implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -1326964402024792298L;

	private String name;
	private String uri;
	private Album album;
	private List<Artist> artists;

}
