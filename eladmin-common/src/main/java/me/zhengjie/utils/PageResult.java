package me.zhengjie.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T>
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private List<T> content;

    private long totalElements;

    private long currPage;

    private long pageSize;

    private long totalPage;

    public PageResult() {
        this.content = null;
        this.totalElements = 0;
        this.currPage = 0;
        this.pageSize = 0;
    }

    public PageResult(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public PageResult(List<T> list, long total, long currPage, long pageSize,long totalPage) {
        this.content = list;
        this.totalElements = total;
        this.currPage = currPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
    }

}
