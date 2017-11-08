package org.lwd.frame.dao.auto;

import org.lwd.frame.dao.model.Jsonable;
import org.lwd.frame.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author lwd
 */
@Component(AutoModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = AutoModel.NAME)
@Table(name = "t_dao_auto")
public class AutoModel extends ModelSupport {
    static final String NAME = "frame.dao.auto";

    private String dataSource; // 数据源
    private String md5; // MD5值
    private String sql; // SQL
    private int state; // 状态：0-执行；1-失效
    private Timestamp time; // 时间

    @Jsonable
    @Column(name = "c_md5")
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Jsonable
    @Column(name = "c_data_source")
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Jsonable
    @Column(name = "c_sql")
    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Jsonable
    @Column(name = "c_state")
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Jsonable
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
