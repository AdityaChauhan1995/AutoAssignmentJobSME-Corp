package com.maxis.autoAssignment.pojo;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PORTAL_REF_EAS_REGISTRATION")
@Data
public class EasRegistration {
	@Id
	@Column(name = "EAS_MASTER_REG_ID")
	private Integer easMasterRegId;
	
	@OneToMany(mappedBy = "easRegMaster", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<PortalRegistrationMaster2> portalRegistrationMaster;
	
	@Column(name="APPROVER_CODE")
	private String approverCode;
	
	@Column(name="DATE_MODIFIED")
	private Date dateModified;
	
	@Column(name="MASTER_REG_STATUS")
	private String masterStatus;
	
	
}
