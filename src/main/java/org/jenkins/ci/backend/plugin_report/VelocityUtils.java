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

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class VelocityUtils {
    static {
        try {
            Velocity.init();
        }

        catch (final Exception e) {
            throw new Error(e);
        }
    }

    public static VelocityContext getVelocityContext(
            final Map<String, ?> properties) {
        return new VelocityContext(properties);
    }

    public static Template getVelocityTemplate(final String vm)
            throws Exception {
        return Velocity.getTemplate("src/main/resources/"
                + VelocityUtils.class.getPackage().getName()
                        .replaceAll("\\.", "/") + "/" + vm);
    }

    public static String interpolate(final Template template,
            final Map<String, ?> properties) throws Exception {
        return interpolate(template, getVelocityContext(properties));
    }

    public static String interpolate(final Template template,
            final VelocityContext context) throws Exception {
        final StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    /**
     * Static-only access.
     */
    private VelocityUtils() {
        // static-only access
    }
}
