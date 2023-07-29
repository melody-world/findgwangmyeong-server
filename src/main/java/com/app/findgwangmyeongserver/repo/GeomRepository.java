package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.GeomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeomRepository extends JpaRepository<GeomEntity, Long> {

}
