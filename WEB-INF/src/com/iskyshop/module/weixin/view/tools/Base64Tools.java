package com.iskyshop.module.weixin.view.tools;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
@Component
public class Base64Tools {
	Base64 base64 = new Base64();  
	 /** 
     * 解密 
     *  
     * @param pwd 
     * @return 
     * @see [类、类#方法、类#成员] 
     */  
	public String decodeStr(String pwd)  
    {  
        byte[] debytes = base64.decodeBase64(new String(pwd).getBytes());  
        return new String(debytes);  
    }  
  
    /** 
     * 加密 
     *  
     * @param pwd 
     * @return 
     * @see [类、类#方法、类#成员] 
     */  
    public String encodeStr(String pwd)  
    {  
        byte[] enbytes = base64.encodeBase64Chunked(pwd.getBytes());  
        return new String(enbytes);  
    }  
}
