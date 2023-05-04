package com.maxis.autoAssignment.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter @Setter
@Table(name="PORTAL_REF_DEALER")
@Data
public class EbdUser {
	@Id
	@Column(name="USERID")
	private String userId;

	@Column(name="DEALER_NAME")
	private String dealerName;

	@Column(name="DEALER_CODE")
	private String dealerCode;
}
