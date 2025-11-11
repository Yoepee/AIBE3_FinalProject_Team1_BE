package com.back.domain.region.region.controller;

import com.back.domain.region.region.dto.RegionCreateReqBody;
import com.back.domain.region.region.dto.RegionResBody;
import com.back.domain.region.region.dto.RegionUpdateReqBody;
import com.back.domain.region.region.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/adm/regions")
public class RegionAdmController {

    private final RegionService regionService;

    @PostMapping
    public ResponseEntity<RegionResBody> createRegion(@Valid @RequestBody RegionCreateReqBody regionCreateReqBody) {
        RegionResBody regionResBody = regionService.createRegion(regionCreateReqBody);
        return ResponseEntity.ok(regionResBody);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RegionResBody> updateRegion(
            @PathVariable("id") Long regionId,
            @Valid @RequestBody RegionUpdateReqBody regionUpdateReqBody) {
        RegionResBody regionResBody = regionService.updateRegion(regionId, regionUpdateReqBody);
        return ResponseEntity.ok(regionResBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable("id") Long regionId) {
        regionService.deleteRegion(regionId);
        return ResponseEntity.ok().build();
    }
}
