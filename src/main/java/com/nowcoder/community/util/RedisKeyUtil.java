package com.nowcoder.community.util;

/**
 * Created by Paul Z on 2020/7/2
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";   //目标，我关注的人

    private static final String PREFIX_FOLLOWER = "follower";   //粉丝，关注我的人

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";

    //某个实体的赞的那个Key该怎样生成
    //key(like:entity:entityType:entityId)
    //value(可以设置成集合，里面存的是点赞人的userId，这样以后可以看到谁给我点了赞)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //关注的是实体（可以是用户、帖子、题目等）
    //key(形式为followee:userId:entityType，表明关注人，区分关注实体的类型)
    //value(采用有序集合zset，对象是entityId，分数是时间整数)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId->zset(userId, time)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //登录验证码的Key
    //参数owner表示用来标识登录用户的临时凭证
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}
