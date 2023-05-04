package com.maxis.autoAssignment.pojo;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter @Setter
@Table(name="SCH_ASSIGNMENT")
@Data
public class SchAssignment {
	@Id
	@Column(name="USER_ID")
	private String userId;
	
	@Column(name="DATE_ASSIGNED")
	private Date dateAssigned;
	
	@Column(name="TOTAL_ASSIGNED")
	private Integer totalAssigned;
	
	@Column(name="WORK_STATUS")
	private String workStatus;
	
	@Column(name="DATE_MODIFIED")
	private Date dateModified;
	
	@Column(name="SCH_CATEGORY")
	private String schCategory;
	
	@Column(name="SHIFT")
	private String shift;
	
	@Column(name="DAILY_CAP")
	private Integer dailyCap;
	
	@Column(name="PROD_EXCLUSION")
	private String productExcluded;
	
	@Column(name="AVAILABLITY")
	private String availability;
	
	@Column(name="COMPONENT_INCLUSION")
	private String componentInclusion;
}
