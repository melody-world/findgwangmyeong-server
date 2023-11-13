package com.app.findgwangmyeongserver.repo;

import com.app.findgwangmyeongserver.dto.inter.ApartCode;
import com.app.findgwangmyeongserver.entity.ApartCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartCodeRepository extends JpaRepository<ApartCodeEntity, String> {

    ApartCodeEntity findByApartCode(String apartCode);

    List<ApartCodeEntity> findByLawdCd(String lawdCd);

    public void deleteByLawdCd(String lawdCd);

    public int countByLawdCd(String lawdCd);
    @Query(value =
    "SELECT " +
    "      C.APART_CODE as apartCode\n" +
    " \t , C.ADDRESS as address\n" +
    " \t , C.DORO_JUSO as doroJuso\n" +
    " \t , C.DONGMYUN as dongmyun\n" +
    " \t , C.LAND_NUMBER as landNumber\n" +
    " \t , C.ZIP_CODE as zipCode\n" +
    " \t , C.CONV_X as convX\n" +
    " \t , C.CONV_Y as convY\n" +
    " \t , C.LAWD_CD as lawdCd\n" +
    " FROM lawd_master A \n" +
    "  \t   INNER JOIN lawd_info B \n" +
    "  \t      ON A.MASTER_CD = B.MASTER_CD \t\n" +
    "  \t   INNER JOIN apart_code C \n" +
    "  \t      ON B.LAWD_CD = C.LAWD_CD \t\n" +
    "  WHERE A.MASTER_CD = :masterCd ", nativeQuery = true)
    public List<ApartCode> findByMasterCd(@Param("masterCd") String masterCd);

}
