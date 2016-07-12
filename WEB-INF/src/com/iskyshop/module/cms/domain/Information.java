package com.iskyshop.module.cms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;
import com.iskyshop.manage.admin.tools.HtmlFilterTools;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "information")
public class Information extends IdEntity {
	private int sequence;// 序号，正常按时间排序，序号用于置顶
	private String title;// 标题
	@Column(columnDefinition = "LongText")
	private String article;// 内容
	private Long cover;// 封面图片id，可有可无
	private String synopsis;// 梗概
	private Long author_id;// 发布人id
	private String author;// 作者
	private String infoSource;//来源
	private Long store_id;// 所在店铺id
	private String store;// 店铺
	private int type;// 0平台，1为商家
	private int status;// 审核状态 0创建，10提交审核，20审核通过前台显示，-10审核失败
	@Column(columnDefinition = "LongText")
	private String failreason;// 审核失败原因
	private long classid;// 分类id
	@Column(columnDefinition = "int default 0")
	private int clicktimes;// 点击次数
	@Column(columnDefinition = "LongText")
	private String infoUserData;//会员选择心情数据，json处理，格式[{"user_id":"hehe","user_id":"kaixin"}]
	@Column(columnDefinition = "LongText")
	private String infoIconData;//资讯图标数据，json处理，格式[{"hehe":8,"kaixin":7}]
	@Column(columnDefinition = "int default 0")
	private int recommend;//0为不推荐，1为推荐到资讯首页，且只有一个
	
	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public Information(Long id, String title) {
		this.setId(id);
		this.title = title;
	}

	public Information(Long id, String title, Long cover) {
		this.setId(id);
		this.title = title;
		this.cover = cover;
	}

	public Information(Long id, String title, String synopsis) {
		this.setId(id);
		this.title = title;
		this.synopsis = synopsis;
	}

	
	
	public String getInfoUserData() {
		return infoUserData;
	}

	public void setInfoUserData(String infoUserData) {
		this.infoUserData = infoUserData;
	}

	public String getInfoIconData() {
		return infoIconData;
	}

	public void setInfoIconData(String infoIconData) {
		this.infoIconData = infoIconData;
	}

	public String getInfoSource() {
		return infoSource;
	}

	public void setInfoSource(String infoSource) {
		this.infoSource = infoSource;
	}

	public int getClicktimes() {
		return clicktimes;
	}

	public void setClicktimes(int clicktimes) {
		this.clicktimes = clicktimes;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Long getCover() {
		return cover;
	}

	public void setCover(Long cover) {
		this.cover = cover;
	}

	public Information(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public Information() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getClassid() {
		return classid;
	}

	public void setClassid(long classid) {
		this.classid = classid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public Long getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(Long author_id) {
		this.author_id = author_id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		title = HtmlFilterTools.delAllTag(title);
		this.title = title;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		article = HtmlFilterTools.delScriptTag(article);
		this.article = article;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFailreason() {
		return failreason;
	}

	public void setFailreason(String failreason) {
		this.failreason = failreason;
	}

}