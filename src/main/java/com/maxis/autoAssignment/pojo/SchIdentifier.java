package com.maxis.autoAssignment.pojo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity@Getter @Setter
@Table(name="SCH_IDENTIFIER")
@Data
public class SchIdentifier {

	@Id
	@Column(name="USER_ID")
	private String userId;
	
	@Column(name="WEEK_ASSIGNED")
	private Integer weekNo;
	
	@Column(name="IDENTITY")
	private String identity;
	
	@Column(name="SCH_CATEGORY")
	private String sch_category;
}
