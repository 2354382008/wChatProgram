# 登录接口说明

## 接口列表

### 1. 用户登录

**请求地址**: `POST /wcProgram-request/auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "token": "a1b2c3d4e5f6g7h8i9j0",
  "userInfo": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "phone": "13800138000",
    "email": "admin@example.com"
  }
}
```

### 2. 用户登出

**请求地址**: `POST /wcProgram-request/auth/logout`

**请求头**:
```
Authorization: a1b2c3d4e5f6g7h8i9j0
```

**响应示例**:
```json
"登出成功"
```

## 数据库初始化

执行 `init_user_table.sql` 文件来创建用户表并插入测试数据。

**测试账号**:
- 用户名: `admin` / 密码: `123456`
- 用户名: `test` / 密码: `123456`

## 功能说明

1. **密码加密**: 使用MD5加密存储用户密码
2. **Token管理**: 生成UUID作为token，存储在Redis中，过期时间为24小时
3. **用户状态验证**: 只有状态为启用（status=1）的用户才能登录
4. **白名单配置**: 登录接口已添加到白名单，无需认证

## 技术栈

- Spring Boot 2.4.8
- MyBatis Plus
- Redis
- Lombok
- Validation

## 目录结构

```
wChartProgram-buss/
├── controller/
│   └── LoginController.java       # 登录控制器
├── service/
│   ├── UserService.java           # 用户服务接口
│   └── impl/
│       └── UserServiceImpl.java   # 用户服务实现
└── mapper/
    └── UserMapper.java            # 用户Mapper

wChartProgram-model/
├── entity/
│   └── User.java                  # 用户实体类
└── dto/
    ├── LoginRequest.java          # 登录请求DTO
    └── LoginResponse.java         # 登录响应DTO
```
