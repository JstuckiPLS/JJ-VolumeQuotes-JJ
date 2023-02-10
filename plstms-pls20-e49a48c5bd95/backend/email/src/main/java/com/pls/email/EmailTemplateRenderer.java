package com.pls.email;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class for rendering freemarker email templates.
 * 
 * @author Stas Norochevskiy
 *
 */
@Component
public class EmailTemplateRenderer {

    private static final String EMAIL_TEMPLATES_PATH = "/emailTemplates";

    /**
     * Render freemarker template to get actual email body.
     * @param templateFilename email template file
     * @param data map with values for filling email template
     * @return email body
     * @throws IOException exception
     * @throws TemplateException exception
     */
    public String renderEmailTemplate(String templateFilename, Map<String, Object> data)
            throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        cfg.setClassForTemplateLoading(this.getClass(), EMAIL_TEMPLATES_PATH);
        cfg.setLocale(Locale.US);
        Template template = cfg.getTemplate(templateFilename);

        StringWriter writer = new StringWriter();
        template.process(data, writer);
        writer.flush();
        writer.close();

        return writer.toString();
    }
}
