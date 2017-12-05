package org.lwd.frame.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Aspect
@Component("frame.test.aspect.http")
public class HttpAspectImpl implements HttpAspect {
    private ThreadLocal<List<String>> getUrls = new ThreadLocal<>();
    private ThreadLocal<List<Map<String, String>>> getHeaders = new ThreadLocal<>();
    private ThreadLocal<List<Object>> getParameters = new ThreadLocal<>();
    private ThreadLocal<List<String>> getContents = new ThreadLocal<>();
    private ThreadLocal<List<String>> postUrls = new ThreadLocal<>();
    private ThreadLocal<List<Map<String, String>>> postHeaders = new ThreadLocal<>();
    private ThreadLocal<List<Object>> postParameters = new ThreadLocal<>();
    private ThreadLocal<List<String>> postContents = new ThreadLocal<>();

    @Override
    public void reset() {
        getUrls.remove();
        getHeaders.remove();
        getParameters.remove();
        getContents.remove();
        postUrls.remove();
        postHeaders.remove();
        postParameters.remove();
        postContents.remove();
    }

    @Override
    public void get(List<String> urls, List<Map<String, String>> headers, List<Object> parameters, List<String> contents) {
        getUrls.set(urls);
        getHeaders.set(headers);
        getParameters.set(parameters);
        getContents.set(contents);
    }

    @Override
    public void post(List<String> urls, List<Map<String, String>> headers, List<Object> parameters, List<String> contents) {
        postUrls.set(urls);
        postHeaders.set(headers);
        postParameters.set(parameters);
        postContents.set(contents);
    }

    @SuppressWarnings({"unchecked"})
    @Around("target(org.lwd.frame.util.Http)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String name = point.getSignature().getName();
        if (name.equals("get")) {
            List<String> urls = getUrls.get();
            List<String> contents = getContents.get();
            if (urls != null && contents != null && contents.size() > urls.size()) {
                urls.add((String) point.getArgs()[0]);
                getHeaders.get().add((Map<String, String>) point.getArgs()[1]);
                getParameters.get().add(point.getArgs()[2]);

                return contents.get(urls.size() - 1);
            }
        } else if (name.equals("post")) {
            List<String> urls = postUrls.get();
            List<String> contents = postContents.get();
            if (urls != null && contents != null && contents.size() > urls.size()) {
                urls.add((String) point.getArgs()[0]);
                postHeaders.get().add((Map<String, String>) point.getArgs()[1]);
                postParameters.get().add(point.getArgs()[2]);

                return contents.get(urls.size() - 1);
            }
        }

        return point.proceed();
    }
}
