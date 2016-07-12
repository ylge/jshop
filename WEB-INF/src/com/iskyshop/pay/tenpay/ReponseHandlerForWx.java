package com.iskyshop.pay.tenpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jdom.JDOMException;

import com.iskyshop.pay.tenpay.util.MD5Util;
import com.iskyshop.pay.tenpay.util.Sha1Util;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.pay.tenpay.util.XMLUtil;

public class ReponseHandlerForWx {
	private static final String appkey = null;

	/** 瀵嗛挜 */
	private String key;

	/** 搴旂瓟鐨勫弬鏁� */
	private SortedMap parameters;

	/** debug淇℃伅 */
	private String debugInfo;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private String uriEncoding;

	private Hashtable xmlMap;

	private String k;

	private String AppSignature;// 浠庢瀯閫犳柟娉曚腑鑾峰彇AppSignature

	private SortedMap smap;

	public SortedMap getSmap() {
		return smap;
	}

	/**
	 * 鏋勯�犲嚱鏁�
	 * 
	 * @param request
	 * @param response
	 */
	public ReponseHandlerForWx(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;

		this.key = "";
		this.parameters = new TreeMap();
		this.debugInfo = "";
		this.smap = new TreeMap<String, String>();
		this.uriEncoding = "";

		Map m = this.request.getParameterMap();
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			String v = ((String[]) m.get(k))[0];
			this.setParameter(k, v);
		}

		// 瑙ｆ瀽xml
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					request.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			System.out.println(sb);
			Document doc = DocumentHelper.parseText(sb.toString());
			Element root = doc.getRootElement();
			for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
				Element e = (Element) iterator.next();
				smap.put(e.getName(), e.getText());
			}
			// sb鐨勬牸寮�
			// 锛�<xml><OpenId><![CDATA[oOe6xuHgTll7b-NVknmM4xZUOyBM]]></OpenId>
			// <AppId><![CDATA[wxdf3c2d94d6b478c5]]></AppId>
			// <IsSubscribe>1</IsSubscribe>
			// <TimeStamp>1408954584</TimeStamp>
			// <NonceStr><![CDATA[4xSple7AarRBSCKl]]></NonceStr>
			// <AppSignature><![CDATA[728d52bd64d6f047a7aa60cb961ffc9b4a5be53b]]></AppSignature>
			// <SignMethod><![CDATA[sha1]]></SignMethod></xml>
			AppSignature = doc.selectSingleNode("xml/AppSignature") != null ? doc
					.selectSingleNode("xml/AppSignature").getText() : "";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 鑾峰彇瀵嗛挜
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 璁剧疆瀵嗛挜
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 鑾峰彇鍙傛暟鍊�
	 * 
	 * @param parameter
	 *            鍙傛暟鍚嶇О
	 * @return String
	 */
	public String getParameter(String parameter) {
		String s = (String) this.parameters.get(parameter);
		return (null == s) ? "" : s;
	}

	/**
	 * 璁剧疆鍙傛暟鍊�
	 * 
	 * @param parameter
	 *            鍙傛暟鍚嶇О
	 * @param parameterValue
	 *            鍙傛暟鍊�
	 */
	public void setParameter(String parameter, String parameterValue) {
		String v = "";
		if (null != parameterValue) {
			v = parameterValue.trim();
		}
		this.parameters.put(parameter, v);
	}

	/**
	 * 杩斿洖鎵�鏈夌殑鍙傛暟
	 * 
	 * @return SortedMap
	 */
	public SortedMap getAllParameters() {
		return this.parameters;
	}

	public void doParse(String xmlContent) throws JDOMException, IOException {
		this.parameters.clear();
		// 瑙ｆ瀽xml,寰楀埌map
		Map m = XMLUtil.doXMLParse(xmlContent);

		// 璁剧疆鍙傛暟
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			String v = (String) m.get(k);
			this.setParameter(k, v);
		}
	}

	/**
	 * 鏄惁璐粯閫氱鍚�,瑙勫垯鏄�:鎸夊弬鏁板悕绉癮-z鎺掑簭,閬囧埌绌哄�肩殑鍙傛暟涓嶅弬鍔犵鍚嶃��
	 * 
	 * @return boolean
	 */
	public boolean isValidSign(String pkey) {
		StringBuffer sb = new StringBuffer();
		Set es = this.parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}

		sb.append("key=" + pkey);

		// 绠楀嚭鎽樿
		String enc = TenpayUtil.getCharacterEncoding(this.request,
				this.response);
		String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();

		String ValidSign = this.getParameter("sign").toLowerCase();

		// debug淇℃伅
		this.setDebugInfo(sb.toString() + " => sign:" + sign + " ValidSign:"
				+ ValidSign);

		return ValidSign.equals(sign);
	}

	/**
	 * 鍒ゆ柇寰俊绛惧悕
	 */
	public boolean isWXsign(String akey) {
		StringBuffer sb = new StringBuffer();
		String keys = "";
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		Set es = this.smap.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (k != "SignMethod" && k != "AppSignature") {
				signParams.put(k.toLowerCase(), v);
			}
		}
		signParams.put("appkey",akey);
		
		Set set = signParams.entrySet();
		Iterator pit = set.iterator();
		while (pit.hasNext()) {
			Map.Entry entry = (Map.Entry) pit.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (sb.length() == 0) {
				sb.append(k + "=" + v);
			} else {
				sb.append("&" + k + "=" + v);
			}
		}
		String sign = Sha1Util.getSha1(sb.toString()).toString().toLowerCase();

		this.setDebugInfo(sb.toString() + " => SHA1 sign:" + sign);
		System.out.println("鍔犲瘑鍚庣殑锛�" + sign);
		System.out.println("娴佷腑鑾峰彇鐨�:" + this.AppSignature);
		return sign.equals(this.AppSignature);

	}

	// 鍒ゆ柇寰俊缁存潈绛惧悕
	public boolean isWXsignfeedback() {

		StringBuffer sb = new StringBuffer();
		Hashtable signMap = new Hashtable();
		Set es = this.parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (k != "SignMethod" && k != "AppSignature") {

				sb.append(k + "=" + v + "&");
			}
		}
		signMap.put("appkey", this.appkey);

		// ArrayList akeys = new ArrayList();
		// akeys.Sort();
		while (it.hasNext()) {
			String v = k;
			if (sb.length() == 0) {
				sb.append(k + "=" + v);
			} else {
				sb.append("&" + k + "=" + v);
			}
		}

		String sign = Sha1Util.getSha1(sb.toString()).toString().toLowerCase();

		this.setDebugInfo(sb.toString() + " => SHA1 sign:" + sign);

		return sign.equals("App    Signature");// 杩欓噷鐜板湪鑲畾涓嶅ソ浣�
	}

	/**
	 * 杩斿洖澶勭悊缁撴灉缁欒储浠橀�氭湇鍔″櫒銆�
	 * 
	 * @param msg
	 *            Success or fail
	 * @throws IOException
	 */
	public void sendToCFT(String msg) throws IOException {
		String strHtml = msg;
		PrintWriter out = this.getHttpServletResponse().getWriter();
		out.println(strHtml);
		out.flush();
		out.close();

	}

	/**
	 * 鑾峰彇uri缂栫爜
	 * 
	 * @return String
	 */
	public String getUriEncoding() {
		return uriEncoding;
	}

	/**
	 * 璁剧疆uri缂栫爜
	 * 
	 * @param uriEncoding
	 * @throws UnsupportedEncodingException
	 */
	public void setUriEncoding(String uriEncoding)
			throws UnsupportedEncodingException {
		if (!"".equals(uriEncoding.trim())) {
			this.uriEncoding = uriEncoding;

			// 缂栫爜杞崲
			String enc = TenpayUtil.getCharacterEncoding(request, response);
			Iterator it = this.parameters.keySet().iterator();
			while (it.hasNext()) {
				String k = (String) it.next();
				String v = this.getParameter(k);
				v = new String(v.getBytes(uriEncoding.trim()), enc);
				this.setParameter(k, v);
			}
		}
	}

	/**
	 * 鑾峰彇debug淇℃伅
	 */
	public String getDebugInfo() {
		return debugInfo;
	}

	/**
	 * 璁剧疆debug淇℃伅
	 */
	protected void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}

	protected HttpServletRequest getHttpServletRequest() {
		return this.request;
	}

	protected HttpServletResponse getHttpServletResponse() {
		return this.response;
	}

	public String getAppSignature() {
		return AppSignature;
	}
}
