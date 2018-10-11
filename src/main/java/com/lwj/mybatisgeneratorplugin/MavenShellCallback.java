/*
 * 公司:北龙中网（北京）科技有限责任公司	网址:http://www.knet.cn
 * 
 */
package com.lwj.mybatisgeneratorplugin;

import java.io.File;
import java.util.StringTokenizer;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.messages.Messages;

/**
 * 类注释
 *
 * @author <a href="mailto:luwenjie@knet.cn">芦文杰</a>
 * @version 2015年12月25日 下午7:43:07
 * @since JDK1.7+
 */
public class MavenShellCallback extends DefaultShellCallback {
	private EasyMybatisGeneratorMojo mybatisGeneratorMojo;

	  public MavenShellCallback(EasyMybatisGeneratorMojo mybatisGeneratorMojo, boolean overwrite)
	  {
	    super(overwrite);
	    this.mybatisGeneratorMojo = mybatisGeneratorMojo;
	  }

	  public File getDirectory(String targetProject, String targetPackage)
	    throws ShellException
	  {
	    if (!"MAVEN".equals(targetProject)) {
	      return super.getDirectory(targetProject, targetPackage);
	    }

	    File project = this.mybatisGeneratorMojo.getOutputDirectory();
	    if (!project.exists()) {
	      project.mkdirs();
	    }

	    if (!project.isDirectory()) {
	      throw new ShellException(Messages.getString("Warning.9", project.getAbsolutePath()));
	    }

	    StringBuilder sb = new StringBuilder();
	    StringTokenizer st = new StringTokenizer(targetPackage, ".");
	    while (st.hasMoreTokens()) {
	      sb.append(st.nextToken());
	      sb.append(File.separatorChar);
	    }

	    File directory = new File(project, sb.toString());
	    if (!directory.isDirectory()) {
	      boolean rc = directory.mkdirs();
	      if (!rc) {
	        throw new ShellException(Messages.getString("Warning.10", directory.getAbsolutePath()));
	      }

	    }

	    return directory;
	  }
	

}
