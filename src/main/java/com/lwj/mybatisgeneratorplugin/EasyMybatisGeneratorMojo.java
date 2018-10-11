package com.lwj.mybatisgeneratorplugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class EasyMybatisGeneratorMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project.build.sourceDirectory}", property = "outputDirectory", required = true )
	private File outputDirectory;

    @Parameter( defaultValue = "${basedir}", required = true ,readonly=true)
	private File sourceDirectory;
    
    @Parameter
   	private File configurationFile;

    @Parameter( defaultValue ="false", property = "verbose", required = true )
	private boolean verbose;
    
    @Parameter( defaultValue ="true", property = "overwrite", required = true )
	private boolean overwrite;

	private String sqlScript;

	private String jdbcDriver;

	private String jdbcURL;

	private String jdbcUserId;

	private String jdbcPassword;

	private String tableNames;

	private String contexts;

	public void execute() throws MojoExecutionException {
		if(configurationFile == null) {
			configurationFile = new File(sourceDirectory, "src/main/resources/generatorConfig.xml");
		}
		getLog().info(sourceDirectory.getAbsolutePath());
		getLog().info(outputDirectory.getAbsolutePath());

		if (this.configurationFile == null) {
			throw new MojoExecutionException(Messages.getString("RuntimeError.0"));
		}
        
		List<String> warnings = new ArrayList<String>();

		if (!this.configurationFile.exists()) {
			throw new MojoExecutionException(Messages.getString("RuntimeError.1", this.configurationFile.toString()));
		}

		runScriptIfNecessary();

		Set fullyqualifiedTables = new HashSet();
		if (StringUtility.stringHasValue(this.tableNames)) {
			StringTokenizer st = new StringTokenizer(this.tableNames, ",");
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				if (s.length() > 0) {
					fullyqualifiedTables.add(s);
				}
			}
		}

		Set contextsToRun = new HashSet();
		if (StringUtility.stringHasValue(this.contexts)) {
			StringTokenizer st = new StringTokenizer(this.contexts, ",");
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				if (s.length() > 0) {
					contextsToRun.add(s);
				}
			}
		}
		try {
			ConfigurationParser cp = new ConfigurationParser(warnings);

			Configuration config = cp.parseConfiguration(this.configurationFile);

			ShellCallback callback = new MavenShellCallback(this, this.overwrite);

			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

			myBatisGenerator.generate(new MavenProgressCallback(getLog(), this.verbose), contextsToRun,
				fullyqualifiedTables);
		} catch (XMLParserException e) {
			for (String error : e.getErrors()) {
				getLog().error(error);
			}

			throw new MojoExecutionException(e.getMessage());
		} catch (SQLException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (InvalidConfigurationException e) {
			for (String error : e.getErrors()) {
				getLog().error(error);
			}

			throw new MojoExecutionException(e.getMessage());
		} catch (InterruptedException e) {
		}

		for (String error : warnings) {
			getLog().warn(error);
		}

	}

	private void runScriptIfNecessary() throws MojoExecutionException {
		if (this.sqlScript == null) {
			return;
		}

		SqlScriptRunner scriptRunner = new SqlScriptRunner(this.sqlScript, this.jdbcDriver, this.jdbcURL,
				this.jdbcUserId, this.jdbcPassword);

		scriptRunner.setLog(getLog());
		scriptRunner.executeScript();
	}

	public File getOutputDirectory() {
		return this.outputDirectory;
	}
}
