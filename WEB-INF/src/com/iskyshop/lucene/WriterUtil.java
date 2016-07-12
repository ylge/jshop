package com.iskyshop.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 
 * <p>
 * Title: WriterUtil.java
 * </p>
 * 
 * <p>
 * Description:系统Lucene
 * indexWriter实例管理器，第一次初始化时候进行一次实例化处理，此后一直单例使用，除非gc回收WriteUtil
 * ，再次重新初始化WriteUtil，各个IndexWriter再次初始化
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
 * @author erikzhang
 * 
 * @date 2015-1-9
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
public class WriterUtil {
	private static IndexWriter indexWriter;
	private static IndexWriter indexWriter_group;
	private static IndexWriter indexWriter_life;
	private static final String GOODS = System.getProperty("iskyshopb2b2c.root")
			+ File.separator + "luence" + File.separator + "goods";
	private static final String GROUP = System.getProperty("iskyshopb2b2c.root")
			+ File.separator + "luence" + File.separator + "groupgoods";
	private static final String LIFE = System.getProperty("iskyshopb2b2c.root")
			+ File.separator + "luence" + File.separator + "lifegoods";
	static {
		try {
			LogMergePolicy mergePolicy = new LogDocMergePolicy();
			// 索引基本配置
			// 设置segment添加文档(Document)时的合并频率
			// 值较小,建立索引的速度就较慢
			// 值较大,建立索引的速度就较快,>10适合批量建立索引
			mergePolicy.setMergeFactor(30);
			IndexWriterConfig iwc1 = new IndexWriterConfig(
					Version.LUCENE_4_10_0, LuceneUtil.getAnalyzer());
			iwc1.setOpenMode(OpenMode.CREATE_OR_APPEND);
			// 设置segment最大合并文档(Document)数
			// 值较小有利于追加索引的速度
			// 值较大,适合批量建立索引和更快的搜索
			mergePolicy.setMaxMergeDocs(5000);
			iwc1.setMaxBufferedDocs(10000);
			iwc1.setMergePolicy(mergePolicy);
			iwc1.setRAMBufferSizeMB(64);
			IndexWriterConfig iwc2 = new IndexWriterConfig(
					Version.LUCENE_4_10_0, LuceneUtil.getAnalyzer());
			iwc2.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwc2.setMaxBufferedDocs(5000);
			iwc2.setMergePolicy(mergePolicy);
			iwc2.setRAMBufferSizeMB(64);

			IndexWriterConfig iwc3 = new IndexWriterConfig(
					Version.LUCENE_4_10_0, LuceneUtil.getAnalyzer());
			iwc3.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwc3.setMaxBufferedDocs(5000);
			iwc3.setMergePolicy(mergePolicy);
			iwc3.setRAMBufferSizeMB(64);
			indexWriter = new IndexWriter(FSDirectory.open(new File(GOODS)),
					iwc1);
			indexWriter_group = new IndexWriter(
					FSDirectory.open(new File(GROUP)), iwc2);
			indexWriter_life = new IndexWriter(
					FSDirectory.open(new File(LIFE)), iwc3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 在线程结束时，自动关闭IndexWriter */
	public static IndexWriter getIndexWriter(String path) {
		System.out.println(path);
		if (path.equals(GOODS)) {
			return indexWriter;
		} else if (path.equals(GROUP)) {
			return indexWriter_group;
		} else if (path.equals(LIFE)) {
			return indexWriter_life;
		} else {
			return indexWriter;
		}
	}

	public static IndexWriter getIndexWriterGroup() {
		return indexWriter_group;
	}

	public static IndexWriter getIndexWriterLife() {
		return indexWriter_life;
	}

	/**
	 * 关闭IndexWriter
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void closeIndexWriter() throws Exception {
		if (indexWriter != null) {
			indexWriter.commit();
			indexWriter.close();
		}
		if (indexWriter_group != null) {
			indexWriter_group.commit();
			indexWriter_group.close();
		}
		if (indexWriter_life != null) {
			indexWriter_life.commit();
			indexWriter_life.close();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		this.closeIndexWriter();
	}
	
	
}
