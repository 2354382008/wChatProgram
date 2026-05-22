package com.wChartProgram.model.dto;

import lombok.Data;

/**
 * 登录公钥获取
 * @author wangmq
 */
@Data
public class LoginPublicKeyData {

  /** PEM，SPKI：-----BEGIN PUBLIC KEY----- */
  private String publicKeyPem;

  private String privateKeyPem;
}