package com.maxis.autoAssignment.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maxis.autoAssignment.pojo.EasRegistration;
import com.maxis.autoAssignment.pojo.PortalRegistrationMaster2;
import com.maxis.autoAssignment.pojo.TransVas;
import com.maxis.autoAssignment.pojo.SchAssignment;
import com.maxis.autoAssignment.pojo.SchIdentifier;
import com.maxis.autoAssignment.repository.EbdUserRepository;
import com.maxis.autoAssignment.service.AutoAssignmentService;
import com.maxis.autoAssignment.repository.TransVasRepository;
import ch.qos.logback.classic.Logger;

@Component
public class AutoAssingmentController {
	Logger batchLogger = (Logger) LoggerFactory.getLogger("autoAssignmentController");
	@Autowired
	AutoAssignmentService scheduleServiceHandler;
	@Autowired
	EbdUserRepository ebdUserRepo;

	private String WORK_STATUS_ACTIVE= "A";
	private String WORK_STATUS_PAUSE= "P";
	private Comparator<SchAssignment> comparator = new SchedulerTotalAssignComparator();
	//private PriorityQueue<SchAssignment> schAssignQueueAM = new PriorityQueue<SchAssignment>(30, comparator);
	//private PriorityQueue<SchAssignment> schAssignQueuePM = new PriorityQueue<SchAssignment>(30, comparator);
	private Queue<SchAssignment> schAssignQueueAM = new LinkedList<SchAssignment>();
	private Queue<SchAssignment> schAssignQueuePM = new LinkedList<SchAssignment>();
	private List<PortalRegistrationMaster2> easMasterRegList = new ArrayList<PortalRegistrationMaster2>();
	private List<String> userIdsAM = new ArrayList<String>();
	private List<String> userIdsPM = new ArrayList<String>();
	private Integer currWeek = 0;
	public void getData() {

		currWeek = scheduleServiceHandler.loadCurrentWeek();
		if (currWeek != null && currWeek.intValue() != 0) {
//			clearingSchIdentifier(currWeek);
			//Data for Corporate Orders
			List<String> dealerCodeCorp = scheduleServiceHandler.getDealerCodesForCorp();
			List<PortalRegistrationMaster2> regListCorp = scheduleServiceHandler.getAssignmentListInd(dealerCodeCorp);
			if(regListCorp!=null && !regListCorp.isEmpty()) {
				scheduleOrdersNew(regListCorp,"corp",currWeek);
			}
			else {
				batchLogger.info("RegList for Corp is null");
			}

			//Data for SME individual
			List<String> dealerCodeSMEInd = scheduleServiceHandler.getDealerCodesForSMEInd();
			List<PortalRegistrationMaster2> regListSMEInd = scheduleServiceHandler.getAssignmentListInd(dealerCodeSMEInd);
			if(regListSMEInd!=null && !regListSMEInd.isEmpty()) {
				scheduleOrdersNew(regListSMEInd,"SMEInd",currWeek);
			}
			else{
				batchLogger.info("RegList for SMEInd is null");
			}
			//Data for SME
			List<String> dealerCodeSME = scheduleServiceHandler.getDealerCodesForSME();
			List<PortalRegistrationMaster2> regListSME = scheduleServiceHandler.getAssignmentListNormal(dealerCodeSME);
			if(regListSME!=null && !regListSME.isEmpty()) {
				scheduleOrdersNew(regListSME,"SME",currWeek);
			}
			else {
				batchLogger.info("RegList for SME is null");
			}


		}

		else {
			batchLogger.info("In main current week : 0");
		}


	}
	private void scheduleOrdersNew(List<PortalRegistrationMaster2> regList, String category, Integer currWeek) {
		String sch_cat = null;
		boolean smeCat = true;
		String team = null;
		if(category.equalsIgnoreCase("corp")) {
			sch_cat = "corp";
			smeCat = false;
			team="Corporate";
		}
		else {
			team="SME";
			if(category.equalsIgnoreCase("SMEInd"))
				sch_cat="BIP";
			else if(category.equalsIgnoreCase("SME"))
				sch_cat = "non-BIP";
		}
		
		String amShiftRange=scheduleServiceHandler.getAMShiftRange(team);
		String pmShiftRange=scheduleServiceHandler.getPMShiftRange(team);
		
		int amStartHour = Integer.parseInt(amShiftRange.split(" ")[0].split(":")[0]);
		int amStartMin = Integer.parseInt(amShiftRange.split(" ")[0].split(":")[1]);
		int amEndHour = Integer.parseInt(amShiftRange.split(" ")[2].split(":")[0]);
		int amEndMin = Integer.parseInt(amShiftRange.split(" ")[2].split(":")[1]);
		
		int pmStartHour = Integer.parseInt(pmShiftRange.split(" ")[0].split(":")[0]);
		int pmStartMin = Integer.parseInt(pmShiftRange.split(" ")[0].split(":")[1]);
		int pmEndHour = Integer.parseInt(pmShiftRange.split(" ")[2].split(":")[0]);
		int pmEndMin = Integer.parseInt(pmShiftRange.split(" ")[2].split(":")[1]);
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String shift = null;
		List<String> shifts = new ArrayList<String>();
		if(((pmStartHour<amEndHour)||(pmStartHour==amEndHour && pmStartMin<amEndMin))&& ((timeOfDay>=pmStartHour && timeOfDay<amEndHour)||
				((timeOfDay==pmStartHour && minute>=pmStartMin)&&timeOfDay<amEndHour) || (timeOfDay> pmStartHour &&((timeOfDay==amEndHour && minute<amEndMin)))))
		
		{
			shift="AM,PM";
			shifts.add("AM");
			shifts.add("PM");
		}
		else if((timeOfDay> amStartHour && timeOfDay<amEndHour)||((timeOfDay==amStartHour && minute>=amStartMin)&&timeOfDay<amEndHour) || (timeOfDay> amStartHour &&((timeOfDay==amEndHour && minute<amEndMin)))) {
			shift="AM";
			shifts.add(shift);
		}

		else if ((timeOfDay> pmStartHour && timeOfDay<pmEndHour)||((timeOfDay==pmStartHour && minute>=pmStartMin)&&timeOfDay<pmEndHour) || (timeOfDay> pmStartHour &&((timeOfDay==pmEndHour && minute<pmEndMin)))) {
			shift="PM";
			shifts.add(shift);
		}

		else
			return;
		System.out.println("********shift:" + shift);
		List<SchAssignment> loadUsersList = scheduleServiceHandler.loadUserList(sch_cat,shifts);
		loadUsersList = orderingUserlist(loadUsersList);

//		if (loadUsersList != null && !loadUsersList.isEmpty())
//		{
//
//			Iterator<SchAssignment> userIterator = loadUsersList.iterator();
//			while (userIterator.hasNext())
//			{
//				SchAssignment sdto = (SchAssignment) userIterator.next();
//				if(sch_cat.equals("BIP")) {
//					//Removing users for non-BIP only
//					String prodCat[]=sdto.getSchCategory().split(",");
//					if(sdto.getSchCategory().contains("non-BIP") && prodCat.length==1) {
//						userIterator.remove();
//						continue;
//					}
//				}
//
//				if(sdto.getShift().equals("AM")) {
//					schAssignQueueAM.add(sdto);
//					userIdsAM.add(sdto.getUserId());
//				}
//				else if (sdto.getShift().equals("PM")) {
//					schAssignQueuePM.add(sdto);
//					userIdsPM.add(sdto.getUserId());
//					batchLogger.info("Before User : " + sdto.getUserId() + " total assigned : " + sdto.getTotalAssigned() + " work status : " + sdto.getWorkStatus() + " shift : " + sdto.getShift());
//				}
//			}
//		}
//		else {
//			batchLogger.info("No active users for :"+category);
//		}
		
		if(loadUsersList!=null && !loadUsersList.isEmpty()) {
				batchLogger.info("No. of active users for "+category+" : "+loadUsersList.size());
				List<SchIdentifier> loadIdentityList = new ArrayList<SchIdentifier>();
				batchLogger.info("=========================================================================================");
				batchLogger.info("Starting scheduler............ ");

				easMasterRegList = getEasMasterRegList(regList);
				for(PortalRegistrationMaster2 regDto : easMasterRegList) {
					try {
						boolean successful = false;
						orderingQueue(loadUsersList,sch_cat,category);
						System.out.println("ORDER NO - "+ regDto.getEasMasterRegId());
						if(shift.equals("AM,PM")) {
							batchLogger.info("Hour of day : 12pm to 3 pm, so using users with PM shift first");
							successful = schedulingProcess(regDto, "PM-2",sch_cat,smeCat);
							if(!successful) {
								batchLogger.info("No user in PM shift was assigned hence using ones with AM shift now");
								schedulingProcess(regDto, "AM-2",sch_cat,smeCat);
							}

						}else {
							schedulingProcess(regDto, shift, sch_cat,smeCat);
						}
					}
					catch(Exception e) {
						batchLogger.info("Exception occured in scheduleOrders"+e.getMessage());
					}
				}
				for(PortalRegistrationMaster2 regDto : regList) {
					if(regDto.getEasMasterRegId() == null){
					try {
						boolean successful = false;
						orderingQueue(loadUsersList,sch_cat,category);
						System.out.println("ORDER NO - "+ regDto.getRegId());
						if(shift.equals("AM,PM")) {
							batchLogger.info("Hour of day : 12pm to 3 pm, so using users with PM shift first");
							successful = schedulingProcess(regDto, "PM-2",sch_cat,smeCat);
							if(!successful) {
								batchLogger.info("No user in PM shift was assigned hence using ones with AM shift now");
								schedulingProcess(regDto, "AM-2",sch_cat,smeCat);
							}

						}else {
							schedulingProcess(regDto, shift, sch_cat,smeCat);
						}
					}
					catch(Exception e) {
						batchLogger.info("Exception occured in scheduleOrders"+e.getMessage());
					}
				  }
				}
//				schAssignQueueAM = new PriorityQueue<SchAssignment>(30, comparator);
//				schAssignQueuePM = new PriorityQueue<SchAssignment>(30, comparator);
				schAssignQueueAM = new LinkedList<SchAssignment>();
				schAssignQueuePM =new LinkedList<SchAssignment>();
				userIdsAM = new ArrayList<String>();
				userIdsPM = new ArrayList<String>();

			}else{
				batchLogger.info("No active users for :"+category);
			}
//			schAssignQueueAM = new PriorityQueue<SchAssignment>(30, comparator);
//			schAssignQueuePM = new PriorityQueue<SchAssignment>(30, comparator);
			schAssignQueueAM = new LinkedList<SchAssignment>();
			schAssignQueuePM = new LinkedList<SchAssignment>();
			userIdsAM = new ArrayList<String>();
			userIdsPM = new ArrayList<String>();

		
		

	}

	private boolean schedulingProcess(PortalRegistrationMaster2 regDto, String shift, String sch_cat, boolean smeCat) {
		try {
//			PriorityQueue<SchAssignment> schAssignQueue = new PriorityQueue<SchAssignment>(30, comparator);
//			PriorityQueue<SchAssignment> schAssignQueueTemp = new PriorityQueue<SchAssignment>(30, comparator);
			Queue<SchAssignment> schAssignQueue = new LinkedList<SchAssignment>();
			Queue<SchAssignment> schAssignQueueTemp = new LinkedList<SchAssignment>();
			List<String> userIds = new ArrayList<String>();

			if(shift.equals("PM-2")|| shift.equals("PM")) {
				schAssignQueue.addAll(schAssignQueuePM);
				schAssignQueueTemp.addAll(schAssignQueuePM);
				userIds.addAll(userIdsPM);
			}else {
				schAssignQueue.addAll(schAssignQueueAM);
				schAssignQueueTemp.addAll(schAssignQueueAM);
				userIds.addAll(userIdsAM);
			}
			boolean assigned = false;
			if (regDto == null)
			{
				batchLogger.info("In main end-of registrations queue.....");
				return false;
			}

			Integer totalAssigned = 0;
			Integer regId = null;
			String identity = null;
			boolean easInd = false;
			if(regDto.getGroupId()!=null && regDto.getGroupId().equals(6)) {
				totalAssigned = 1;
				regId = regDto.getRegId();
			}
			else {
				if(regDto.getEasMasterRegId()==null) {
					totalAssigned = regDto.getTransMsisdn().size();
					regId = regDto.getRegId();
				}
				else {
					regId = regDto.getEasMasterRegId();
					easInd = true;
					if(schAssignQueueTemp != null && !schAssignQueueTemp.isEmpty()){
					EasRegistration easReg = scheduleServiceHandler.geteasRegMaster(regDto.getEasMasterRegId());
					if(easReg.getPortalRegistrationMaster()!=null && !easReg.getPortalRegistrationMaster().isEmpty()) {
						for(PortalRegistrationMaster2 r: easReg.getPortalRegistrationMaster()) {
							totalAssigned = totalAssigned + r.getTransMsisdn().size();
						}
					  }
					}
				}
			}

			if(regDto.getRoadShowId()!=null)
				identity = regDto.getCustInfo().getNewIc();
			else
				identity = regDto.getCustInfo().getCustBrnNo();

			if(regDto.getApproverRemarks()!=null && regDto.getBpuApprovedBy()!=null) {
				batchLogger.info("The Order :"+regId+" is of resubmission with assigned agent: "+regDto.getBpuApprovedBy());
				batchLogger.info("Checking if the same agent is available.");
				boolean available = scheduleServiceHandler.isAgentAvaialable(regDto.getBpuApprovedBy());
				if(available) {
					/*if(userIds.contains(regDto.getBpuApprovedBy()))*/
					batchLogger.info("Agent is available so no need for reassignment.");
					scheduleServiceHandler.updateStatusToPR(regDto);
					return true;
				}
				else {
					batchLogger.info("Agent is not available so reassigning a new agent.");
				}

			}
			
			if(totalAssigned!=null && totalAssigned.intValue()!=0 && identity!=null) {
			batchLogger.info("RegId : " + regId + " MSISDN count : "+totalAssigned +"Identity : "+identity);

				boolean ID_NOT_EXIST = true;
				SchIdentifier idDto;
				Integer msisdnCount;
				SchAssignment tempDto = null;
				boolean limitExceeded = false;
				boolean limitReached= false;
				boolean USER_EXIST = false;
				//List<SchIdentifier> identityList = scheduleServiceHandler.loadIdentifierListForIdentity(userIds,sch_cat,identity);
				HashMap<String,Integer> totalCountMap = null; 
				List<TransVas> transVasList = null;
				
				if(schAssignQueueTemp != null && !schAssignQueueTemp.isEmpty()){
					batchLogger.info("getTotalAssigned started");
					totalCountMap = scheduleServiceHandler.getTotalAssigned(userIds);
					batchLogger.info("getTotalAssigned ended");
					if(regDto.getEasMasterRegId() != null)
					{
						batchLogger.info("getAllTransVas started");
						transVasList=	scheduleServiceHandler.getAllTransVas(regDto.getEasMasterRegId());
						batchLogger.info("getAllTransVas ended");
					}else {
						batchLogger.info("getTransVas started");
						transVasList=	scheduleServiceHandler.getTransVas(regDto.getRegId());
						batchLogger.info("getTransVas ended");
					}
					
				}
				PortalRegistrationMaster2 transDto = new PortalRegistrationMaster2();
//				if(identityList!=null && !identityList.isEmpty()) {
//					Iterator<SchIdentifier> it = identityList.iterator();
//					while(it.hasNext()) {
//						idDto = (SchIdentifier)it.next();
//						batchLogger.info("Existing user : " + idDto.getUserId());
//						Iterator<SchAssignment> ite = schAssignQueue.iterator();
//						while (ite.hasNext())
//						{
//							SchAssignment userDto = (SchAssignment) ite.next();
//							userDto.setTotalAssigned(totalCountMap.get(userDto.getUserId()));
//							msisdnCount = totalAssigned;
//							if (userDto.getWorkStatus() != null && "A".equalsIgnoreCase(userDto.getWorkStatus()) && idDto.getUserId().equals(userDto.getUserId()))
//							{
//								msisdnCount = msisdnCount + userDto.getTotalAssigned();
//								if(msisdnCount.intValue() > userDto.getDailyCap().intValue()) {
//									batchLogger.info("The daily cap limit for user: "+idDto.getUserId()+" will exceed for regId:"+regId+" with msidn count:"+totalAssigned);
//									batchLogger.info("Moving to the next active user for this order.");
//									limitExceeded= true;
//									break;
//								}
//								if(smeCat && regDto.getGroupId()!=null && regDto.getGroupId().intValue()!=0) {
//									batchLogger.info("Checking the products excluded for the user: "+idDto.getUserId());
//									batchLogger.info("Product Id for regId: "+regId+" is "+regDto.getGroupId());
//									if(userDto.getProductExcluded()!=null && !userDto.getProductExcluded().equals("")) {
//										List<String> prods = Stream.of(userDto.getProductExcluded().split(",")).collect(Collectors.toList());
//										if(prods.contains(regDto.getGroupId().toString())) {
//											batchLogger.info("Product Id is to be excluded for the user. Hence moving on to the next user");
//											limitExceeded= true;
//											break;
//										}
//									}
//									else {
//										batchLogger.info("No products are to be excluded.");
//									}
//								}
//								
//								if(transVasList != null && transVasList.size()>0){
//									batchLogger.info("Checking the component excluded for the user: "+idDto.getUserId());
//									boolean componentToBeIncluded = componentIncluded(userDto.getComponentInclusion(),regDto.getGroupId(),transVasList);
//									if(!componentToBeIncluded){
//										batchLogger.info("Component Id is not included for the user. Hence moving on to the next user");
//										limitExceeded= true;
//										break;
//									}
//								}
//								
//								
//								transDto.setBpuApprovedBy(idDto.getUserId());
//								transDto.setRegDateApproved(new Date((new java.util.Date()).getTime()));
//								assigned = true;
//								tempDto = new SchAssignment();
//								tempDto.setUserId(userDto.getUserId());
//								tempDto.setDateAssigned(userDto.getDateAssigned());
//								tempDto.setTotalAssigned(msisdnCount);
//								if(msisdnCount.intValue() == userDto.getDailyCap().intValue()) {
//									tempDto.setWorkStatus(WORK_STATUS_PAUSE);
//									limitReached = true; //remove this user for further assignments since daily limit has been reached
//								}
//								else
//									tempDto.setWorkStatus(WORK_STATUS_ACTIVE);
//								tempDto.setSchCategory(userDto.getSchCategory());
//								tempDto.setDailyCap(userDto.getDailyCap());
//								tempDto.setAvailability(userDto.getAvailability());
//								tempDto.setProductExcluded(userDto.getProductExcluded());
//								tempDto.setComponentInclusion(userDto.getComponentInclusion());
//								tempDto.setShift(userDto.getShift());
//								tempDto.setDateModified(userDto.getDateModified());
//								ite.remove();
//								USER_EXIST = true;
//								break;
//							}
//
//							if(limitExceeded && it.hasNext()) {
//								limitExceeded = false;
//								continue;
//							}
//							else if (limitExceeded && !it.hasNext()) {
//								break;
//							}
//						}
//						if(USER_EXIST) {
//							if(!limitReached)
//								schAssignQueue.add(tempDto);
//							transDto.setRegId(regDto.getRegId());
//							transDto.setEasMasterRegId(regDto.getEasMasterRegId());
//							ID_NOT_EXIST = false;
//							assigned = true;
//							break;
//						}
//					}
//				}

				idDto = new SchIdentifier();
				
				while(!schAssignQueueTemp.isEmpty())
				{
					SchAssignment userDto = (SchAssignment) schAssignQueueTemp.peek();
					if (userDto != null)
					{
						boolean productExclusionCheck = true;
						boolean dailyLimitCheck = true;
						userDto.setTotalAssigned(totalCountMap.get(userDto.getUserId()));
						batchLogger.info("User on top : " + userDto.getUserId() + " Total Assigned : " + userDto.getTotalAssigned() +" Daily Cap Limit: "+userDto.getDailyCap());
						msisdnCount = totalAssigned;
						if (userDto.getTotalAssigned() != null)
						{
							msisdnCount = msisdnCount + userDto.getTotalAssigned();
						}
						
						if(regDto.getGroupId()!=null && regDto.getGroupId().intValue()!=0) {
							if(regDto.getGroupId() ==13 || regDto.getGroupId() ==15 || checkSME(transVasList)){
//							schAssignQueueTemp = new PriorityQueue<SchAssignment>(30, comparator);
//							schAssignQueueTemp = new LinkedList<SchAssignment>();
							while(!schAssignQueue.isEmpty()) {
								SchAssignment user = (SchAssignment) schAssignQueue.peek();
								batchLogger.info("Checking the components included for the user: "+user.getUserId());

								if(transVasList != null && transVasList.size()>0){
									batchLogger.info("Checking the component to be included for the user: "+user.getUserId());
									boolean componentToBeIncluded = componentIncluded(user.getComponentInclusion(),regDto.getGroupId(),transVasList);
									if(!componentToBeIncluded){
										batchLogger.info("Component Id is to be excluded for the user. Hence moving on to the next user");
										schAssignQueue.poll();
//										schAssignQueueTemp.add(user);
										user=(SchAssignment)schAssignQueue.peek();
										if(user!=null)
											batchLogger.info("Next User : " + user.getUserId() + " Total Assigned : " + user.getTotalAssigned() +" Daily Cap Limit: "+user.getDailyCap());
									}else{
										batchLogger.info("Components to be included.");
										userDto = user;
										userDto.setTotalAssigned(totalCountMap.get(userDto.getUserId()));
										if (userDto.getTotalAssigned() != null)
										{
											msisdnCount = totalAssigned + userDto.getTotalAssigned();
										}
										break;
									}
								}
								else {
									batchLogger.info("Component Id is to be excluded for the user. Hence moving on to the next user");
									schAssignQueue.poll();
//									schAssignQueueTemp.add(user);
									user=(SchAssignment)schAssignQueue.peek();
									if(user!=null)
										batchLogger.info("Next User : " + user.getUserId() + " Total Assigned : " + user.getTotalAssigned() +" Daily Cap Limit: "+user.getDailyCap());
								}

							}
							if(schAssignQueue.isEmpty()) {
								batchLogger.info("Order with regId: "+regId+" cannot be assgned to any active user due to component exclusion.");
//								schAssignQueue = schAssignQueueTemp;
								return false;
							}
						  }
						}
						
						
						if(smeCat && regDto.getGroupId()!=null && regDto.getGroupId().intValue()!=0) {
//							schAssignQueueTemp = new PriorityQueue<SchAssignment>(30, comparator);
//							schAssignQueueTemp = new LinkedList<SchAssignment>();
							batchLogger.info("Checking the products excluded for the user: "+idDto.getUserId());
							batchLogger.info("Product Id for regId: "+regId+" is "+regDto.getGroupId());
							while(!schAssignQueue.isEmpty()) {
								SchAssignment user = (SchAssignment) schAssignQueue.peek();
								if(user.getProductExcluded()!=null && !user.getProductExcluded().equals("")) {
									List<String> prods = Stream.of(user.getProductExcluded().split(",")).collect(Collectors.toList());
									if(prods.contains(regDto.getGroupId().toString())) {
										batchLogger.info("Product Id is to be excluded for the user. Hence moving on to the next user");
										schAssignQueue.poll();
										productExclusionCheck = false;
//										schAssignQueueTemp.add(user);
										user=(SchAssignment)schAssignQueue.peek();
										if(user!=null)
											batchLogger.info("Next User : " + user.getUserId() + " Total Assigned : " + user.getTotalAssigned() +" Daily Cap Limit: "+user.getDailyCap());
										//										continue;
									}

									else {
										batchLogger.info("Order will be assigned to user: "+user.getUserId());
										userDto = user;
										userDto.setTotalAssigned(totalCountMap.get(userDto.getUserId()));
										if (userDto.getTotalAssigned() != null)
										{
											msisdnCount = totalAssigned + userDto.getTotalAssigned();
										}
										break;
									}

								}
								else {
									batchLogger.info("No products are to be excluded.");
									userDto = user;
									userDto.setTotalAssigned(totalCountMap.get(userDto.getUserId()));
									if (userDto.getTotalAssigned() != null)
									{
										msisdnCount = totalAssigned + userDto.getTotalAssigned();
									}
									break;
								}

							}
							if(schAssignQueue.isEmpty()) {
								batchLogger.info("Order with regId: "+regId+" cannot be assgned to any active user due to Product exclusion.");
//								schAssignQueue = schAssignQueueTemp;

								return false;
							}
						}
						
						if(msisdnCount.intValue() > userDto.getDailyCap().intValue()) {
							batchLogger.info("The daily cap limit for user: "+idDto.getUserId()+" will exceed for regId:"+regId+" with msidn count:"+totalAssigned);
							batchLogger.info("Moving to the next active user for this order.");
							int size = schAssignQueue.size();
							//schAssignQueue.poll();
//							schAssignQueueTemp = new PriorityQueue<SchAssignment>(30, comparator);
//							schAssignQueueTemp = new LinkedList<SchAssignment>();
							for(int i =0 ;i<size;i++) {
								schAssignQueue.poll();
								dailyLimitCheck  =false;
//								schAssignQueueTemp.add(userDto);
								SchAssignment user = (SchAssignment) schAssignQueue.peek();
								if(user!=null) {
									user.setTotalAssigned(totalCountMap.get(user.getUserId()));
									msisdnCount = totalAssigned + user.getTotalAssigned();
									batchLogger.info("Next User : " + userDto.getUserId() + " Total Assigned : " + userDto.getTotalAssigned() +" Daily Cap Limit: "+userDto.getDailyCap());
									if(msisdnCount.intValue() > user.getDailyCap().intValue()) {
										batchLogger.info("The daily cap limit for user: "+user.getUserId()+" will exceed for regId:"+regId+" with msidn count:"+totalAssigned);
										batchLogger.info("Moving to the next active user for this order.");
										//schAssignQueue.poll();
										userDto = user;
									}
									else {
										batchLogger.info("Order will be assigned to user: "+user.getUserId());
										userDto = user;
										break;
									}
								}

							}
							if(schAssignQueue.isEmpty()) {
								batchLogger.info("Order with regId: "+regId+" cannot be assgned to any active user due to daily limit exceeding.");
//								schAssignQueue = schAssignQueueTemp;
								return false;
							}

						}
						
						
						transDto.setBpuApprovedBy(userDto.getUserId());
						transDto.setTempApproverCode(userDto.getUserId());
						transDto.setTempDateApproved(new Date((new java.util.Date()).getTime()));
						transDto.setRegDateApproved(new Date((new java.util.Date()).getTime()));

						transDto.setRegId(regDto.getRegId());
						transDto.setEasMasterRegId(regDto.getEasMasterRegId());
						idDto.setUserId(userDto.getUserId());
						idDto.setIdentity(identity);
						idDto.setWeekNo(currWeek);
						idDto.setSch_category(userDto.getSchCategory());
						//						loadIdentityList.add(idDto);
						tempDto = new SchAssignment();
						tempDto.setUserId(userDto.getUserId());
						tempDto.setDateAssigned(userDto.getDateAssigned());
						tempDto.setTotalAssigned(msisdnCount);
						tempDto.setDailyCap(userDto.getDailyCap());
						tempDto.setAvailability(userDto.getAvailability());
						tempDto.setShift(userDto.getShift());
						tempDto.setProductExcluded(userDto.getProductExcluded());
						tempDto.setComponentInclusion(userDto.getComponentInclusion());
						tempDto.setSchCategory(userDto.getSchCategory());
						tempDto.setDateModified(userDto.getDateModified());
						if(msisdnCount.intValue() == userDto.getDailyCap().intValue())
							tempDto.setWorkStatus(WORK_STATUS_PAUSE);
						else
							tempDto.setWorkStatus(WORK_STATUS_ACTIVE);
						schAssignQueue.poll();
//						if(schAssignQueueTemp!=null && !schAssignQueueTemp.isEmpty())
//							schAssignQueue.addAll(schAssignQueueTemp);
						assigned =true;
						if(msisdnCount.intValue() != userDto.getDailyCap().intValue())
							schAssignQueue.add(tempDto);
						
						if(!dailyLimitCheck || !productExclusionCheck){
							while(!schAssignQueueTemp.isEmpty()){
								if(!schAssignQueueTemp.peek().getUserId().equalsIgnoreCase(userDto.getUserId())){
									schAssignQueueTemp.poll();
								}else{
									break;
								}
							}
							schAssignQueue = new LinkedList<SchAssignment>();
							if(schAssignQueueTemp != null && !schAssignQueueTemp.isEmpty()){
								schAssignQueue.addAll(schAssignQueueTemp);
							}
						}else{
							break;
						}
					}
					else
					{
						batchLogger.info("---user dto is null---");
						return false;
					}
				}
				if(assigned) {
					boolean isSuccessful = scheduleServiceHandler.insertSchRegistrationAssign(transDto, idDto,tempDto, ID_NOT_EXIST,easInd);
					if (isSuccessful)
					{
						batchLogger.info("RegId : " + regDto.getRegId() + " Inserted the record successfully...");
						if(shift.equals("PM-2")|| shift.equals("PM")) {
							schAssignQueuePM = schAssignQueue;
							userIdsPM = userIds;
						}else {
							schAssignQueueAM = schAssignQueue;
							userIdsAM = userIds;
						}
						return true;
					}
					else
					{
						batchLogger.info("RegId : " + regDto.getRegId() + " Record insertion failed...");
						return false;
					}
				}else{
					batchLogger.info("Assignment not successful");
					return false;
				}

			}
			else {
				batchLogger.info("RegId : " + regId + " MSISDN count : 0");
				return false;
			}
		}catch(Exception e) {
			batchLogger.info("Exception occured in schedulingProcess: " + e.getMessage());
			return false;
		}
	}
	private void clearingSchIdentifier(Integer currWeek) {

		batchLogger.info("In main Current week : " + currWeek);
		Calendar day=Calendar.getInstance();
		Integer sch_week = scheduleServiceHandler.loadSchWeek();
		if((day.get(Calendar.DAY_OF_WEEK)==2)&&(sch_week!=null && sch_week.intValue()!=0 && sch_week.intValue()!=currWeek)) {
			batchLogger.info("Deleting SCH_IDENTIFIER as it is first day of week ");
			scheduleServiceHandler.deletePrevWeek();
			batchLogger.info("Deleted SCH_IDENTIFIER as it is first day of week ");
		}

	}
	private boolean componentIncluded(String componentInclusion,Integer groupId, List<TransVas> transVasList){
		
		if(componentInclusion != null && !componentInclusion.isEmpty()){
			boolean businessPostpaidOnly = false;
			boolean businessPostpaidFibre = false;
			boolean smeGrant = false;
			
			if(componentInclusion.equalsIgnoreCase("1,3") || componentInclusion.equalsIgnoreCase("3,1")){
				if(groupId != null && (groupId == 13 || groupId ==15)){
					businessPostpaidOnly = true;
				}
				if(businessPostpaidOnly){
					for(TransVas transVas:transVasList){
						if(transVas.getPackageId() == 41772 && transVas.getComponentId() == 48856){
							businessPostpaidOnly = false;
							break;
						}
					}
				}
				return businessPostpaidOnly && checkSME(transVasList);
			}
			else if(componentInclusion.equalsIgnoreCase("2,3") || componentInclusion.equalsIgnoreCase("3,2")){
				if(groupId != null && (groupId == 13 || groupId ==15)){
					businessPostpaidFibre = true;
				}
				if(businessPostpaidFibre){
					for(TransVas transVas:transVasList){
						if(transVas.getPackageId() == 41772 && transVas.getComponentId() == 48856){
							businessPostpaidFibre = true;
							break;
						}else{
							businessPostpaidFibre = false;
						}
					}
				}
				return businessPostpaidFibre && checkSME(transVasList);
			}
			else if(componentInclusion.contains("1") && componentInclusion.contains("2") && componentInclusion.contains("3")){
				if(groupId != null && !(groupId == 13 || groupId ==15)){
					return false;
				}
				return checkSME(transVasList);
			}
			else if(componentInclusion.equalsIgnoreCase("1")){
				if(groupId != null && (groupId == 13 || groupId ==15)){
					businessPostpaidOnly = true;
				}
				if(businessPostpaidOnly){
					for(TransVas transVas:transVasList){
						if(transVas.getPackageId() == 41772 && transVas.getComponentId() == 48856){
							businessPostpaidOnly = false;
							break;
						}
					}
				}
				return businessPostpaidOnly;
			}
			else if(componentInclusion.equalsIgnoreCase("2")){
				if(groupId != null && (groupId == 13 || groupId ==15)){
					businessPostpaidFibre = true;
				}
				if(businessPostpaidFibre){
					for(TransVas transVas:transVasList){
						if(transVas.getPackageId() == 41772 && transVas.getComponentId() == 48856){
							businessPostpaidFibre = true;
							break;
						}else{
							businessPostpaidFibre = false;
						}
					}
				}
				return businessPostpaidFibre;
			}
			else if(componentInclusion.equalsIgnoreCase("3")){
				for(TransVas transVas:transVasList){
					String vas="";
					System.out.println(vas+transVas+" , ");
					if(transVas.getPackageId() == 42031 && transVas.getComponentId() == 49372){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 1711 && transVas.getComponentId() == 9855){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60333){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60333){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 20483 && transVas.getComponentId() == 23377){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 20483 && transVas.getComponentId() == 23378){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 42033 && transVas.getComponentId() == 49376){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 42033 && transVas.getComponentId() == 49377){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60335){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60336){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 20485 && transVas.getComponentId() == 23384){
						smeGrant = true;
						break;
					}else if (transVas.getPackageId() == 20485 && transVas.getComponentId() == 23385){
						smeGrant = true;
						break;
					}
				}
				return smeGrant;
			}
//			if(businessPostpaidOnly || businessPostpaidFibre || smeGrant){
//				return true;
//			}
			return false;
		}else{
			batchLogger.info("No components are to be included.");
			return true;
		}
	}
	private List<SchAssignment> orderingUserlist(List<SchAssignment> loadUsersList){
		
		List<SchAssignment> templist = new ArrayList<SchAssignment>();
		if(loadUsersList != null){
			for(SchAssignment schAssignment:loadUsersList){
				if(schAssignment.getComponentInclusion() != null && schAssignment.getComponentInclusion().contains("1") 
					&& schAssignment.getComponentInclusion().contains("2") && schAssignment.getComponentInclusion().contains("3")){
					templist.add(schAssignment);
				}
			}
			for(SchAssignment schAssignment:loadUsersList){
				if(schAssignment.getComponentInclusion() != null && schAssignment.getComponentInclusion().contains("1") 
					&& schAssignment.getComponentInclusion().contains("3")){
					templist.add(schAssignment);
				}
			}
			for(SchAssignment schAssignment:loadUsersList){
				if(schAssignment.getComponentInclusion() != null && schAssignment.getComponentInclusion().contains("2") 
					&& schAssignment.getComponentInclusion().contains("3")){
					templist.add(schAssignment);
				}
			}
			for(SchAssignment schAssignment:loadUsersList){
				if(schAssignment.getComponentInclusion() != null && schAssignment.getComponentInclusion().contains("1") 
					&& schAssignment.getComponentInclusion().contains("2")){
					templist.add(schAssignment);
				}
			}
			for(SchAssignment schAssignment:loadUsersList){
				if(schAssignment.getComponentInclusion() != null && schAssignment.getComponentInclusion().trim().length()==1){
					templist.add(schAssignment);
				}
			}
			for(SchAssignment schAssignment:loadUsersList){
				if(schAssignment.getComponentInclusion() == null){
					templist.add(schAssignment);
				}
			}
		}
		return templist;
	}
	
	private void orderingQueue(List<SchAssignment> loadUsersList,String sch_cat,String category){
		
		if (loadUsersList != null && !loadUsersList.isEmpty())
		{
			schAssignQueueAM = new LinkedList<SchAssignment>();
			schAssignQueuePM = new LinkedList<SchAssignment>();
			userIdsAM = new ArrayList<String>();
			userIdsPM = new ArrayList<String>();
			Iterator<SchAssignment> userIterator = loadUsersList.iterator();
			while (userIterator.hasNext())
			{
				SchAssignment sdto = (SchAssignment) userIterator.next();
				if(sch_cat.equals("BIP")) {
					//Removing users for non-BIP only
					String prodCat[]=sdto.getSchCategory().split(",");
					if(sdto.getSchCategory().contains("non-BIP") && prodCat.length==1) {
						userIterator.remove();
						continue;
					}
				}

				if(sdto.getShift().equals("AM")) {
					schAssignQueueAM.add(sdto);
					userIdsAM.add(sdto.getUserId());
				}
				else if (sdto.getShift().equals("PM")) {
					schAssignQueuePM.add(sdto);
					userIdsPM.add(sdto.getUserId());
					batchLogger.info("Before User : " + sdto.getUserId() + " total assigned : " + sdto.getTotalAssigned() + " work status : " + sdto.getWorkStatus() + " shift : " + sdto.getShift());
				}
			}
		}
		else {
			batchLogger.info("No active users for :"+category);
		}
	}
	
	private List<PortalRegistrationMaster2> getEasMasterRegList(List<PortalRegistrationMaster2> regList){
		Map<Integer,PortalRegistrationMaster2> easMasterMap = new HashMap<Integer,PortalRegistrationMaster2>();
		
		for(PortalRegistrationMaster2 temp:regList){
			if(temp.getEasMasterRegId() != null){
				if(easMasterMap.get(temp.getEasMasterRegId()) == null){
					easMasterRegList.add(temp);
					easMasterMap.put(temp.getEasMasterRegId(), temp);
				}
			}
		}
		return easMasterRegList;
	}
	
	private boolean checkSME(List<TransVas> transVasList){
		boolean smeGrant = false;
		for(TransVas transVas:transVasList){
			String vas="";
			System.out.println(vas+transVas+" , ");
			if(transVas.getPackageId() == 42031 && transVas.getComponentId() == 49372){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 1711 && transVas.getComponentId() == 9855){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60333){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60333){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 20483 && transVas.getComponentId() == 23377){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 20483 && transVas.getComponentId() == 23378){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 42033 && transVas.getComponentId() == 49376){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 42033 && transVas.getComponentId() == 49377){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60335){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 6186 && transVas.getComponentId() == 60336){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 20485 && transVas.getComponentId() == 23384){
				smeGrant = true;
				break;
			}else if (transVas.getPackageId() == 20485 && transVas.getComponentId() == 23385){
				smeGrant = true;
				break;
			}
		}
		return smeGrant;
	}
}
