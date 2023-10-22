package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.dto.inter.Apart;
import com.app.findgwangmyeongserver.dto.inter.ApartCode;
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
        "select distinct " +
        "      x.apartName " +
        "    , x.apartDong " +
        "    , x.address " +
        "    , x.lawdCd " +
        " from ( " +
        "       select " +
        "             ti.apart_name AS apartName " +
        "           , ti.apart_dong AS apartDong " +
        "           , MAX(ti.address) AS address " +
        "           , MAX(ti.lawd_cd) AS lawdCd " +
        "       from trade_info ti " +
        "       where ti.lawd_cd = :lawdCd " +
        "       group by ti.apart_name, ti.apart_dong" +
        "       union all " +
        "       select " +
        "             ti.apart_name AS apartName " +
        "           , ti.apart_dong AS apartDong " +
        "           , MAX(ti.address) AS address " +
        "           , MAX(ti.lawd_cd) AS lawdCd " +
        "       from trade_rent_info ti " +
        "       where ti.lawd_cd = :lawdCd " +
        "      group by ti.apart_name, ti.apart_dong " +
        "       ) x ", nativeQuery = true)
    public List<Apart> findByLawdCd(@Param("lawdCd") String lawdCd);

    @Query(value =
        "select x.seq AS seq " +
        "     , x.apart_code AS apartCode " +
        " from ( " +
        "       select al.seq " +
        "            , ac.apart_code " +
        "         from apart_list al " +
        "              left outer join apart_code ac " +
        "                on ac.lawd_cd = al.lawd_cd " +
        "               and ac.address like concat('%', al.apart_dong, ' ', al.address, '%')" +
        "       where al.lawd_cd = :lawdCd " +
        "     ) x " +
        "where x.apart_code is not null", nativeQuery = true)
    public List<ApartCode> findByLawdCd2(@Param("lawdCd") String lawdCd);

    @Query(value =
        "select x.seq AS seq " +
        "     , x.apart_code AS apartCode " +
        "  from ( " +
        "        select al.seq " +
        "             , ac.apart_code " +
        "          from apart_list al " +
        "               inner join " +
        "               ( " +
        "                select x.seq " +
        "                  from ( " +
        "                        select al.seq " +
        "                             , ac.apart_code " +
        "                          from apart_list al " +
        "                               left outer join apart_code ac " +
        "                                 on ac.lawd_cd = al.lawd_cd " +
        "                                and ac.address like concat('%', al.apart_dong, ' ', al.address, '%')" +
        "                         where al.lawd_cd = :lawdCd " +
        "                        ) x " +
        "                  where x.apart_code is null " +
        "               ) tmp " +
        "              on al.seq = tmp.seq " +
        "              left outer join apart_code ac " +
        "                on ac.lawd_cd = al.lawd_cd " +
        "               and ac.apart_name like concat('%', al.apart_name, '%') " +
        "        where al.lawd_cd = :lawdCd " +
        "       ) x" +
        " where x.apart_code is not null ", nativeQuery = true)
    public List<ApartCode> findByLawdCd3(@Param("lawdCd") String lawdCd);


}
