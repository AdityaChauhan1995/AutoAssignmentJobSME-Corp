package com.maxis.autoAssignment.repository;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxis.autoAssignment.pojo.SchAssignment;
@Repository
public interface SchAssignmentRepository extends CrudRepository<SchAssignment, String>{

//	@Query("select u from SchAssignment u where u.dateAssigned=TRUNC(SYSDATE) and u.workStatus='A' and u.availability='A' and u.schCategory like '%:sch_category%' and u.shift=:shift")
	List<SchAssignment> loadUserList(@Param("sch_category")String sch_cat, @Param("shift") List<String>  shifts);
	@Modifying
	@Transactional
	void updateSchAssignment(@Param("workStatus") String workStatus, @Param("totalAssigned") Integer totalAssigned, @Param("userId") String userId,@Param("schCategory") String schCategory);
	Integer isAvailable(@Param("userId")String bpuApprovedBy);
	@Query(nativeQuery = true)
	String getAMShift(@Param("team")String team);
	@Query(nativeQuery = true)
	String getPMShift(@Param("team")String team);

}
