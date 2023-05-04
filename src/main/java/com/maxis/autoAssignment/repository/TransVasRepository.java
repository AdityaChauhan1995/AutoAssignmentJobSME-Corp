package com.maxis.autoAssignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maxis.autoAssignment.pojo.TransVas;


@Repository
public interface TransVasRepository extends CrudRepository<TransVas, String> {
	//@Query(nativeQuery = true)
	List<TransVas> getTransVas(@Param("regId")Integer regId);
	
	List<TransVas> getAllTransVas(@Param("easMasterRegId")Integer easMasterRegId);
	
	
}
