package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.ApartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartRepository extends JpaRepository<ApartEntity, String> {

    public List<ApartEntity> findByLawdCd(String lawdCd);

    public ApartEntity findBySeq(long seq);

    public void deleteByMasterCd(String masterCd);

}
