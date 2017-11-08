package org.lwd.frame.ctrl.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.ctrl.Dispatcher;
import org.lwd.frame.ctrl.context.HeaderAware;
import org.lwd.frame.ctrl.context.RequestAware;
import org.lwd.frame.ctrl.context.ResponseAware;
import org.lwd.frame.ctrl.context.SessionAware;
import org.lwd.frame.ctrl.http.context.*;
import org.lwd.frame.ctrl.http.upload.UploadHelper;
import org.lwd.frame.ctrl.status.Status;
import org.lwd.frame.storage.StorageListener;
import org.lwd.frame.storage.Storages;
import org.lwd.frame.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author lwd
 */
@Controller("frame.ctrl.http.service.helper")
public class ServiceHelperImpl implements ServiceHelper, StorageListener {
    private static final String ROOT = "/";
    private static final String SESSION_ID = "frame-session-id";

    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Context context;
    @Inject
    private TimeHash timeHash;
    @Inject
    private Logger logger;
    @Inject
    private HeaderAware headerAware;
    @Inject
    private SessionAware sessionAware;
    @Inject
    private RequestAware requestAware;
    @Inject
    private ResponseAware responseAware;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private Status status;
    @Inject
    private Optional<IgnoreTimeHash> ignoreTimeHash;
    @Inject
    private CookieAware cookieAware;
    @Value("${frame.ctrl.http.ignore.root:false}")
    private boolean ignoreRoot;
    @Value("${frame.ctrl.http.ignore.prefixes:/upload/}")
    private String ignorePrefixes;
    @Value("${frame.ctrl.http.ignore.names:}")
    private String ignoreNames;
    @Value("${frame.ctrl.http.ignore.suffixes:.ico,.js,.css,.html,.jpg,.jpeg,.gif,.png}")
    private String ignoreSuffixes;
    @Value("${frame.ctrl.http.cors:/WEB-INF/cors.json}")
    private String cors;
    private int contextPath;
    private String servletContextPath;
    private String[] prefixes;
    private String[] suffixes;
    private Set<String> ignoreUris;
    private Set<String> corsOrigins;
    private String corsMethods;
    private String corsHeaders;

    @Override
    public void setPath(String real, String context) {
        contextPath = validator.isEmpty(context) || context.equals(ROOT) ? 0 : context.length();
        servletContextPath = contextPath > 0 ? context : "";
        if (logger.isInfoEnable())
            logger.info("部署项目路径[{}]。", context);
        prefixes = converter.toArray(ignorePrefixes, ",");
        suffixes = converter.toArray(ignoreSuffixes, ",");

        ignoreUris = new HashSet<>();
        BeanFactory.getBeans(IgnoreUri.class).forEach(ignoreUri -> ignoreUris.addAll(Arrays.asList(ignoreUri.getIgnoreUris())));
    }

    @Override
    public boolean service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        if (contextPath > 0)
            uri = uri.substring(contextPath);
        String lowerCaseUri = uri.toLowerCase();
        if (lowerCaseUri.startsWith(UploadHelper.ROOT)) {
            if (!lowerCaseUri.startsWith(UploadHelper.ROOT + "image/"))
                response.setHeader("Content-Disposition", "attachment;filename=" + uri.substring(uri.lastIndexOf('/') + 1));

            if (logger.isDebugEnable())
                logger.debug("请求[{}]非图片上传资源。", uri);

            return false;
        }

        if (ignoreUris.contains(uri) || ignore(uri)) {
            if (logger.isDebugEnable())
                logger.debug("忽略请求[{}]。", uri);

            return false;
        }

        setCors(request, response);
        OutputStream outputStream = setContext(request, response, uri);
        if (timeHash.isEnable() && !timeHash.valid(request.getIntHeader("time-hash")) && !status.isStatus(uri) && (!ignoreTimeHash.isPresent() || !ignoreTimeHash.get().ignore())) {
            if (logger.isDebugEnable())
                logger.debug("请求[{}]TimeHash[{}]验证不通过。", uri, request.getIntHeader("time-hash"));

            return false;
        }

        dispatcher.execute();
        outputStream.flush();
        outputStream.close();

        return true;
    }

    private boolean ignore(String uri) {
        if (ignoreRoot && uri.equals(ROOT))
            return true;

        for (String prefix : prefixes)
            if (uri.startsWith(prefix))
                return true;

        int indexOf = uri.lastIndexOf('/');
        if (indexOf == -1)
            return false;

        String name = uri.substring(indexOf + 1);
        for (String n : suffixes)
            if (name.equals(n))
                return true;

        indexOf = name.lastIndexOf('.');
        if (indexOf == -1)
            return false;

        String suffix = name.substring(indexOf);
        for (String s : suffixes)
            if (suffix.equals(s))
                return true;

        return false;
    }

    private void setCors(HttpServletRequest request, HttpServletResponse response) {
        if (validator.isEmpty(corsOrigins))
            return;

        String origin = request.getHeader("Origin");
        if (!corsOrigins.contains("*") && !corsOrigins.contains(origin))
            return;

        response.addHeader("Access-Control-Allow-Origin", origin);
        response.addHeader("Access-Control-Allow-Methods", corsMethods);
        response.addHeader("Access-Control-Allow-Headers", corsHeaders);
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    public OutputStream setContext(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        context.setLocale(request.getLocale());
        headerAware.set(new HeaderAdapterImpl(request));
        sessionAware.set(new SessionAdapterImpl(getSessionId(request)));
        requestAware.set(new RequestAdapterImpl(request, uri));
        cookieAware.set(request, response);
        response.setCharacterEncoding(context.getCharset(null));
        OutputStream outputStream = response.getOutputStream();
        responseAware.set(new ResponseAdapterImpl(servletContextPath, response, outputStream));

        return outputStream;
    }

    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_ID);
        if (!validator.isEmpty(sessionId))
            return useframeSessionId(request, sessionId);

        sessionId = request.getParameter(SESSION_ID);
        if (!validator.isEmpty(sessionId))
            return useframeSessionId(request, sessionId);

        sessionId = converter.toString(request.getSession().getAttribute(SESSION_ID));
        if (!validator.isEmpty(sessionId))
            return sessionId;

        return request.getSession().getId();
    }

    private String useframeSessionId(HttpServletRequest request, String sessionId) {
        request.getSession().setAttribute(SESSION_ID, sessionId);

        return sessionId;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{cors};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        JSONObject object = json.toObject(io.readAsString(absolutePath));
        if (object == null) {
            corsOrigins = new HashSet<>();
            corsMethods = "";
            corsHeaders = "";
        } else {
            corsOrigins = new HashSet<>();
            if (object.containsKey("origin")) {
                JSONArray array = object.getJSONArray("origin");
                for (int i = 0, size = array.size(); i < size; i++)
                    corsOrigins.add(array.getString(i));
            }
            corsMethods = toString(object.getJSONArray("methods")).toUpperCase();
            corsHeaders = toString(object.getJSONArray("headers"));
        }
        if (logger.isInfoEnable())
            logger.info("设置跨域[{}:{}:{}]。", converter.toString(corsOrigins), corsMethods, corsHeaders);
    }

    private String toString(JSONArray array) {
        if (array == null)
            return "";

        Set<String> set = new HashSet<>();
        for (int i = 0, size = array.size(); i < size; i++)
            set.add(array.getString(i));

        StringBuilder sb = new StringBuilder();
        for (String string : set)
            sb.append(',').append(string);

        return sb.length() == 0 ? "" : sb.substring(1);
    }
}
