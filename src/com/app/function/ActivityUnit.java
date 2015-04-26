package com.app.function;

public class ActivityUnit {

	private Integer activity_id;
	private String acpictureurls;
	private String aclabel;
	private String actheme;
	private Integer user_id;
	private String nickname;
	private String pictureurl;
	private String updated_at;
	private Integer joinsums;
	private String joiners;
	private Integer commentsums;
	private String starttime;
	private String endtime;
	private String acplace;
	private String phonenumber;
	private String accontent;
	private Integer iscollected;
	private Integer isjoined;

	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public String getImg() {
		return acpictureurls;
	}

	public String getType() {
		return aclabel;
	}

	public String getTitle() {
		return actheme;
	}

	public Integer getUserId() {
		return user_id;
	}

	public String getUserName() {
		return nickname;
	}

	public String getUserImage() {
		return pictureurl;
	}

	public String getWhen() {
		String when = Configuration.getLocalTimeFromUTC(updated_at);
		return when;
	}

	public String getJoinsum() {
		String s = "已有" + Integer.toString(this.joinsums) + "参加";
		return s;
	}

	public Integer getjoinsum() {
		return this.joinsums;
	}

	public String getFreshJoinsum(int change) {
		String s = "已有" + Integer.toString(this.joinsums + change) + "参加";
		return s;
	}

	public String getCommentsum() {
		String s = "已有" + Integer.toString(this.commentsums) + "评论";
		return s;
	}

	public Integer getcommentsum() {
		return this.commentsums;
	}

	public String getFreshCommentsum(int change) {
		String s = "已有" + Integer.toString(this.commentsums + change) + "评论";
		return s;
	}

	public String getStarttime() {
		return starttime + "开始";
	}

	public String getOriginalStarttime() {
		return starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public String getLocation() {
		return acplace;
	}

	public String getPhone() {
		return phonenumber;
	}

	public String getContent() {
		return accontent;
	}

	public String getAcpictureurls() {
		return acpictureurls;
	}

	public String getAclabel() {
		return aclabel;
	}

	public String getActheme() {
		return actheme;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPictureurl() {
		return pictureurl;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public String getAcplace() {
		return acplace;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public String getAccontent() {
		return accontent;
	}

	public Integer getIscollected() {
		return iscollected;
	}

	public Integer getIsjoined() {
		return isjoined;
	}

	public void setIsjoined(Integer isjoined) {
		this.isjoined = isjoined;
	}

	public void setAcpictureurls(String acpictureurls) {
		this.acpictureurls = acpictureurls;
	}

	public void setAclabel(String aclabel) {
		this.aclabel = aclabel;
	}

	public void setActheme(String actheme) {
		this.actheme = actheme;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPictureurl(String pictureurl) {
		this.pictureurl = pictureurl;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public void setJoinsums(Integer joinsums) {
		this.joinsums = joinsums;
	}

	public void setCommentsums(Integer commentsums) {
		this.commentsums = commentsums;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public void setAcplace(String acplace) {
		this.acplace = acplace;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public void setAccontent(String accontent) {
		this.accontent = accontent;
	}

	public void setIscollected(Integer iscollected) {
		this.iscollected = iscollected;
	}

	public String getJoiners() {
		return joiners;
	}

	public void setJoiners(String joiners) {
		this.joiners = joiners;
	}
}
