package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.TradeRentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRentRepository extends JpaRepository<TradeRentEntity, Long> {

    public List<TradeRentEntity> findByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

    public long countByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

}
