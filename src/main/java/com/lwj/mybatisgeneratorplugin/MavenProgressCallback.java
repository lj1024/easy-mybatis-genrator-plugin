/*
 * 公司:北龙中网（北京）科技有限责任公司	网址:http://www.knet.cn
 * 
 */
package com.lwj.mybatisgeneratorplugin;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.mybatis.generator.internal.NullProgressCallback;

/**
 * 类注释
 * 
 * @author <a href="mailto:luwenjie@knet.cn">芦文杰</a>
 * @version 2015年12月25日 下午7:40:44
 * @since JDK1.7+
 */
public class MavenProgressCallback extends NullProgressCallback {

	private Log log;

	private boolean verbose;

	public MavenProgressCallback(Log log, boolean verbose) {
		this.log = log;
		this.verbose = verbose;
	}

	public void startTask(String subTaskName) {
		if (this.verbose)
			this.log.info(subTaskName);
	}

}
