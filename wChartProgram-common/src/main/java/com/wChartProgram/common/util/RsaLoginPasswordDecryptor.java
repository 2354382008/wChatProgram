package com.wChartProgram.common.util;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/**
 * 与前端 Web Crypto {@code RSA-OAEP}（导入公钥时 {@code hash: SHA-256}）对齐的登录密文解密。
 *
 * <p>前端请求体字段：{@code passwordCipher}（Base64）；后端在验密前调用 {@link #decryptUtf8(String)} 得到明文密码。
 *
 * <p>公钥下发：GET {@code /api/wChat/auth/login/public-key}，响应 data 中带 PEM（{@code -----BEGIN PUBLIC KEY-----}，
 * SPKI / SubjectPublicKeyInfo）。私钥仅放服务端（环境变量或密钥管理服务），切勿下发。
 *
 * <p>生成 2048 位密钥对（示例，私钥用 PKCS#8 PEM）：
 *
 * <pre>{@code
 * openssl genrsa -out rsa_private_pkcs1.pem 2048
 * openssl pkcs8 -topk8 -inform PEM -in rsa_private_pkcs1.pem -out rsa_private.pem -nocrypt
 * openssl rsa -in rsa_private_pkcs1.pem -pubout -out rsa_public.pem
 * }</pre>
 */
public final class RsaLoginPasswordDecryptor {

  private static final String CIPHER = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
  private static final OAEPParameterSpec OAEP_SPEC =
      new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

  private final PrivateKey privateKey;

  public RsaLoginPasswordDecryptor(PrivateKey privateKey) {
    this.privateKey = privateKey;
  }

  /**
   * 从 PKCS#8 PEM（{@code -----BEGIN PRIVATE KEY-----}）构造。
   *
   * @param pkcs8PemPrivateKey PEM 文本
   */
  public static RsaLoginPasswordDecryptor fromPkcs8Pem(String pkcs8PemPrivateKey) throws Exception {
    String body =
        pkcs8PemPrivateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
    byte[] der = Base64.getDecoder().decode(body);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return new RsaLoginPasswordDecryptor(kf.generatePrivate(spec));
  }

  /**
   * 将前端 Base64 密文解密为 UTF-8 字符串（明文密码）。
   *
   * @param passwordCipherBase64 前端 {@code passwordCipher}
   * @return 明文密码
   */
  public String decryptUtf8(String passwordCipherBase64) throws Exception {
    byte[] cipherBytes = Base64.getDecoder().decode(passwordCipherBase64);
    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, privateKey, OAEP_SPEC);
    byte[] plain = cipher.doFinal(cipherBytes);
    return new String(plain, StandardCharsets.UTF_8);
  }
}
