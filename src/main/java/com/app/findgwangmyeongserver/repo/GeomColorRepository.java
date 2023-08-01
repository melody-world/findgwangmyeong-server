package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.GeomColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeomColorRepository extends JpaRepository<GeomColorEntity, Long> {

}
