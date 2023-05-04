package com.maxis.autoAssignment.repository;


import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxis.autoAssignment.pojo.SchIdentifier;
@Repository
public interface SchIdentifierRepository extends CrudRepository<SchIdentifier, String> {
	

//	@Query("select distinct(weekNo) from SchIdentifier")
	Integer loadSchWeek();

	@Modifying
    @Transactional
//	@Query("delete from SchIdentifier")
	void deletePrevWeek();
	
	//@Query(value="SELECT ide from SchIdentifier where userId in :userIds",nativeQuery = true)
	List<SchIdentifier> loadIdentifierList(@Param("userIds")Collection<String> userIds);
	
	List<SchIdentifier> loadIdentifierListForIdentity(@Param("userIds")Collection<String> userIds, @Param("category")String sch_cat, @Param("identity")String identity);

	@Modifying
	@Transactional
	//@Query(value = "insert into SchIdentifier (userId, weekNo, identity, sch_category) values (:userId, :weekNo, :identity, :category)",
	@Query(nativeQuery = true)
	void insertIdentifier(@Param("userId")String userId, @Param("weekNo")Integer weekNo, @Param("identity") String identity, @Param("category")String category);


}
