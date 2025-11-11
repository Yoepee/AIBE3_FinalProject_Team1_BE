package com.back.domain.region.region.repository;

import com.back.domain.region.region.entity.Region;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    @EntityGraph(attributePaths = {"children"})
    Optional<Region> findRegionWithChildById(Long regionId);
}
