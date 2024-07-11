package com.wz.pilipili.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * UUID生成类
 */
public class UUIDGenerator {


    /**
     * 根据文件的md5值生成UUID
     * @param fileMD5
     * @return
     */
    public static String generateUUID(String fileMD5) {
        try {
            // 将MD5和随机数结合
            String input = fileMD5 + UUID.randomUUID();
            // 获取SHA-256消息摘要
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // 使用前16个字节构建UUID
            ByteBuffer buffer = ByteBuffer.wrap(hash);
            long mostSigBits = buffer.getLong();
            long leastSigBits = buffer.getLong();

            return new UUID(mostSigBits, leastSigBits).toString();
        } catch (Exception e) {
            throw new RuntimeException("生成UUID时出错", e);
        }
    }
}
