package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.LawdMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawdMasterRepository extends JpaRepository<LawdMasterEntity, String> {


}
