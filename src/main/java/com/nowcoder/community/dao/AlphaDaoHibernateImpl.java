package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * Created by Paul Z on 2020/6/5
 */

@Repository("alphaHibernate")        //想要被Spring容器扫描，必须添加注解，Repository表示与数据库相关的bean对象
//注解后面加括号加字符串就是自定义这个Bean的名字，默认为类名首字母小写
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
