package com.maxis.autoAssignment.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maxis.autoAssignment.pojo.EasRegistration;
import com.maxis.autoAssignment.pojo.PortalRegistrationMaster2;
import com.maxis.autoAssignment.pojo.SchAssignment;
import com.maxis.autoAssignment.pojo.SchIdentifier;
import com.maxis.autoAssignment.pojo.TransVas;
import com.maxis.autoAssignment.repository.EasRegistrationRepository;
import com.maxis.autoAssignment.repository.EbdUserRepository;
import com.maxis.autoAssignment.repository.PortalRegistrationMasterRepository2;
import com.maxis.autoAssignment.repository.SchAssignmentRepository;
import com.maxis.autoAssignment.repository.SchIdentifierRepository;
import com.maxis.autoAssignment.repository.TransVasRepository;

import ch.qos.logback.classic.Logger;

@Service
public class AutoAssignmentService {
	Logger batchLogger = (Logger) LoggerFactory.getLogger("AutoAssignmentService");
	@Autowired
	PortalRegistrationMasterRepository2 portalRegistrationMasterRepository2;

	@Autowired
	SchIdentifierRepository schIdentifierRepo;
	
	@Autowired
	EbdUserRepository ebdUserRepo;
	
	@Autowired
	SchAssignmentRepository schAssignmentRepo;
	
	@Autowired
	EasRegistrationRepository easRegRepo;
	
	@Autowired
	TransVasRepository transVasRepo;
	
	public List<PortalRegistrationMaster2> getAssignmentListInd(List<String> dealerCode) {
		List<PortalRegistrationMaster2> assignmentList = new ArrayList<PortalRegistrationMaster2>();
		  final List<List<String>> partitions = ListUtils.partition(dealerCode, 999);
		  for (List<String> partition : partitions) {
			  assignmentList.addAll(portalRegistrationMasterRepository2.getAssignmentListInd(partition));
		  }
		return assignmentList;
	}
	public List<PortalRegistrationMaster2> getAssignmentListNormal(List<String> dealerCodeSME) {

		List<PortalRegistrationMaster2> assignmentList = new ArrayList<PortalRegistrationMaster2>();
		  final List<List<String>> partitions = ListUtils.partition(dealerCodeSME, 999);
		  for (List<String> partition : partitions) {
			  assignmentList.addAll(portalRegistrationMasterRepository2.getAssignmentListNormal(partition));
		  }
		return assignmentList;
	}
	public Integer loadCurrentWeek() {
	    Calendar cl = Calendar. getInstance();
	    Integer week = cl.get(Calendar.WEEK_OF_YEAR);
	    if(cl.get(Calendar.DAY_OF_WEEK)==1)
	    	--week;
		return week;
	}
	public Integer loadSchWeek() {
		return schIdentifierRepo.loadSchWeek();
	}
	public void deletePrevWeek() {
		schIdentifierRepo.deletePrevWeek();
	}
	public List<String> getDealerCodesForCorp() {
		return ebdUserRepo.getDealerCodesForCorp();
	}
	public List<String> getDealerCodesForSMEInd() {
		return ebdUserRepo.getDealerCodesForSMEInd();
	}
	public List<String> getDealerCodesForSME() {
		return ebdUserRepo.getDealerCodesForSME();
	}
	public List<SchAssignment> loadUserList(String sch_cat, List<String> shifts) {
		return schAssignmentRepo.loadUserList(sch_cat,shifts);
	}
	public List<SchIdentifier> loadIdentifierList(List<String> userIds) {
		return schIdentifierRepo.loadIdentifierList(userIds);
	}
	public EasRegistration geteasRegMaster(Integer easMasterRegId) {
		return easRegRepo.geteasRegMaster(easMasterRegId);
	}
	public List<TransVas> getTransVas(Integer regId) {
		return transVasRepo.getTransVas(regId);
	}
	
	public List<TransVas> getAllTransVas(Integer easMasterRegId) {
		return transVasRepo.getAllTransVas(easMasterRegId);
	}
	
	public boolean insertSchRegistrationAssign(PortalRegistrationMaster2 transDto, SchIdentifier idDto,
			SchAssignment tempDto, boolean iD_NOT_EXIST, boolean easInd) {
		try {
			if(iD_NOT_EXIST) {
	            batchLogger.info("in insertSchIdentifier user : " + idDto.getUserId() + " week : " + idDto.getWeekNo() + " id : " + idDto.getIdentity() + " category:" + idDto.getSch_category());
	            List<String> userIds = new ArrayList<String>();
	            userIds.add(idDto.getUserId());
//	            List<SchIdentifier> identityList = loadIdentifierListForIdentity(userIds,idDto.getSch_category(),idDto.getIdentity());
//	            if(identityList==null || identityList.isEmpty())
//	            	schIdentifierRepo.insertIdentifier(idDto.getUserId(),idDto.getWeekNo(),idDto.getIdentity(),idDto.getSch_category());
			}
			if(transDto.getEasMasterRegId()!=null && easInd) {
				easRegRepo.updateEasMaster(transDto.getEasMasterRegId(),transDto.getBpuApprovedBy());
				portalRegistrationMasterRepository2.updateregMasterForEas(transDto.getEasMasterRegId(),transDto.getBpuApprovedBy());
				schAssignmentRepo.updateSchAssignment(tempDto.getWorkStatus(), tempDto.getTotalAssigned(), tempDto.getUserId(), tempDto.getSchCategory());
			}
			else {
				schAssignmentRepo.updateSchAssignment(tempDto.getWorkStatus(), tempDto.getTotalAssigned(), tempDto.getUserId(), tempDto.getSchCategory());
				portalRegistrationMasterRepository2.updateregMaster(transDto.getRegId(),transDto.getBpuApprovedBy());
			}
				
			return true;
		}
		catch(Exception e) {
			batchLogger.info("Exception occureed in insertSchRegistrationAssign" + e.getMessage());
			return false;
			
		}
		
	}
	public List<SchIdentifier> loadIdentifierListForIdentity(List<String> userIds, String sch_cat, String identity) {
		batchLogger.info("Checking users for Identity: "+identity+" and category: "+sch_cat+" in SCH_IDENTIFIER");
		return schIdentifierRepo.loadIdentifierListForIdentity(userIds,sch_cat,identity);
	}
	public void updateStatusToPR(PortalRegistrationMaster2 regDto) {
		batchLogger.info("updating status to PR");
		if(regDto.getEasMasterRegId()!=null) {
			batchLogger.info("updating status to PR for Master Reg Id: "+regDto.getEasMasterRegId());
			easRegRepo.updateEASStatus(regDto.getEasMasterRegId());
			portalRegistrationMasterRepository2.updateRegStatusForEas(regDto.getEasMasterRegId());
		}
		else {
			batchLogger.info("updating status to PR for Reg Id: "+regDto.getRegId());
			portalRegistrationMasterRepository2.updateRegStatus(regDto.getRegId());
		}

	}
	
	public HashMap<String,Integer> getTotalAssigned(List<String> userId) {
		HashMap<String, Integer> lineCount = new HashMap<String, Integer>();
		for(String user:userId) {
			Integer count = portalRegistrationMasterRepository2.getLineCount(user);
			Integer countAppDirect = portalRegistrationMasterRepository2.getLineCountAppDirect(user);
			lineCount.put(user, count+countAppDirect);
		}
		return lineCount;
	}
	public boolean isAgentAvaialable(String bpuApprovedBy) {
		batchLogger.info("Checking if agent: "+bpuApprovedBy+" is available for the day");
		Integer count = schAssignmentRepo.isAvailable(bpuApprovedBy);
		if(count!=null && count>0)
			return true;
		else
			return false;
	}
	public String getAMShiftRange(String team) {
		return schAssignmentRepo.getAMShift(team);
	}
	public String getPMShiftRange(String team) {
		return schAssignmentRepo.getPMShift(team);
	}
	
}
