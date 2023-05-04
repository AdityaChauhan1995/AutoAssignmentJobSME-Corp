package com.maxis.autoAssignment.pojo;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PORTAL_REGISTRATION_MASTER")
@Data
public class PortalRegistrationMaster2 {
	@Id
	@Column(name = "REG_ID")
	private Integer regId;

	@Column(name = "EAS_MASTER_REG_ID")
	private Integer easMasterRegId;

	@Column(name = "BPU_APPROVED_BY")
	private String bpuApprovedBy;

	@Column(name = "REG_STATUS")
	private String regStatus;

	@Column(name = "REG_DATE_APPROVED")
	private Date regDateApproved;

	@Column(name = "ROADSHOW_ID")
	private String roadShowId;

	@Column(name = "CUST_ID")
	private String custId;

	@Column(name = "DEALER_CODE")
	private String dealerCode;

	@Column(name = "REG_TYPE")
	private String regType;

	@Column(name = "PRODUCT_GROUP_ID")
	private Integer groupId;
	
	@Column(name="APPROVER_REMARK")
	private String approverRemarks;

	@Column(name="TEMP_APPROVER_CODE")
	private String tempApproverCode;
	
	@Column(name="TEMP_DATE_APPROVED")
	private Date tempDateApproved;
	
	@Column(name="PROD_CAT_ID")
	private Integer prodCatId;
	
	@Column(name="IS_ZEROLUTION_RTF")
	private String isZerolutionRTF;
	
	@OneToMany(mappedBy = "portalRegMaster", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<PortalTransMsisdn2> transMsisdn;

	@OneToOne
	@JoinColumn(name = "CUST_ID", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private PortalCustInfo custInfo;
	
	@ManyToOne
	@JoinColumn(name = "EAS_MASTER_REG_ID", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private EasRegistration easRegMaster;
}
