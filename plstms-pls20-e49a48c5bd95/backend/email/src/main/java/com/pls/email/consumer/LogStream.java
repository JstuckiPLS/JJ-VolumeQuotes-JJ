package com.pls.email.consumer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stream for logging emails.
 * 
 * @author Aleksandr Leshchenko
 */
public class LogStream extends FilterOutputStream {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderImpl.class);
    private List<String> out = new ArrayList<String>();
    private static final String CONTENT_DISPOSITION = "Content-Disposition: attachment";
    private static final Pattern ATTACHMENT_END_PATTERN = Pattern.compile("_Part_\\d");

    /**
     * Constructor.
     */
    public LogStream() {
        super(null);
        out.add("");
    }

    @Override
    public void flush() {
        boolean contentDispositionFound = false;
        Iterator<String> it = out.iterator();
        while (it.hasNext()) {
            String string = it.next(); // FIXME java.util.ConcurrentModificationException
            if (string.startsWith(CONTENT_DISPOSITION)) {
                contentDispositionFound = true;
            } else if (contentDispositionFound) {
                if (ATTACHMENT_END_PATTERN.matcher(string).find()) {
                    contentDispositionFound = false;
                } else {
                    it.remove();
                }
            }
        }
        LOGGER.info(out.stream().collect(Collectors.joining("\n")));
        out.clear();
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(new String(b));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        write(new String(b, off, len));
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte) b });
    }

    private void write(String string) {
        if (!StringUtils.isBlank(string)) {
            out.add(string);
        }
    }
}