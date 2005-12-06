package de.berlios.statcvs.xml.maven;

/*
 * Copyright 2005 Tammo van Lessen, Steffen Pingel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.doxia.module.xdoc.XdocSiteModule;
import org.codehaus.doxia.site.renderer.SiteRenderer;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * 
 * @phase process-sources
 */
public class StatCvsMojo extends AbstractMojo {

	/**
	 * Location of the file.
	 * @parameter expression="${project.build.directory}/statcvs-xdocs"
	 * @required
	 */
	private File reportsDirectory;

    /**
     * Specifies the directory where the report will be generated
     *
     * @parameter default-value="${project.reporting.outputDirectory}"
     * @required
     */
    private File outputDirectory;
    
	/**
     * @component
     * @required
     * @readonly
     */
    private SiteRenderer siteRenderer;
    
	public void execute() throws MojoExecutionException
	{
		 if(reportsDirectory.exists()) {
             File[] fileNames = reportsDirectory.listFiles();

             if(fileNames.length > 0) {
                 XdocSiteModule xdoc = new XdocSiteModule();

                 //siteRenderer.render(reportsDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), xdoc.getSourceDirectory(), 
                 //		 xdoc.getExtension(), xdoc.getParserId());
                 //, siteDescriptor, template, attributes, locale, outputEncoding );
             }
         }
	}
}
