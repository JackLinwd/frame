package org.lwd.frame.dao.orm;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.dao.model.Model;

import java.util.List;

/**
 * @author lwd
 */
public interface PageList<T extends Model> {
    /**
     * 设置分页信息。
     *
     * @param count  记录总数。
     * @param size   每页显示记录数。
     * @param number 当前显示页码数。
     */
    void setPage(int count, int size, int number);

    /**
     * 获取记录总数。
     *
     * @return 记录总数。
     */
    int getCount();

    /**
     * 获取每页最大显示记录数。
     *
     * @return 每页最大显示记录数。
     */
    int getSize();

    /**
     * 获取当前显示页数。
     *
     * @return 当前显示页数。
     */
    int getNumber();

    /**
     * 获取总页数。
     *
     * @return 总页数。
     */
    int getPage();

    /**
     * 获取分页显示起始页数。
     *
     * @return 起始页数。
     */
    int getPageStart();

    /**
     * 获取分页显示结束页数。
     *
     * @return 结束页数。
     */
    int getPageEnd();

    /**
     * 获取数据集。
     *
     * @return 数据集。
     */
    List<T> getList();

    /**
     * 设置数据集。
     *
     * @param list 数据集。
     */
    void setList(List<T> list);

    /**
     * 转化为JSON格式的数据。
     *
     * @return JSON格式的数据。
     */
    JSONObject toJson();

    /**
     * 转化为JSON格式的数据。
     *
     * @param listable 是否包含列表数据。
     * @return JSON格式的数据。
     */
    JSONObject toJson(boolean listable);
}
