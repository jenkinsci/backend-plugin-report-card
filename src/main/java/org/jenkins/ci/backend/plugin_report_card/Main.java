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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class Main implements Runnable {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(final String[] args) {
        new Main().run();
    }

    public void run() {
        try {
            WikiUpdater.validateConfiguration();

            final String report = PluginReport.generatePluginReport();

            // if (LOG.isLoggable(Level.FINE)) {
            LOG.info(report);
            // }

            if (StringUtils.isNotEmpty(report)) {
                WikiUpdater.updateWikiPage("Plugin Report Card", report);
            }
        }

        catch (final Exception e) {
            LOG.warning(e.getMessage());
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, e.getMessage(), e);
            }
        }
    }
}
