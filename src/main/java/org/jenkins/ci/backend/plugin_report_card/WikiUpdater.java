/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkins.ci.backend.plugin_report_card;

import hudson.plugins.jira.soap.ConfluenceSoapService;
import hudson.plugins.jira.soap.RemotePage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.jvnet.hudson.confluence.Confluence;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class WikiUpdater {
    private static final Logger LOG        = Logger.getLogger(WikiUpdater.class
                                                   .getName());

    private static File         credential = new File(
                                                   new File(
                                                           System.getProperty("user.home")),
                                                   ".jenkins-ci.org");

    private static String       userName;

    private static String       password;

    public static void validateConfiguration() throws IOException {
        if (!credential.exists()) {
            throw new FileNotFoundException(
                    "You need to have userName and password in " + credential);
        }

        if (!credential.isFile()) {
            throw new IOException("Credential file is not a file " + credential);
        }

        if (!credential.canRead()) {
            throw new IOException("Can't read credential file " + credential);
        }

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(credential);
            final Properties props = new Properties();
            props.load(inputStream);
            userName = props.getProperty("userName");
            password = props.getProperty("password");
        }

        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static void updateWikiPage(final String pageName,
            final String contents) throws IOException, ServiceException {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Updating " + pageName + " with "
                    + contents.substring(0, Math.min(128, contents.length()))
                    + " ... ");
        }

        if (StringUtils.isEmpty(pageName)) {
            return;
        }

        if (StringUtils.isEmpty(contents)) {
            return;
        }

        validateConfiguration();

        final ConfluenceSoapService service = Confluence.connect(new URL(
                "https://wiki.jenkins-ci.org/"));
        final String token = service.login(userName, password);
        final RemotePage page = service.getPage(token, "JENKINS", pageName);
        page.setContent(contents);
        service.storePage(token, page);
    }
}