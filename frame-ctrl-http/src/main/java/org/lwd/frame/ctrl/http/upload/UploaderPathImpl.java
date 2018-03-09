package org.lwd.frame.ctrl.http.upload;

import org.lwd.frame.ctrl.upload.UploadReader;
import org.lwd.frame.ctrl.upload.UploadService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * @author lwd
 */
@Service(UploadHelper.PREFIX + "uploader.path")
public class UploaderPathImpl implements Uploader {
    @Inject
    private UploadService uploadService;

    @Override
    public String getName() {
        return UploadHelper.UPLOAD_PATH;
    }

    @Override
    public byte[] upload(List<UploadReader> readers) throws IOException {
        return uploadService.upload(readers.get(0)).getString("path").getBytes();
    }
}