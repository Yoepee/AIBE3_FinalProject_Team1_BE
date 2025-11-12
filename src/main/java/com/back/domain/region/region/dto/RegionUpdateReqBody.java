package com.back.domain.region.region.dto;

import jakarta.validation.constraints.NotBlank;

public record RegionUpdateReqBody(
        @NotBlank
        String name
) {
}
