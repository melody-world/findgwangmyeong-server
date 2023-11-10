package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.LawdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawdRepository extends JpaRepository<LawdEntity, String> {

    public List<LawdEntity> findByMasterCd(String masterCd);
}
