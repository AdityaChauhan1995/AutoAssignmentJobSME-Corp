package com.maxis.autoAssignment.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter @Setter
@Table(name="PORTAL_CUST_INFO")
@Data
public class PortalCustInfo {
@Id
@Column(name="CUST_ID")
private Integer custId;

@Column(name="CUST_BRN_NO")
private String custBrnNo;

@Column(name="NEW_IC")
private String newIc;
}
