package org.lwd.frame.dao.test;

import org.junit.Before;
import org.lwd.frame.atomic.Closables;
import org.lwd.frame.test.CoreTestSupport;
import org.lwd.frame.util.Validator;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lwd
 */
public class DaoTestSupport extends CoreTestSupport {
    @Inject
    private Validator validator;
    @Resource(name = "frame.dao.jdbc.sql")
    private Object sql;
    @Inject
    private Closables closables;
    private Method update;

    @Before
    public void before() throws Exception {
        String path = getClass().getResource("/").getPath();
        path = path.substring(0, path.lastIndexOf("/target/")) + "/src/";
        Set<String> set = new HashSet<>();
        scan(set, new File(path + "main"));
        for (String p : set)
            sql(p);
        sql(path + "test/sql/mock.sql");
        close();
    }

    private void scan(Set<String> set, File file) {
        if (file.isFile()) {
            if (file.getName().equals("create.sql"))
                set.add(file.getAbsolutePath());
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0)
                for (File f : files)
                    scan(set, f);
        }
    }

    private void sql(String path) throws Exception {
        File file = new File(path);
        if (!file.exists() && !file.isFile())
            return;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            if (validator.isEmpty(line))
                continue;

            sb.append(line);
            if (line.trim().endsWith(";")) {
                update(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        reader.close();
        close();
    }

    private void update(String sql) throws Exception {
        if (update == null)
            update = this.sql.getClass().getMethod("update", String.class, Object[].class);
        update.invoke(this.sql, sql, new Object[0]);
    }

    protected void close() {
        closables.close();
    }
}
