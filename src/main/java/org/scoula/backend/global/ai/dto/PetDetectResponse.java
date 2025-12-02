package org.scoula.backend.global.ai.dto;

import java.util.List;

import lombok.Data;

@Data
public class PetDetectResponse {
	private String thumbnail_url;
	private String summary;
	private String shorts_url;
	private List<List<Double>> segments;
}