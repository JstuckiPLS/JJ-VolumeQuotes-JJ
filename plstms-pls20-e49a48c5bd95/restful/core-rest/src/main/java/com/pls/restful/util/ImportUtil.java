package com.pls.restful.util;

import com.pls.core.common.MimeTypes;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Util class to prevent code duplication for import functionality.
 * @author Alex Kyrychenko
 */
public final class ImportUtil {

    /**
     * Due to that it's a util class was created private constructor to prevent class instantiation.
     */
    private ImportUtil() {
    }

    /**
     * Methd that converts {@link LoadDocumentEntity} to {@link FileInputStreamResponseEntity}.
     *
     * @param document - {@link LoadDocumentEntity}
     * @return {@link FileInputStreamResponseEntity}
     */
    public static FileInputStreamResponseEntity toFixDocumentEntity(final LoadDocumentEntity document) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        MimeTypes fileType = ObjectUtils.defaultIfNull(document.getFileType(), MimeTypes.XLSX);
        String extension = ObjectUtils.toString(FilenameUtils.getExtension(document.getDocFileName()), fileType.name());
        String fileName = String.format("Import_Incorrect_Data_%s.%s", stringDate, extension);

        return new FileInputStreamResponseEntity(document.getStreamContent(), document.getStreamLength(), fileName);
    }
}
