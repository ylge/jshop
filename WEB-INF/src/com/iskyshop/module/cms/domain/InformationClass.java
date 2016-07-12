package com.iskyshop.module.cms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "information_class")
public class InformationClass extends IdEntity {
	@Column(columnDefinition = "int default 0")
	private int ic_sequence;// 序号，正常按时间排序
	private Long ic_pid;// 上级分类Id
	private String ic_name;// 标题

	public int getIc_sequence() {
		return ic_sequence;
	}

	public void setIc_sequence(int ic_sequence) {
		this.ic_sequence = ic_sequence;
	}

	public Long getIc_pid() {
		return ic_pid;
	}

	public void setIc_pid(Long ic_pid) {
		this.ic_pid = ic_pid;
	}

	public String getIc_name() {
		return ic_name;
	}

	public void setIc_name(String ic_name) {
		this.ic_name = ic_name;
	}

}