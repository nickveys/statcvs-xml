/*
 * Copyright 2005, 2006 Tammo van Lessen, Steffen Pingel
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

package de.berlios.statcvs.xml.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

/**
 * Extension of the Commandline concept to cover executing a Java class.
 * 
 * @see org.codehaus.plexus.util.cli.Commandline
 */
public class JavaCommandLine {

    /** Proxied Commandline instance */
    private Commandline cli;

    /** List of archives to be added to the classpath */
    private List classPath;

    /** Main class to execute */
    private String mainClass;

    /**
     * Test if the given JVM is available for execution.
     * 
     * @param jvm executable name to find
     * 
     * @return true if it is available and executable, false otherwise
     */
    public static boolean isJavaAvailable(String jvm) {
        try {
            new Commandline(jvm).execute();
        } catch (CommandLineException e) {
            return false;
        }
        return true;
    }

    /**
     * Dump the contents of the writer to the given logger instance.
     * 
     * @param logger output logger
     * @param writer writer containing lines to output
     */
    public static void print(Log logger, Writer writer) {
        String string = writer.toString();
        if (string != null && string.length() > 0) {
            StringReader sr = new StringReader(string);
            BufferedReader br = new BufferedReader(sr);
            try {
                while ((string = br.readLine()) != null) {
                    logger.info(string);
                }
            } catch (IOException e) {
                logger.debug(e);
            }
        }
    }

    /**
     * Create a new java command line.
     * 
     * @see Commandline#Commandline()
     */
    public JavaCommandLine() {
        cli = new Commandline();
        classPath = new ArrayList();
    }

    /**
     * Add a path to the classpath.
     * 
     * @param file path to add
     */
    public void addClassPath(File file) {
        classPath.add(file);
    }

    /**
     * Get the classpath argument.
     * 
     * @return classpath argument
     */
    public String[] getClassPathArgs() {
        return new String[] { "-classpath", getClassPathAsString() };
    }

    /**
     * Get all of the paths on the classpath as a path-separated string.
     * 
     * @return classpath elements in one string
     */
    public String getClassPathAsString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator it = classPath.iterator(); it.hasNext();) {
            File file = (File) it.next();
            sb.append(file.getAbsolutePath());
            if (it.hasNext()) {
                sb.append(File.pathSeparator);
            }
        }
        return sb.toString();
    }

    /**
     * Get the contents of the command line.
     * 
     * @return command line contents
     * 
     * @see Commandline#getCommandline()
     */
    public String[] getCommandline() {
        return cli.getCommandline();
    }

    /**
     * Get the main class to be executed.
     * 
     * @return main class
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * Execute this java command line.
     * 
     * @param args arguments to add to command line
     * @param logger logger to output to
     * 
     * @return return code from the process
     * 
     * @throws CommandLineException
     */
    public int run(String[] args, Log logger) throws CommandLineException {
        cli.clearArgs();
        cli.addArguments(getClassPathArgs());
        cli.addArguments(new String[] { getMainClass() });
        cli.addArguments(args);

        String[] foo = cli.getArguments();
        for (int i = 0; i < foo.length; i++) {
            System.out.println(i + ": " + foo[i]);
        }

        Writer stringWriter = new StringWriter();
        StreamConsumer out = new WriterStreamConsumer(stringWriter);
        StreamConsumer err = new WriterStreamConsumer(stringWriter);

        int returnCode = CommandLineUtils.executeCommandLine(cli, out, err);

        /* dump output */
        print(logger, stringWriter);

        return returnCode;
    }

    /**
     * Set the JVM to use to execute the java class.
     * 
     * @param jvm executable name of jvm to use
     * 
     * @see Commandline#setExecutable(String)
     */
    public void setJvm(String jvm) {
        cli.setExecutable(jvm);
    }

    /**
     * Fully qualified class name containing a main() to execute.
     * 
     * @param mainClass class name
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Set the working directory of the process.
     * 
     * @param directory path to directory
     * 
     * @see Commandline#setWorkingDirectory(String)
     */
    public void setWorkingDirectory(File directory) {
        cli.setWorkingDirectory(directory.getAbsolutePath());
    }
}
