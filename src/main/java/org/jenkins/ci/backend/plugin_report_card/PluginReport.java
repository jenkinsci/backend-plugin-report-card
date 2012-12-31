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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.velocity.VelocityContext;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class PluginReport {
    private static final Logger LOG = Logger.getLogger(PluginReport.class
                                            .getName());

    public static String generatePluginReport() throws Exception {
        return generatePluginReport(GitHub.connect());
    }

    protected static String generatePluginReport(
            final Collection<GHRepository> repositories) throws Exception {
        if (repositories != null) {
            final Map<String, RequiredCore> requiredCores = getRequiredCoreMap();
            final SortedSet<Plugin> plugins = new TreeSet<Plugin>();

            // int idx = 0;
            for (final GHRepository repository : repositories) {
                // if (++idx > 20) {
                // break;
                // }

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Processing " + repository.getUrl());
                }

                final Model model = MavenUtils
                        .getModel(getGitHubPomURL(repository));
                if (model != null) {
                    final Plugin plugin = new Plugin(repository, model);
                    if (plugin.isJenkinsPlugin() || plugin.isOldHudsonPlugin()) {
                        if (!requiredCores.containsKey(plugin
                                .getParentVersion())) {
                            requiredCores
                                    .put(plugin.getParentVersion(),
                                            new RequiredCore(plugin
                                                    .getParentVersion()));
                        }

                        requiredCores.get(plugin.getParentVersion())
                                .increment();
                        plugins.add(plugin);
                    }
                }
            }

            return generatePluginReport(plugins, new TreeSet<RequiredCore>(
                    requiredCores.values()));
        }

        return null;
    }

    protected static String generatePluginReport(
            final GHOrganization organization) throws Exception {
        if (organization == null) {
            return null;
        }

        return generatePluginReport(organization.getRepositories());
    }

    protected static String generatePluginReport(final GitHub github)
            throws Exception {
        if (github == null) {
            return null;
        }

        return generatePluginReport(github.getOrganization("jenkinsci"));
    }

    protected static String generatePluginReport(
            final Map<String, GHRepository> repositories) throws Exception {
        if (repositories == null) {
            return null;
        }

        return generatePluginReport(repositories.values());
    }

    protected static String generatePluginReport(
            final SortedSet<Plugin> plugins,
            final SortedSet<RequiredCore> requiredCores) throws Exception {
        final VelocityContext context = new VelocityContext();
        context.put("now", new Date());
        context.put("requiredCores", requiredCores);
        context.put("plugins", plugins);

        return VelocityUtils.interpolate(
                VelocityUtils.getVelocityTemplate("pluginReportCard.vm"),
                context);
    }

    protected static URL getGitHubPomURL(final GHRepository repository)
            throws MalformedURLException {
        if (repository == null) {
            return null;
        }

        final String url = repository.getUrl();

        if (StringUtils.isEmpty(url)) {
            return null;
        }

        return new URL(url + "/raw/master/pom.xml");
    }

    protected static Map<String, RequiredCore> getRequiredCoreMap() {
        final Map<String, RequiredCore> requiredCores = new HashMap<String, RequiredCore>();

        for (int requiredCore = 300; requiredCore <= 600; requiredCore++) {
            final String version = "1." + Integer.toString(requiredCore);
            requiredCores.put(version, new RequiredCore(version));
        }

        return requiredCores;
    }
}
