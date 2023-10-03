package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.ApartListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartListRepository extends JpaRepository<ApartListEntity, String> {

    List<ApartListEntity> findByLawdCd(String lawdCd);

}
