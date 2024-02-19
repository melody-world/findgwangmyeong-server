package com.app.findgwangmyeongserver.repo;


import com.app.findgwangmyeongserver.entity.BibleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibleRepository extends JpaRepository<BibleEntity, Integer> {

}
