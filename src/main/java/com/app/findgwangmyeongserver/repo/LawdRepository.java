package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.LawdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawdRepository extends JpaRepository<LawdEntity, String> {


}
