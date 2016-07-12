package com.iskyshop.foundation.service;

import com.iskyshop.foundation.domain.SysConfig;

public interface ISysConfigService {
	/**
	 * 
	 * @param shopConfig
	 * @return
	 */
	boolean save(SysConfig shopConfig);

	/**
	 * 
	 * @param shopConfig
	 * @return
	 */
	boolean delete(SysConfig shopConfig);

	/**
	 * 
	 * @param shopConfig
	 * @return
	 */
	boolean update(SysConfig shopConfig);

	/**
	 * 
	 * @return
	 */
	SysConfig getSysConfig();

	/**
	 * 系统零时定时任务控制器，每天00:00:01秒执行
	 * 
	 * @return
	 */
	void runTimerByDay();

	/**
	 * 系统半小时定时任务控制器，每半小时运行一次
	 * 
	 * @return
	 * @throws Exception 
	 */
	void runTimerByHalfhour() throws Exception;

}
