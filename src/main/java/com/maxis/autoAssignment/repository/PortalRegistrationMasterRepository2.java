package com.maxis.autoAssignment.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxis.autoAssignment.pojo.PortalRegistrationMaster2;




@Repository
public interface PortalRegistrationMasterRepository2 extends CrudRepository<PortalRegistrationMaster2, String>{
	/*@Query("select regMaster "  
			+" from PortalRegistrationMaster2 regMaster where "
			+ " regMaster.regType not in ('PhoneOrder') and regMaster.bpuApprovedBy is null and regMaster.regStatus='PA' and regMaster.dealerCode in (:dealerCode) and regMaster.roadShowId is not null")*/
	List<PortalRegistrationMaster2> getAssignmentListInd(@Param("dealerCode")List<String> dealerCode);

	/*@Query("select regMaster "  
			+" from PortalRegistrationMaster2 regMaster where "
			+ " regMaster.regType not in ('PhoneOrder') and regMaster.bpuApprovedBy is null and regMaster.regStatus='PA' and regMaster.dealerCode in (:dealerCode) and regMaster.roadShowId is null")*/
	List<PortalRegistrationMaster2> getAssignmentListNormal(@Param("dealerCode")List<String> dealerCodeSME);

	@Transactional
	@Modifying
//	@Query("update PortalRegistrationMaster2 set bpuApprovedBy=:approverCode, tempApproverCode=:approverCode, regDateApproved=:regDateApproved, tempDateApproved=:regDateApproved, regStatus='PR' where easMasterRegId=:easMasterRegId")
	void updateregMasterForEas(@Param("easMasterRegId")Integer easMasterRegId, @Param("approverCode")String bpuApprovedBy);

	@Transactional
	@Modifying
//	@Query("update PortalRegistrationMaster2 set bpuApprovedBy=:approverCode, tempApproverCode=:approverCode, regDateApproved=:regDateApproved, tempDateApproved=:regDateApproved, regStatus='PR' where regId=:regId")
	void updateregMaster(@Param("regId")Integer regId, @Param("approverCode")String bpuApprovedBy);

	@Transactional
	@Modifying
	void updateRegStatusForEas(@Param("easMasterRegId")Integer easMasterRegId);

	@Transactional
	@Modifying
	void updateRegStatus(@Param("regId")Integer regId);
	
	@Query(nativeQuery = true)
	Integer getLineCount(@Param("approverCode") String bpuApprovedBy);

	@Query(nativeQuery = true)
	Integer getLineCountAppDirect(@Param("approverCode") String bpuApprovedBy);

}
