package com.netease.nim.demo.common.infra;

public interface TaskObserver {
	/**
	 * on task result
	 * @param task
	 * @param results
	 */
	public void onTaskResult(com.netease.nim.demo.common.infra.Task task, Object[] results);

	/**
	 * on task progress
	 * @param task
	 * @param params
	 */
	public void onTaskProgress(com.netease.nim.demo.common.infra.Task task, Object[] params);
}
