package com.wChartProgram.common.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * 获取一个私钥
 * @author wangmq
 */
public class PrivateKeyUtil {

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 密钥长度：2048、3072、4096
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成一个新的RSA私钥，并返回PEM格式字符串
     * @return
     * @throws Exception
     */
    public static String getPrivateKey() throws Exception {
        KeyPair keyPair = generateKeyPair();
        // 这就是你自己的私钥
        PrivateKey privateKey = keyPair.getPrivate();
        // 将私钥转为 PEM 格式字符串（方便存储或配置）
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(privateKey.getEncoded()) +
                "\n-----END PRIVATE KEY-----";
        return privateKeyPem;
    }

    /**
     * 从私钥PEM获取公钥PEM
     * @param privateKeyPem 私钥PEM字符串（PKCS#8格式）
     * @return 公钥PEM字符串（X.509格式）
     */
    public static String getPublicKeyPemFromPrivateKeyPem(String privateKeyPem) throws Exception {
        // 1. PEM → 私钥对象
        PrivateKey privateKey = pemToPrivateKey(privateKeyPem);

        // 2. 私钥对象 → 公钥对象
        PublicKey publicKey = getPublicKeyFromPrivateKey(privateKey);

        // 3. 公钥对象 → PEM字符串
        return publicKeyToPem(publicKey);
    }

    /**
     * 私钥对象 → 公钥对象
     */
    private static PublicKey getPublicKeyFromPrivateKey(PrivateKey privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                privateKeySpec.getModulus(),
                // RSA标准公钥指数
                BigInteger.valueOf(65537)
        );
        return keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * PEM → 私钥对象
     */
    private static PrivateKey pemToPrivateKey(String pem) throws Exception {
        String base64 = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 公钥对象 → PEM格式
     */
    private static String publicKeyToPem(PublicKey publicKey) {
        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        for (int i = 0; i < base64.length(); i += 64) {
            sb.append(base64, i, Math.min(i + 64, base64.length())).append("\n");
        }
        sb.append("-----END PUBLIC KEY-----");
        return sb.toString();
    }
}
