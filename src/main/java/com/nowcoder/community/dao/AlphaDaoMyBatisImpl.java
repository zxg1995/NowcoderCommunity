package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Created by Paul Z on 2020/6/5
 */
@Repository
@Primary     //赋予这个Bean对象在容器中更高的优先级
public class AlphaDaoMyBatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "MyBatis";
    }
}
