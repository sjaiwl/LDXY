package com.app.function;

import java.io.Serializable;

public class DynamicMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer activity_id;
	private Integer dynamic_id;
	private String acpictureurls;
	private String pictureurl;
	private String nickname;
	private String actheme;
	private String comment;
	private String executor_id;
	private String updated_at;
	private Integer operate_type;

	public String getActheme() {
		return actheme;
	}

	public void setActheme(String actheme) {
		this.actheme = actheme;
	}

	public String getPictureurl() {
		return pictureurl;
	}

	public void setPictureurl(String pictureurl) {
		this.pictureurl = pictureurl;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUpdated_at() {
		String when;
		when = Configuration.getLocalTimeFromUTC(updated_at);
		return when;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public Integer getOperate_type() {
		return operate_type;
	}

	public void setOperate_type(Integer operate_type) {
		this.operate_type = operate_type;
	}

	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public Integer getDynamic_id() {
		return dynamic_id;
	}

	public void setDynamic_id(Integer dynamic_id) {
		this.dynamic_id = dynamic_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getExecutor_id() {
		return executor_id;
	}

	public void setExecutor_id(String executor_id) {
		this.executor_id = executor_id;
	}

	public String getAcpictureurls() {
		return acpictureurls;
	}

	public void setAcpictureurls(String acpictureurls) {
		this.acpictureurls = acpictureurls;
	}
}
