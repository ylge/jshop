package com.iskyshop.manage.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: JobManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统定时任务控制器，每天00:00:01秒执行
 * B2B2C2015版开始，系统定时器方法移到configService中，执行方法分别为runTimerByDay，runTimerByHalfhour
 * 移到configService中能够有效保持所有数据一致性。（hezeng）
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang，hezeng
 * 
 * @date 2014-5-13
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component(value = "shop_job")
public class JobManageAction {
	@Autowired
	private ISysConfigService configService;

	public void execute() {
		// TODO Auto-generated method stub
		this.configService.runTimerByDay();
	}

}
