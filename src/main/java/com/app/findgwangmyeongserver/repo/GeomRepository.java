package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.GeomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeomRepository extends JpaRepository<GeomEntity, Long> {

    List<GeomEntity> findByStnKrNmIn(List<String> names);

    List<GeomEntity> findByOutStnNum(String outStnNum);
}
