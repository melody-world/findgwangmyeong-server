package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.ApartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartRepository extends JpaRepository<ApartEntity, String> {

    List<ApartEntity> findByLawdCd(String lawdCd);

}
