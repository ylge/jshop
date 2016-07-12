package com.iskyshop.lucene.parse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.Version;

import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: ShopQueryParser.java
 * </p>
 * 
 * <p>
 * Description: 自定义lucene查询分析器，支持区间查询
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
 * @author erikzhang,jy
 * 
 * @date 2014-10-30
 * 
 * @version iskyshop_b2b2c_2015
 */
public class ShopQueryParser extends QueryParser {
	public ShopQueryParser(Version matchVersion, String field, Analyzer a) {
		super(matchVersion, field, a);
	}

	@Override
	protected Query getRangeQuery(String field, String part1, String part2,
			boolean startInclusive, boolean endInclusive) throws ParseException {
		// TODO Auto-generated method stub
		TermRangeQuery query = (TermRangeQuery) super.getRangeQuery(field,
				part1, part2, startInclusive, endInclusive);
		if ("goods_inventory".equals(field)) {
			return NumericRangeQuery.newIntRange(field,
					CommUtil.null2Int(part1), CommUtil.null2Int(part2),
					query.includesLower(), query.includesUpper());
		} else {
			return query;
		}
	}
}
