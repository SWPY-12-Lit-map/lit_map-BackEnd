package com.lit_map_BackEnd.domain.board.repository;

import com.lit_map_BackEnd.domain.board.entity.MainBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<MainBanner, Long> {
    void deleteByImageUrl(String imageUrl);
}
