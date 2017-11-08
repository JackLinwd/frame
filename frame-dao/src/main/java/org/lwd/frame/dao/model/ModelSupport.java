package org.lwd.frame.dao.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Model支持类，主键ID使用UUID。
 *
 * @author lwd
 */
@MappedSuperclass
public class ModelSupport implements Model {
    private static final String ID = "c_id";
    private static final String UUID = "uuid2";

    private String id;

    @Jsonable
    @Column(name = ID)
    @Id
    @GeneratedValue(generator = UUID)
    @GenericGenerator(name = UUID, strategy = UUID)
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
