package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul Z on 2020/6/24
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //定义一个用于替换敏感词的符号
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct   //表示Spring容器实例化SensitiveFilter类之后调用该初始化方法
    public void init(){
        //服务器启动的时候执行该方法
        //首先读sensitive-words文件的内容
        //通过类加载器从类路径下去读取资源
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt"); //得到字节流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is)) //字节流转字符流再转缓冲流
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        }
        catch (IOException e) {
            logger.error("加载敏感词失败：" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中去
    private void addKeyword(String keyword) {
        TrieNode tmpNode = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if (subNode == null){
                subNode = new TrieNode();
                tmpNode.addSubNode(c, subNode);
            }
            tmpNode = subNode;

            //设置结束标识
            if (i == keyword.length()-1){
                tmpNode.setKeyEnd(true);
            }
        }
    }

    //过滤敏感词的方法,text为待检测的文本
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tmpNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (begin < text.length()){
            if (position < text.length()) {
                char c = text.charAt(position);
                //跳过符号
                if (isSymbol(c)){
                    //若指针1处于根节点
                    if (tmpNode == rootNode){
                        sb.append(c);
                        begin++;
                    }
                    //无论符号在开头或中间，指针3都向下走一步
                    position++;
                    continue;
                }

                //检查下级节点
                tmpNode = tmpNode.getSubNode(c);
                if (tmpNode == null){
                    //以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    position = ++begin;
                    tmpNode = rootNode;
                }
                else if (tmpNode.isKeyEnd()){
                    sb.append(REPLACEMENT);
                    begin = ++position;
                    tmpNode = rootNode;
                }
                else {
                    //检查下一个字符
                    position++;
                }
            }
            else {
                sb.append(text.charAt(begin));
                position = ++begin;
                tmpNode = rootNode;
            }
        }

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c){
        //CharUtils.isAsciiAlphanumeric(c)判断c是否是1-9|a-z|A-Z这些字符
        //0x2e80-0x9FFFF表示东亚文字的范围，即这些不是特殊符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c >0x9FFF);
    }

    //定义前缀树的数据结构
    private class TrieNode {

        //关键词结束的标识
        private boolean isKeyEnd = false;

        public boolean isKeyEnd() {
            return isKeyEnd;
        }

        public void setKeyEnd(boolean keyEnd) {
            isKeyEnd = keyEnd;
        }

        //定义子节点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }


}
