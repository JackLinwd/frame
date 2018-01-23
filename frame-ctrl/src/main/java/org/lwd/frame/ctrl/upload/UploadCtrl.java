package org.lwd.frame.ctrl.upload;

import org.lwd.frame.ctrl.context.Request;
import org.lwd.frame.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Controller(UploadService.PREFIX + "ctrl")
@Execute(name = "/frame/ctrl/", key = "frame.ctrl")
public class UploadCtrl {
    @Inject
    private Request request;
    @Inject
    private UploadService uploadService;

    @Execute(name = "uploads")
    public Object uploads() {
        return uploadService.uploads(request.getFromInputStream());
    }

    @Execute(name = "upload")
    public Object upload() {
        return uploadService.upload(request.get("fieldName"), request.get("fileName"), request.get("contentType"), request.get("base64"));
    }
}
