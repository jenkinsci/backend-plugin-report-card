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

package org.jenkins.ci.backend.plugin_report;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.kohsuke.github.GHRepository;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class Plugin implements Comparable<Plugin> {

    private String artifactId       = null;

    private String groupId          = null;

    private String name             = null;

    private String parentArtifactId = null;

    private String parentGroupId    = null;

    private String parentVersion    = null;

    private String repositoryUrl    = null;

    private String version          = null;

    public Plugin(final GHRepository repository, final Model model) {
        super();

        if (model != null) {
            name = model.getName();
            groupId = model.getGroupId();
            artifactId = model.getArtifactId();
            version = model.getVersion();

            final Parent parent = model.getParent();
            if (parent != null) {
                parentGroupId = parent.getGroupId();
                parentArtifactId = parent.getArtifactId();
                parentVersion = parent.getVersion();
            }
        }

        if (repository != null) {
            repositoryUrl = repository.getUrl();
        }
    }

    public int compareTo(final Plugin other) {
        if (this == other) {
            return 0;
        }

        int rc;

        rc = new CompareToBuilder().append(parentVersion,
                other.getParentVersion()).toComparison();
        if (rc != 0) {
            return rc;
        }

        rc = new CompareToBuilder().append(parentGroupId,
                other.getParentGroupId()).toComparison();
        if (rc != 0) {
            return rc;
        }

        rc = new CompareToBuilder().append(parentArtifactId,
                other.getParentArtifactId()).toComparison();
        if (rc != 0) {
            return rc;
        }

        rc = new CompareToBuilder().append(groupId, other.getGroupId())
                .toComparison();
        if (rc != 0) {
            return rc;
        }

        rc = new CompareToBuilder().append(artifactId, other.getArtifactId())
                .toComparison();
        if (rc != 0) {
            return rc;
        }

        rc = new CompareToBuilder().append(version, other.getVersion())
                .toComparison();
        if (rc != 0) {
            return rc;
        }

        return new CompareToBuilder().append(repositoryUrl,
                other.getRepositoryUrl()).toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Plugin)) {
            return false;
        }

        final Plugin other = (Plugin) obj;

        return new EqualsBuilder().append(version, other.getVersion())
                .append(groupId, other.getGroupId())
                .append(artifactId, other.getArtifactId())
                .append(parentVersion, other.getParentVersion())
                .append(parentGroupId, other.getParentGroupId())
                .append(parentArtifactId, other.getParentArtifactId())
                .isEquals();
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getEffectiveArtifactId() {
        if (StringUtils.isEmpty(artifactId)) {
            return parentArtifactId;
        }

        return artifactId;
    }

    public String getEffectiveGroupId() {
        if (StringUtils.isEmpty(groupId)) {
            return parentGroupId;
        }

        return groupId;
    }

    public String getEffectiveName() {
        if (StringUtils.isEmpty(name)) {
            return getEffectiveArtifactId();
        }

        return name;
    }

    public String getEffectiveVersion() {
        if (StringUtils.isEmpty(version)) {
            return parentVersion;
        }

        return version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getParentArtifactId() {
        return parentArtifactId;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(version).append(groupId)
                .append(artifactId).append(parentVersion).append(parentGroupId)
                .append(parentArtifactId).append(repositoryUrl).toHashCode();
    }

    public boolean isJenkinsPlugin() {
        return "org.jenkins-ci.plugins".equalsIgnoreCase(parentGroupId)
                && "plugin".equalsIgnoreCase(parentArtifactId);
    }

    public boolean isOldHudsonPlugin() {
        return "org.jvnet.hudson.plugins".equalsIgnoreCase(parentGroupId)
                && "plugin".equalsIgnoreCase(parentArtifactId);
    }
}
