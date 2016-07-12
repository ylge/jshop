package com.iskyshop.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.lang.util.ArraySet;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.lucene.parse.ShopQueryParser;
import com.iskyshop.lucene.pool.LuceneThreadPool;

/**
 * 
 * <p>
 * Title: LuceneUtil.java
 * </p>
 * 
 * <p>
 * Description: lucene搜索工具类,用来写入索引，搜索数据
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
 * @date 2014-6-5
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class LuceneUtil {
	private static File index_file = null;// 索引文件
	private static Analyzer analyzer = null;// 搜索分词器
	private static LuceneUtil lucence = new LuceneUtil();// 搜索工具类单例
	private static QueryParser parser;// 查询解析器
	private static String index_path;// 索引路径
	private static int gc_size;// 商品分类大小
	private int textmaxlength = 100;// 截取字符串长度，该长度类关键词高亮显示
	private static String prefixHTML = "<font color='red'>";// 高亮html前置
	private static String suffixHTML = "</font>";// 高亮html后置
	private int pageSize = 24;
	private static SysConfig sysConfig;// 判断是否开启异步写索引

	/** 初始化工具 * */
	public LuceneUtil() {
		// System.out.println("lucene初始化开始....");
		analyzer = new IKAnalyzer();
		parser = new ShopQueryParser(Version.LUCENE_4_10_0, LuceneVo.TITLE,
				analyzer);

	}

	public static Analyzer getAnalyzer() {
		return analyzer;
	}

	public static File getIndex_filer() {
		return index_file;
	}

	/** 返回一个单例 * */
	public static LuceneUtil instance() {
		return lucence;
	}

	public static void setIndex_path(String index_path) {
		LuceneUtil.index_path = index_path;
		index_file = new File(index_path);
	}

	public static void setGc_size(int gc_size) {
		LuceneUtil.gc_size = gc_size;
	}

	public static void setConfig(SysConfig config) {
		LuceneUtil.sysConfig = config;
	}

	/**
	 * 此方法为特定类型的排序
	 * 
	 * @param params
	 * @param after
	 * @return
	 */
	public LuceneResult search(String keyword, int currentPage,
			String goods_inventory, String goods_type, String goods_class,
			String goods_transfee, String goods_cod, Sort sort,
			String goods_cat, String goods_area, String gb_name) {
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		List<LuceneVo> vo_list = new ArrayList<LuceneVo>();
		IndexReader reader = null;
		int pages = 0;
		int rows = 0;
		String params = "";
		try {
			// 创建索引搜索器 且只读
			index_file = new File(index_path);
			if (!index_file.exists()) {
				return pList;
			}
			reader = IndexReader.open(FSDirectory.open(index_file));
			isearcher = new IndexSearcher(reader);
			// 在索引器中使用IKSimilarity相似度评估器
			// isearcher.setSimilarity(new IKSimilarity());
			// 处理查询筛选条件
			if (gb_name != null && !gb_name.equals("")) {// 此为品牌主页请求
				gb_name = gb_name.trim();
			} else {
				if (keyword != null && !"".equals(keyword)
						&& keyword.indexOf("title:") < 0) {
					params = "(title:" + keyword + " OR content:" + keyword
							+ ")";
				} else {
					params = "(title:*)";
				}
			}
			if (goods_inventory != null && goods_inventory.equals("0")) {
				params = params + " AND goods_inventory:[1 TO "
						+ Integer.MAX_VALUE + "]";
			}
			if (goods_type != null && !goods_type.equals("-1")) {
				params = params + " AND goods_type:" + goods_type;
			}
			if (goods_class != null && !goods_class.equals("")) {
				params = params + " AND goods_class:" + goods_class;
			}
			if (goods_transfee != null && !goods_transfee.equals("")) {
				params = params + " AND goods_transfee:" + goods_transfee;
			}
			if (goods_cod != null && !goods_cod.equals("")) {
				params = params + " AND goods_cod:" + goods_cod;
			}
			if (goods_cat != null && !goods_cat.equals("")) {
				params = params + " AND (goods_cat : " + goods_cat + ")";
			}
			if (goods_area != null && !goods_area.equals("")) {
				params = params + " AND (goods_area : " + goods_area + ")";
			}
			// System.out.println(params);
			parser.setAllowLeadingWildcard(true);
			BooleanQuery query = new BooleanQuery();
			if(params!=null&&!"".equals(params)){
				query.add(parser.parse(params), Occur.SHOULD);
			}
			if(gb_name!=null&&!gb_name.equals("")){
				Query query1 =new TermQuery(new Term("goods_brand",gb_name));
				query.add(query1, Occur.MUST);
			}
			if(keyword!=null&&!keyword.equals("")){
				Query query1 =new TermQuery(new Term("goods_brand",keyword));
				query.add(query1, Occur.SHOULD);
			}
			TopDocs topDocs = null;
			int start = (currentPage - 1) * this.pageSize;
			if (currentPage == 0) {// currentPage为零，该请求为搜索请求
				topDocs = isearcher.search(query, this.pageSize, sort);
				pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize; // 记算总页数
				rows = topDocs.totalHits;// 计算总记录数
				currentPage = 1;
				start = 0;
			} else if (currentPage != 0) {// currentPage非零，该请求为分页请求
				topDocs = isearcher.search(query, start + this.pageSize, sort);
				pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize; // 记算总页数
				rows = topDocs.totalHits;// 计算总记录数
			}
			int end = (this.pageSize + start) < topDocs.totalHits ? (this.pageSize + start)
					: topDocs.totalHits;
			for (int i = start; i < end; i++) {
				Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
				LuceneVo vo = new LuceneVo();
				// 对商品名称进行关键字高亮
				String title = doc.get(LuceneVo.TITLE);
				if (gb_name == null || gb_name.equals("")) {// 非品牌主页请求，对商品名称进行关键字高亮显示
					if (!"(title:*)".equals(keyword)
							&& !"(title:*)".equals(pages)) {
						SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
								prefixHTML, suffixHTML);
						Highlighter highlighter = new Highlighter(
								simpleHTMLFormatter, new QueryScorer(query));
						highlighter.setTextFragmenter(new SimpleFragmenter(
								textmaxlength));
						title = highlighter.getBestFragment(analyzer, keyword,
								doc.get(LuceneVo.TITLE));
					}
				}
				// 商品id，名称，图片
				vo.setVo_id(CommUtil.null2Long(doc.get(LuceneVo.ID)));
				if (title == null) {
					vo.setVo_title(doc.get(LuceneVo.TITLE));
				} else {
					vo.setVo_title(title);
				}
				vo.setVo_main_photo_url(doc.get(LuceneVo.MAIN_PHOTO_URL));
				vo.setVo_photos_url(doc.get(LuceneVo.PHOTOS_URL));
				// 价格，销量，评论，类型
				vo.setVo_store_price(CommUtil.null2Double(doc
						.get(LuceneVo.STORE_PRICE)));
				vo.setVo_goods_salenum(CommUtil.null2Int(doc
						.get(LuceneVo.GOODS_SALENUM)));
				vo.setVo_goods_evas(CommUtil.null2Int(doc
						.get(LuceneVo.GOODS_EVAS)));
				vo.setVo_goods_type(CommUtil.null2Int(doc
						.get(LuceneVo.GOODS_TYPE)));
				vo.setVo_whether_active(CommUtil.null2Int(doc
						.get(LuceneVo.WHETHER_ACTIVE)));
				vo.setVo_f_sale_type(CommUtil.null2Int(doc
						.get(LuceneVo.F_SALE_TYPE)));
				vo_list.add(vo);
			}
			pList.setPages(pages);
			pList.setRows(rows);
			pList.setCurrentPage(currentPage);
			pList.setVo_list(vo_list);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isearcher != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return pList;
	}

	/**
	 * 此方法为无特定类型的默认排序，
	 * 
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	public LuceneResult search(String keyword, int currentPage,
			String goods_inventory, String goods_type, String goods_class,
			String goods_transfee, String goods_cod, String goods_cat,
			String goods_area, String gb_name) {
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		List<LuceneVo> vo_list = new ArrayList<LuceneVo>();
		IndexReader reader = null;
		int pages = 0;
		int rows = 0;
		String params = "";
		try {
			// 创建索引搜索器 且只读
			index_file = new File(index_path);
			if (!index_file.exists()) {
				return pList;
			}
			reader = IndexReader.open(FSDirectory.open(index_file));
			isearcher = new IndexSearcher(reader);
			// 在索引器中使用IKSimilarity相似度评估器 
			// isearcher.setSimilarity(new IKSimilarity());
			// 处理查询筛选条件
			if (gb_name != null && !gb_name.equals("")) {// 此为品牌主页请求
				gb_name = gb_name.trim();
				//params = "(goods_brand:" + gb_name + ")";
			} else {
				if (keyword != null && !"".equals(keyword)
						&& keyword.indexOf("title:") < 0) {
					params = "(title:" + keyword + " OR content:" + keyword
							+ ")";
				} else {
					params = "(title:*)";
				}
			}
			if (goods_inventory != null && goods_inventory.equals("0")) {
				params = params + " AND goods_inventory:[1 TO "
						+ Integer.MAX_VALUE + "]";
			}
			if (goods_type != null && !goods_type.equals("-1")) {
				params = params + " AND goods_type:" + goods_type;
			}
			if (goods_class != null && !goods_class.equals("")) {
				params = params + " AND goods_class:" + goods_class;
			}
			if (goods_transfee != null && !goods_transfee.equals("")) {
				params = params + " AND goods_transfee:" + goods_transfee;
			}
			if (goods_cod != null && !goods_cod.equals("")) {
				params = params + " AND goods_cod:" + goods_cod;
			}
			if (goods_cat != null && !goods_cat.equals("")) {
				params = params + " AND (goods_cat : " + goods_cat + ")";
			}
			if (goods_area != null && !goods_area.equals("")) {
				params = params + " AND (goods_area : " + goods_area + ")";
			}
			parser.setAllowLeadingWildcard(true);
//			Query query = parser.parse(params);
			BooleanQuery query = new BooleanQuery();
			parser.setAllowLeadingWildcard(true);
			if(params!=null&&!"".equals(params)){
				query.add(parser.parse(params), Occur.SHOULD);
			}
			if(gb_name!=null&&!gb_name.equals("")){
				Query query1 =new TermQuery(new Term("goods_brand",gb_name));
				query.add(query1, Occur.MUST);
			}
			if(keyword!=null&&!keyword.equals("")){
				Query query1 =new TermQuery(new Term("goods_brand",keyword));
				query.add(query1, Occur.SHOULD);
			}
			TopDocs topDocs = null;
			if (currentPage == 0) {// currentPage为零，该请求为搜索请求
				topDocs = isearcher.search(query, this.pageSize);
				pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize; // 记算总页数
				rows = topDocs.totalHits;// 计算总记录数
				currentPage = 1;
			} else if (currentPage != 0) {// currentPage非零，该请求为分页请求
				ScoreDoc scoreDoc = getLastScoreDoc(currentPage, this.pageSize,
						query, isearcher);
				topDocs = isearcher.searchAfter(scoreDoc, query, this.pageSize);
				pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize; // 记算总页数
				rows = topDocs.totalHits;// 计算总记录数
			}
			for (ScoreDoc sd : topDocs.scoreDocs) {
				Document doc = isearcher.doc(sd.doc);
				LuceneVo vo = new LuceneVo();
				String title = doc.get(LuceneVo.TITLE);
				if (gb_name == null || gb_name.equals("")) {// 非品牌主页请求，对商品名称进行关键字高亮显示
					if (!"(title:*)".equals(keyword)
							&& !"(title:*)".equals(pages)) {
						SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
								prefixHTML, suffixHTML);
						Highlighter highlighter = new Highlighter(
								simpleHTMLFormatter, new QueryScorer(query));
						highlighter.setTextFragmenter(new SimpleFragmenter(
								textmaxlength));
						title = highlighter.getBestFragment(analyzer, keyword,
								doc.get(LuceneVo.TITLE));
					}
				}
				// 商品id，名称，图片
				vo.setVo_id(CommUtil.null2Long(doc.get(LuceneVo.ID)));
				if (title == null) {
					vo.setVo_title(doc.get(LuceneVo.TITLE));
				} else {
					vo.setVo_title(title);
				}
				vo.setVo_main_photo_url(doc.get(LuceneVo.MAIN_PHOTO_URL));
				vo.setVo_photos_url(doc.get(LuceneVo.PHOTOS_URL));
				// 价格，销量，评论，类型
				vo.setVo_store_price(CommUtil.null2Double(doc
						.get(LuceneVo.STORE_PRICE)));
				vo.setVo_goods_salenum(CommUtil.null2Int(doc
						.get(LuceneVo.GOODS_SALENUM)));
				vo.setVo_goods_evas(CommUtil.null2Int(doc
						.get(LuceneVo.GOODS_EVAS)));
				vo.setVo_goods_type(CommUtil.null2Int(doc
						.get(LuceneVo.GOODS_TYPE)));
				vo.setVo_whether_active(CommUtil.null2Int(doc
						.get(LuceneVo.WHETHER_ACTIVE)));
				vo.setVo_f_sale_type(CommUtil.null2Int(doc
						.get(LuceneVo.F_SALE_TYPE)));
				vo_list.add(vo);
			}
			pList.setPages(pages);
			pList.setRows(rows);
			pList.setCurrentPage(currentPage);
			pList.setVo_list(vo_list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isearcher != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return pList;
	}

	/**
	 * 根据页码和分页大小获取上一次的最后一个ScoreDoc
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @param query
	 * @param searcher
	 * @return
	 * @throws IOException
	 */
	private ScoreDoc getLastScoreDoc(int currentPage, int pageSize,
			Query query, IndexSearcher searcher) throws IOException {
		if (currentPage == 1)
			return null;// 如果是第一页就返回空
		int num = pageSize * (currentPage - 1);// 获取上一页的数量
		TopDocs tds = searcher.search(query, num);
		return tds.scoreDocs[num - 1];
	}

	/**
	 * 对关键字命中的商品进行分类提取
	 * 
	 * @param keyword
	 * @param rows
	 * @return
	 */
	public Set<String> LoadData_goods_class(String keyword) {
		IndexSearcher searcher = null;
		Set<String> list = new ArraySet<String>();
		IndexReader reader = null;
		try {
			index_file = new File(index_path);
			reader = IndexReader.open(FSDirectory.open(index_file));
			searcher = new IndexSearcher(reader);
			if (keyword != null && !"".equals(keyword)
					&& keyword.indexOf("title:") < 0) {
				keyword = "(title:" + keyword + " OR content:" + keyword
						+ " OR goods_brand: " + keyword + ")";
			}
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(keyword);
			TopDocs topDocs = null;
			Filter filter = new DuplicateFilter(LuceneVo.GOODS_CLASS);
			topDocs = searcher.search(query, filter, this.gc_size);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < scoreDocs.length; i++) {
				Document doc = searcher.doc(scoreDocs[i].doc);
				String gc = doc.get(LuceneVo.GOODS_CLASS);
				list.add(gc);
			}
			// Collections.sort(list, new Comparator() {
			// public int compare(Object o1, Object o2) {
			// String str1[] = CommUtil.null2String(o1).split("_");
			// String str2[] = CommUtil.null2String(o2).split("_");
			// if (CommUtil.null2Int(str1[0]) > CommUtil.null2Int(str2[0])) {
			// return 1;
			// }
			// if (CommUtil.null2Int(str1[0]) == CommUtil
			// .null2Int(str2[0])) {
			// if (CommUtil.null2Int(str1[1]) > CommUtil
			// .null2Int(str2[1])) {
			// return 1;
			// }
			// }
			// return -1;
			// }
			// });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 添加列表到索引库中
	 * 
	 * @erikzhang
	 * @param list
	 * @throws IOException
	 */
	public void writeIndex(List<LuceneVo> list) throws IOException {
		IndexWriter writer = openIndexWriter();
		try {
			for (LuceneVo lucenceVo : list) {
				Document document = builderDocument(lucenceVo);
				writer.addDocument(document);
			}
			writer.commit();
		} finally {
			writer.close();
		}
	}

	/**
	 * 添加单个到索引库中
	 * 
	 * @erikzhang
	 * @param LuceneVo
	 * @throws IOException
	 */
	public void writeIndex(LuceneVo vo) {
		IndexWriter writer = null;
		if (sysConfig != null && sysConfig.getLucenen_queue() == 1) {
			LuceneThreadPool pool = LuceneThreadPool.instance();
			final LuceneVo lu_vo = vo;
			pool.addThread(new Runnable() {
				IndexWriter writer = WriterUtil.getIndexWriter(index_path);
				Document document = builderDocument(lu_vo);

				public void run() {
					// TODO Auto-generated method stub
					try {
						if (writer.isLocked(writer.getDirectory())) {
							writer.unlock(writer.getDirectory());
						}
						writer.addDocument(document);
						writer.commit();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		} else {
			try {
				writer = this.openIndexWriter();
				if (writer.isLocked(writer.getDirectory())) {
					writer.unlock(writer.getDirectory());
				}
				Document document = builderDocument(vo);
				writer.addDocument(document);
				writer.commit();
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 更新索引
	 */
	public void update(String id, LuceneVo vo) {
		IndexWriter writer = null;
		try {
			index_file = new File(index_path);
			// writer = WriterUtil.getIndexWriter(index_path);
			writer = openIndexWriter();
			Document doc = builderDocument(vo);
			Term term = new Term("id", String.valueOf(id));
			writer.updateDocument(term, doc);
			writer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除索引文件
	 * 
	 * @param id
	 */
	public void delete_index(String id) {
		IndexWriter writer = null;
		try {
			index_file = new File(index_path);
			Directory directory = FSDirectory.open(index_file);
			IndexWriterConfig writerConfig = new IndexWriterConfig(
					Version.LUCENE_35, analyzer);
			writer = new IndexWriter(directory, writerConfig);
			// index_file = new File(index_path);
			// writer = WriterUtil.getIndexWriter(index_path);
			Term term = new Term("id", String.valueOf(id));
			writer.deleteDocuments(term);
			writer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除所有索引文件
	 * 
	 * @erikzhang
	 */
	private void deleteAllFile() {
		index_file = new File(index_path);
		File[] files = index_file.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	private static IndexWriter openIndexWriter() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_4_10_2, analyzer);
		// 索引 设置为追加或者覆盖
		index_file = new File(index_path);
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = new IndexWriter(FSDirectory.open(index_file),
				indexWriterConfig);
		return writer;
	}

	@SuppressWarnings("static-access")
	private Document builderDocument(LuceneVo luceneVo) {
		Document document = new Document();
		Whitelist white = new Whitelist();
		if ("goods".equals(luceneVo.getVo_type())) {
			Field id = new Field(luceneVo.ID, String.valueOf(luceneVo
					.getVo_id()), Field.Store.YES, Field.Index.ANALYZED);
			Field title = new Field(luceneVo.TITLE, Jsoup.clean(
					luceneVo.getVo_title(), white.none()), Field.Store.YES,
					Field.Index.ANALYZED);
			title.setBoost(10F);
			Field content = new Field(luceneVo.CONTENT, Jsoup.clean(
					luceneVo.getVo_content(), white.none()), Field.Store.YES,
					Field.Index.ANALYZED);
			Field type = new Field(luceneVo.TYPE, luceneVo.getVo_type(),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			LongField add_time = new LongField(luceneVo.ADD_TIME,
					luceneVo.getVo_add_time(), Field.Store.YES);
			IntField goods_salenum = new IntField(luceneVo.GOODS_SALENUM,
					luceneVo.getVo_goods_salenum(), Field.Store.YES);
			IntField goods_collect = new IntField(luceneVo.GOODS_COLLECT,
					luceneVo.getVo_goods_collect(), Field.Store.YES);
			DoubleField well_evaluate = new DoubleField(luceneVo.WELL_EVALUATE,
					luceneVo.getVo_well_evaluate(), Field.Store.YES);
			DoubleField store_price = new DoubleField(luceneVo.STORE_PRICE,
					luceneVo.getVo_store_price(), Field.Store.YES);
			// 库存改为数值型字段
			IntField goods_inventory = new IntField(luceneVo.GOODS_INVENTORY,
					luceneVo.getVo_goods_inventory(), Field.Store.YES);

			Field goods_type = new Field(luceneVo.GOODS_TYPE,
					CommUtil.null2String(luceneVo.getVo_goods_type()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);

			if (luceneVo.getVo_main_photo_url() != null) {
				Field photo_url = new Field(luceneVo.MAIN_PHOTO_URL,
						CommUtil.null2String(luceneVo.getVo_main_photo_url()),
						Field.Store.YES, Field.Index.NOT_ANALYZED);
				document.add(photo_url);
			}

			Field photos_url = new Field(luceneVo.PHOTOS_URL,
					CommUtil.null2String(luceneVo.getVo_photos_url()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field vo_goods_evas = new Field(luceneVo.GOODS_EVAS,
					CommUtil.null2String(luceneVo.getVo_goods_evas()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			// 由于品牌名较短，所以此处即使不加强该域的权重，该域的优先级也较高。
			// 此域采用不分词分析器
			String gb_name = CommUtil.null2String(luceneVo
					.getVo_goods_brandname()).trim();
			Field vo_goods_brandname = new Field(luceneVo.GOODS_BRAND, gb_name,
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field vo_goods_class = new Field(LuceneVo.GOODS_CLASS,
					CommUtil.null2String(luceneVo.getVo_goods_class()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field vo_goods_transfee = new Field(LuceneVo.GOODS_TRANSFEE,
					CommUtil.null2String(luceneVo.getVo_goods_transfee()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field vo_goods_cod = new Field(LuceneVo.GOODS_COD,
					CommUtil.null2String(luceneVo.getVo_goods_cod()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field vo_whether_active = new Field(LuceneVo.WHETHER_ACTIVE,
					CommUtil.null2String(luceneVo.getVo_whether_active()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field vo_f_sale_type = new Field(LuceneVo.F_SALE_TYPE,
					CommUtil.null2String(luceneVo.getVo_f_sale_type()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);

			document.add(id);
			document.add(title);
			document.add(content);
			document.add(type);
			document.add(add_time);
			document.add(goods_salenum);
			document.add(goods_collect);
			document.add(well_evaluate);
			document.add(store_price);
			document.add(goods_inventory);
			document.add(goods_type);
			document.add(photos_url);
			document.add(vo_goods_evas);
			document.add(vo_goods_brandname);
			document.add(vo_goods_class);
			document.add(vo_goods_transfee);
			document.add(vo_goods_cod);
			document.add(vo_whether_active);
			document.add(vo_f_sale_type);
		}

		if ("lifegoods".equals(luceneVo.getVo_type())) {
			Field id = new Field(luceneVo.ID, String.valueOf(luceneVo
					.getVo_id()), Field.Store.YES, Field.Index.ANALYZED);
			Field title = new Field(luceneVo.TITLE, Jsoup.clean(
					luceneVo.getVo_title(), white.none()), Field.Store.YES,
					Field.Index.ANALYZED);
			title.setBoost(10);
			Field content = new Field(luceneVo.CONTENT, Jsoup.clean(
					luceneVo.getVo_content(), white.none()), Field.Store.YES,
					Field.Index.ANALYZED);
			Field type = new Field(luceneVo.TYPE, luceneVo.getVo_type(),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			DoubleField store_price = new DoubleField(luceneVo.STORE_PRICE,
					luceneVo.getVo_store_price(), Field.Store.YES);
			Field add_time = new Field(luceneVo.ADD_TIME,
					CommUtil.null2String(luceneVo.getVo_add_time()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field goods_salenum = new Field(luceneVo.GOODS_SALENUM,
					CommUtil.null2String(luceneVo.getVo_goods_salenum()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field goods_cat = new Field(luceneVo.Cat,
					CommUtil.null2String(luceneVo.getVo_cat()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			if (luceneVo.getVo_main_photo_url() != null) {
				Field goods_main_photo = new Field(luceneVo.MAIN_PHOTO_URL,
						CommUtil.null2String(luceneVo.getVo_main_photo_url()),
						Field.Store.YES, Field.Index.NOT_ANALYZED);
				document.add(goods_main_photo);
			}
			Field goods_rate = new Field(luceneVo.GOODS_RATE,
					CommUtil.null2String(luceneVo.getVo_rate()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field cost_price = new Field(luceneVo.COST_PRICE,
					CommUtil.null2String(luceneVo.getVo_cost_price()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			Field goods_area = new Field(luceneVo.GOODS_AREA,
					CommUtil.null2String(luceneVo.getVo_goods_area()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			document.add(id);
			document.add(title);
			document.add(content);
			document.add(type);
			document.add(store_price);
			document.add(add_time);
			document.add(goods_salenum);
			document.add(goods_cat);
			document.add(goods_rate);
			document.add(cost_price);
			document.add(goods_area);
		}

		return document;
	}

}
