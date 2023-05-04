package com.maxis.autoAssignment.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PORTAL_TRANS_VAS")
@Data
public class TransVas {
	@Id
	@Column(name = "VAS_ID")
	private Integer vasId;
	
	@Column(name = "REG_ID")
	private Integer regId;

	@Column(name = "TRANS_MSID")
	private Integer transMsid;
	
	@Column(name = "RATEPLAN_ID")
	private Integer rateplanId;

	@Column(name = "PACKAGE_ID")
	private Integer packageId;

	@Column(name = "COMPONENT_ID")
	private Integer componentId;

	

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "DATE_MODIFIED")
	private Date dateModified;
	
	@Column(name = "DATE_CREATED")
	private Date dateCreated;

	@Column(name = "CREATED_BY")
	private String createdBy;

}
