package com.maxis.autoAssignment.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.maxis.autoAssignment.pojo.EbdUser;


@Repository
public interface EbdUserRepository extends CrudRepository<EbdUser, String> {
	//@Query("select user.dealerCode from EbdUser user where user.dealerName not like '%Maxis%' and user.dealerName not like '%EBD%'")
	List<String> getDealerCodesForSMEInd();
	
//	@Query("select user.dealerCode from EbdUser user where user.dealerName not like '%Maxis%' and user.dealerName like '%EBD%'")
	List<String> getDealerCodesForCorp();
	
//	@Query("select user.dealerCode from EbdUser user where user.dealerName not like '%Maxis%' and user.dealerName not like '%BPU PROCESSING UNIT(EBDPU.00001)%'")
	List<String> getDealerCodesForSME();
}
