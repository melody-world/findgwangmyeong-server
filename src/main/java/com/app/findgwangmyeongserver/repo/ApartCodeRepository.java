package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.ApartCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartCodeRepository extends JpaRepository<ApartCodeEntity, String> {

    List<ApartCodeEntity> findByLawdCd(String lawdCd);

}
