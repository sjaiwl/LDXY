package com.app.function;

public class Dynamic {

	private String suffer;
	private String executor;
	private String executor_id;
	private String executorpictureurl;
	private Integer operate_type;
	private String comment;
	private Integer activity_id;
	private String updated_at;

	public String getSuffer() {
		return suffer;
	}

	public void setSuffer(String suffer) {
		this.suffer = suffer;
	}

	public String getExecutor_id() {
		return executor_id;
	}

	public void setExecutor_id(String executor_id) {
		this.executor_id = executor_id;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public String getWhen() {
		String when = Configuration.getLocalTimeFromUTC(updated_at);
		return when;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getExecutorpictureurl() {
		return executorpictureurl;
	}

	public void setExecutorpictureurl(String executorpictureurl) {
		this.executorpictureurl = executorpictureurl;
	}

	public Integer getOperate_type() {
		return operate_type;
	}

	public void setOperate_type(Integer operate_type) {
		this.operate_type = operate_type;
	}
}
