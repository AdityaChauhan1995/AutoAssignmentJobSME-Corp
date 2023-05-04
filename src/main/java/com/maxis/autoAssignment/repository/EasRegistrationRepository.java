package com.maxis.autoAssignment.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxis.autoAssignment.pojo.EasRegistration;

@Repository
public interface EasRegistrationRepository extends CrudRepository<EasRegistration, String>{
//	@Query("select eas from EasRegistration where eas.easMasterRegId=:easMasterRegId")
	EasRegistration geteasRegMaster(@Param("easMasterRegId")Integer easMasterRegId);
	@Transactional
	@Modifying
//	@Query("update EasRegistration set approverCode =:appproverCode, dateModified=:dateModified ,masterStatus ='PR' where easMasterRegId=:easMasterRegId ")
	void updateEasMaster(@Param("easMasterRegId")Integer easMasterRegId, @Param ("appproverCode")String bpuApprovedBy);
	@Transactional
	@Modifying
	void updateEASStatus(@Param("easMasterRegId")Integer easMasterRegId);

}
