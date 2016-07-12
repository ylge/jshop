package com.iskyshop.manage.admin.tools;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: GoodsTools.java
 * </p>
 * 
 * <p>
 * Description:商品管理工具
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
 * @author hezeng
 * 
 * @date 2014-12-10
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class GoodsTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 平台自营商品异步生成商品二维码
	 * 
	 * @param web_url
	 * @param goods_id
	 * @param uploadFilePath
	 * @param goods_main_id
	 */
	@Async
	public void createSelfGoodsQR(String web_url, String goods_id,
			String uploadFilePath, Long goods_main_id) {
		try {
			String destPath = System.getProperty("iskyshopb2b2c.root")
					+ uploadFilePath + File.separator + "goods_qr";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			Accessory main_img = this.accessoryService.getObjById(CommUtil
					.null2Long(goods_main_id));
			destPath = destPath + File.separator + goods_id + "_qr.jpg";
			String logoPath = "";
			if (main_img != null) {
				logoPath = System.getProperty("iskyshopb2b2c.root")
						+ main_img.getPath() + File.separator
						+ main_img.getName();
			} else {
				logoPath = System.getProperty("iskyshopb2b2c.root")
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ File.separator
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
			}
			String goods_url = web_url + "/wap/goods_" + goods_id + ".htm";
			QRCodeUtil.encode(goods_url, logoPath, destPath, true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 商家商品异步生成商品二维码
	 * 
	 * @param web_url
	 * @param second_domain
	 *            :商家二级域名
	 * @param goods_id
	 * @param uploadFilePath
	 * @param goods_main_id
	 */
	@Async
	public void createUserGoodsQR(String web_url, String second_domain,
			String goods_id, String uploadFilePath, Long goods_main_id) {
		try {
			Goods obj = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			String destPath = System.getProperty("iskyshopb2b2c.root")
					+ uploadFilePath + File.separator + "goods_qr";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			Accessory main_img = this.accessoryService.getObjById(CommUtil
					.null2Long(goods_main_id));
			destPath = destPath + File.separator + goods_id + "_qr.jpg";
			String logoPath = "";
			if (main_img != null) {
				logoPath = System.getProperty("iskyshopb2b2c.root")
						+ main_img.getPath() + File.separator
						+ main_img.getName();
			} else {
				logoPath = System.getProperty("iskyshopb2b2c.root")
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ File.separator
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
			}
			String goods_url = web_url + "/wap/goods_" + goods_id + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& !CommUtil.null2String(second_domain).equals("")) {
				goods_url = "http://"
						+ obj.getGoods_store().getStore_second_domain() + "."
						+ second_domain + "/wap/goods_" + goods_id + ".htm";
			}
			QRCodeUtil.encode(goods_url, logoPath, destPath, true);
			obj.setQr_img_path(web_url + "/" + uploadFilePath + "/"
					+ "goods_qr" + "/" + obj.getId() + "_qr.jpg");

			this.goodsService.update(obj);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
