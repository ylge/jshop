package com.iskyshop.module.sns.view.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.domain.UserShare;
import com.iskyshop.module.sns.service.ISnsAttentionService;
import com.iskyshop.module.sns.service.IUserShareService;

/**
 * 
 * <p>
 * Title: SnsTools.java
 * </p>
 * 
 * <p>
 * Description: SNS相关工具类
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
 * @author jinxinzhe
 * 
 * @date 2014-11-23
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class SnsTools {
	@Autowired
	private ISnsAttentionService snsAttentionService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IUserShareService userShareService;

	public int queryFans(String user_id) {
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		int fans = this.snsAttentionService
				.query("select obj.id from SnsAttention obj where obj.toUser.id = :user_id",
						params, -1, -1).size();
		return fans;
	}

	public int queryAtts(String user_id) {
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		int atts = this.snsAttentionService
				.query("select obj.id from SnsAttention obj where obj.fromUser.id = :user_id",
						params, -1, -1).size();
		return atts;
	}

	public int queryfavCount(String user_id) {
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		int favsCount = this.favoriteService
				.query("select obj.id from Favorite obj where obj.user_id = :user_id and obj.type=0",
						params, -1, -1).size();
		return favsCount;
	}

	public UserShare querylastUserShare(Long user_id) {
		Map params = new HashMap();
		params.put("user_id", user_id);
		List<UserShare> userShares = this.userShareService
				.query("select obj from UserShare obj where obj.user_id = :user_id order by obj.addTime desc",
						params, 0, 1);
		if (userShares.size() > 0) {
			return userShares.get(0);
		}
		return null;
	}

	public Favorite queryLastUserFav(Long user_id) {
		Map params = new HashMap();
		params.put("user_id", user_id);
		List<Favorite> favorites = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id = :user_id and obj.type=0 order by obj.addTime desc",
						params, 0, 1);
		if (favorites.size() > 0) {
			return favorites.get(0);
		}
		return null;
	}

	public boolean whetherAttention(String fromUser, String toUser) {
		boolean ret = false;
		Map params = new HashMap();
		params.put("fromUser", CommUtil.null2Long(fromUser));
		params.put("toUser", CommUtil.null2Long(toUser));
		List<SnsAttention> sns = this.snsAttentionService
				.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser and obj.toUser.id=:toUser",
						params, -1, -1);
		if (sns.size() > 0) {
			ret = true;
		}
		return ret;
	}

	public boolean whetherAttentionId(String fromUser_id, String toUser_id) {
		boolean ret = false;
		Map params = new HashMap();
		params.put("fromUser", CommUtil.null2Long(fromUser_id));
		params.put("toUser", CommUtil.null2Long(toUser_id));
		List<SnsAttention> sns = this.snsAttentionService
				.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser and obj.toUser.id=:toUser",
						params, -1, -1);
		if (sns.size() > 0) {
			ret = true;
		}
		return ret;
	}
	
	public List getDynamic_img_info(String img_info){
		List<Map> list = Json.fromJson(ArrayList.class, img_info);
		return list;
	}

}
