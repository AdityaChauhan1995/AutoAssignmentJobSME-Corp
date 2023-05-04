package com.maxis.autoAssignment.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PORTAL_TRANS_MSISDN")
@Data
public class PortalTransMsisdn2 {
	@Id
	@Column(name = "TRANS_MSID")
	private Integer transMsisdn;

	@Column(name = "REG_ID")
	private Integer regId;

	@ManyToOne
	@JoinColumn(name = "REG_ID", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private PortalRegistrationMaster2 portalRegMaster;
}
