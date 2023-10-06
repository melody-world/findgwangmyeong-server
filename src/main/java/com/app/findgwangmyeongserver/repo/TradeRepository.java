package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, Long> {

    public List<TradeEntity> findByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

    public long countByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

    public void deleteByLawdCdAndYearAndMonth(String lawdCd, String year, String month);

    @Query(value =
        "select " +
        "  ti.apart_name AS apartName " +
        ", ti.apart_dong AS apartDong " +
        ", MAX(ti.address) AS address " +
        ", MAX(ti.apart_street) AS apartStreet " +
        ", MAX(ti.lawd_cd) AS lawdCd " +
        "from trade_info ti " +
        "where ti.lawd_cd = '41210' " +
        "group by ti.apart_name, ti.apart_dong", nativeQuery = true)
    public List<Apart> findByLawdCd(@Param("lawdCd") String lawdCd);

    interface Apart {
        String getApartName();
        String getApartDong();
        String getApartStreet();
        String getAddress();
        String getLawdCd();
    }

}
