package com.lit_map_BackEnd.domain.member.repository;

import com.lit_map_BackEnd.domain.member.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher,Long> {

    List<Publisher> findByPublisherNumber(Long publisherNumber);

    // Member의 litmapEmail로 Publisher를 찾는 메서드
    Optional<Publisher> findByMemberListLitmapEmail(String litmapEmail);

}
