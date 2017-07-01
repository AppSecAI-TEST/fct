/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       MPI基本参数工具类
 * =============================================================================
 */
package com.fct.pay.service.unionpay;

/**
 * 软件开发工具包 配制
 * 
 * @author xuyaowen
 * 
 */
public class SDKConfig {

	private static String signCertPath = ""; //功能：读取配置文件获取签名证书路径
	private static String validateCertDir = "";//功能：读取配置文件获取验签目录

	private static String signCertPwd = "";//功能：读取配置文件获取签名证书密码

	private static String signCertType = "";//签名类型：

	private static String cardRequestUrl = "";  //功能：有卡交易路径;
	private static String appRequestUrl = "";  //功能：appj交易路径;
	private static String singleQueryUrl = ""; //功能：读取配置文件获取交易查询地址
	private static String fileTransUrl = "";  //功能：读取配置文件获取文件传输类交易地址
	private static String frontTransUrl = ""; //功能：读取配置文件获取前台交易地址
	private static String backTransUrl = "";//功能：读取配置文件获取后台交易地址
	private static String batTransUrl = "";//功能：读取配批量交易地址

	private static String frontUrl = "";//功能：读取配置文件获取前台通知地址
	private static String backUrl = "";//功能：读取配置文件获取前台通知地址
	private static String refundBackUrl = "";//功能：读取配置文件获取退款后台通知地

	private static String version="";
	private static String encoding = "";
	private static String bizType ="";
	private static String signMethod ="";
	private static String accessType ="";
	private static String currencyCode ="";
	private static String merId ="";

	/** 磁道加密证书路径. */
	private static String encryptTrackCertPath;
	/** 磁道加密公钥模数. */
	private static String encryptTrackKeyModulus;
	/** 磁道加密公钥指数. */
	private static String encryptTrackKeyExponent;

	/** 证书使用模式(单证书/多证书) */
	private static String singleMode;

	/** 加密公钥证书路径. */
	private static String encryptCertPath;

	public static String getEncryptCertPath()
	{
		return SDKConfig.encryptCertPath;
	}

	public static void setEncryptCertPath(String encryptCertPath)
	{
		SDKConfig.encryptCertPath = encryptCertPath;
	}

	public static String getEncryptTrackCertPath()
	{
		return SDKConfig.encryptTrackCertPath;
	}

	public static void setEncryptTrackCertPath(String encryptTrackCertPath)
	{
		SDKConfig.encryptTrackCertPath = encryptTrackCertPath;
	}

	public static String getEncryptTrackKeyModulus()
	{
		return SDKConfig.encryptTrackKeyModulus;
	}

	public static void setEncryptTrackKeyModulus(String encryptTrackKeyModulus)
	{
		SDKConfig.encryptTrackKeyModulus = encryptTrackKeyModulus;
	}

	public static String getEncryptTrackKeyExponent()
	{
		return SDKConfig.encryptTrackKeyExponent;
	}

	public static void setEncryptTrackKeyExponent(String encryptTrackKeyExponent)
	{
		SDKConfig.encryptTrackKeyExponent = encryptTrackKeyExponent;
	}

	public static String getSingleMode()
	{
		return SDKConfig.singleMode;
	}

	public static void setSingleMode(String singleMode)
	{
		SDKConfig.singleMode = singleMode;
	}

	public static String getMerId()
	{
		return SDKConfig.merId;
	}

	public static void setMerId(String merId)
	{
		SDKConfig.merId = merId;
	}

	public static String getSignCertType()
	{
		return SDKConfig.signCertType;
	}

	public static void setSignCertType(String signCertType)
	{
		SDKConfig.signCertType = signCertType;
	}

	public static String getSignCertPath()
	{
		return SDKConfig.signCertPath;
	}

	public static void setSignCertPath(String signCertPath)
	{
		SDKConfig.signCertPath = signCertPath;
	}

	public static String getValidateCertDir()
	{
		return SDKConfig.validateCertDir;
	}

	public static void setValidateCertDir(String validateCertDir)
	{
		SDKConfig.validateCertDir = validateCertDir;
	}

	public static String getSignCertPwd()
	{
		return SDKConfig.signCertPwd;
	}

	public static void setSignCertPwd(String signCertPwd)
	{
		SDKConfig.signCertPwd = signCertPwd;
	}
	public static String getCardRequestUrl()
	{
		return SDKConfig.cardRequestUrl;
	}

	public static void setCardRequestUrl(String cardRequestUrl)
	{
		SDKConfig.cardRequestUrl = cardRequestUrl;
	}
	public static String getAppRequestUrl()
	{
		return SDKConfig.appRequestUrl;
	}

	public static void setAppRequestUrl(String appRequestUrl)
	{
		SDKConfig.appRequestUrl = appRequestUrl;
	}
	public static String getSingleQueryUrl()
	{
		return SDKConfig.singleQueryUrl;
	}

	public static void setSingleQueryUrl(String singleQueryUrl)
	{
		SDKConfig.singleQueryUrl = singleQueryUrl;
	}
	public static String getFileTransUrl()
	{
		return SDKConfig.fileTransUrl;
	}

	public static void setFileTransUrl(String fileTransUrl)
	{
		SDKConfig.fileTransUrl = fileTransUrl;
	}
	public static String getFrontTransUrl()
	{
		return SDKConfig.frontTransUrl;
	}

	public static void setFrontTransUrl(String frontTransUrl)
	{
		SDKConfig.frontTransUrl = frontTransUrl;
	}
	public static String getBackTransUrl()
	{
		return SDKConfig.backTransUrl;
	}

	public static void setBackTransUrl(String backTransUrl)
	{
		SDKConfig.backTransUrl = backTransUrl;
	}
	public static String getBatTransUrl()
	{
		return SDKConfig.batTransUrl;
	}

	public static void setBatTransUrl(String batTransUrl)
	{
		SDKConfig.batTransUrl = batTransUrl;
	}
	public static String getFrontUrl()
	{
		return SDKConfig.frontUrl;
	}

	public static void setFrontUrl(String frontUrl)
	{
		SDKConfig.frontUrl = frontUrl;
	}
	public static String getBackUrl()
	{
		return SDKConfig.backUrl;
	}

	public static void setBackUrl(String backUrl)
	{
		SDKConfig.backUrl = backUrl;
	}
	public static String getRefundBackUrl()
	{
		return SDKConfig.refundBackUrl;
	}

	public static void setRefundBackUrl(String refundBackUrl)
	{
		SDKConfig.refundBackUrl = refundBackUrl;
	}
	public static String getVersion()
	{
		return SDKConfig.version;
	}

	public static void setVersion(String version)
	{
		SDKConfig.version = version;
	}
	public static String getEncoding()
	{
		return SDKConfig.encoding;
	}

	public static void setEncoding(String encoding)
	{
		SDKConfig.encoding = encoding;
	}
	public static String getBizType()
	{
		return SDKConfig.bizType;
	}

	public static void setBizType(String bizType)
	{
		SDKConfig.bizType = bizType;
	}
	public static String getSignMethod()
	{
		return SDKConfig.signMethod;
	}

	public static void setSignMethod(String signMethod)
	{
		SDKConfig.signMethod = signMethod;
	}
	public static String getAccessType()
	{
		return SDKConfig.accessType;
	}

	public static void setAccessType(String accessType)
	{
		SDKConfig.accessType = accessType;
	}
	public static String getCurrencyCode()
	{
		return SDKConfig.currencyCode;
	}

	public static void setCurrencyCode(String currencyCode)
	{
		SDKConfig.currencyCode = currencyCode;
	}

}
