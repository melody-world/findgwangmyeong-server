package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

    public List<TradeEntity> findByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

    public long countByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

    public void deleteByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

}
