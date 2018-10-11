/*
 * 公司:北龙中网（北京）科技有限责任公司	网址:http://www.knet.cn
 * 
 */
package com.lwj.mybatisgeneratorplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

/**
 * 类注释
 * 
 * @author <a href="mailto:luwenjie@knet.cn">芦文杰</a>
 * @version 2015年12月25日 下午7:48:14
 * @since JDK1.7+
 */
public class SqlScriptRunner {
	private String driver;

	private String url;

	private String userid;

	private String password;

	private String sourceFile;

	private Log log;

	public SqlScriptRunner(String sourceFile, String driver, String url, String userId, String password)
			throws MojoExecutionException {
		if (!StringUtility.stringHasValue(sourceFile)) {
			throw new MojoExecutionException("SQL script file is required");
		}

		if (!StringUtility.stringHasValue(driver)) {
			throw new MojoExecutionException("JDBC Driver is required");
		}

		if (!StringUtility.stringHasValue(url)) {
			throw new MojoExecutionException("JDBC URL is required");
		}

		this.sourceFile = sourceFile;
		this.driver = driver;
		this.url = url;
		this.userid = userId;
		this.password = password;
	}

	public void executeScript() throws MojoExecutionException {
		Connection connection = null;
		try {
			Class.forName(this.driver);
			connection = DriverManager.getConnection(this.url, this.userid, this.password);

			Statement statement = connection.createStatement();

			BufferedReader br = getScriptReader();
			String sql;
			while ((sql = readStatement(br)) != null) {
				statement.execute(sql);
			}

			closeStatement(statement);
			connection.commit();
			br.close();
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("Class not found: " + e.getMessage());
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("File note found: " + this.sourceFile);
		} catch (SQLException e) {
			throw new MojoExecutionException("SqlException: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException("IOException: " + e.getMessage(), e);
		} finally {
			closeConnection(connection);
		}
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private void closeConnection(Connection connection) {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
			}
	}

	private void closeStatement(Statement statement) {
		if (statement != null)
			try {
				statement.close();
			} catch (SQLException e) {
			}
	}

	private String readStatement(BufferedReader br) throws IOException {
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			if ((!line.startsWith("--")) && (StringUtility.stringHasValue(line))) {
				if (line.endsWith(";")) {
					sb.append(line.substring(0, line.length() - 1));
					break;
				}
				sb.append(' ');
				sb.append(line);
			}
		}

		String s = sb.toString().trim();

		if (s.length() > 0) {
			this.log.debug(Messages.getString("Progress.13", s));
		}

		return s.length() > 0 ? s : null;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	private BufferedReader getScriptReader() throws MojoExecutionException, FileNotFoundException {
		BufferedReader answer;
		if (this.sourceFile.startsWith("classpath:")) {
			String resource = this.sourceFile.substring("classpath:".length());
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

			if (is == null) {
				throw new MojoExecutionException("SQL script file does not exist: " + resource);
			}
			answer = new BufferedReader(new InputStreamReader(is));
		} else {
			File file = new File(this.sourceFile);
			if (!file.exists()) {
				throw new MojoExecutionException("SQL script file does not exist");
			}
			answer = new BufferedReader(new FileReader(file));
		}

		return answer;
	}
}
