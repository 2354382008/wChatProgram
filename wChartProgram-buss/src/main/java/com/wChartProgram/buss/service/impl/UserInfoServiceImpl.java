package com.wChartProgram.buss.service.impl;

import com.wChartProgram.buss.constroct.executor.BaseExecutor;
import com.wChartProgram.buss.service.UserInfoService;
import com.wChartProgram.model.dto.CommonResponseDto;
import com.wChartProgram.model.dto.UserInfoDto;
import org.springframework.stereotype.Component;

/**
 * 用户信息操作类
 * @author wmq
 * 调用某一个接口之前需要校验是否登录，是否有权限等等，调接口之前会先进到扩展器里把这个校验走完，然后
 */
@Component
public class UserInfoServiceImpl extends BaseExecutor<UserInfoDto, CommonResponseDto> implements UserInfoService {

    @Override
    protected CommonResponseDto executorHnadle(UserInfoDto request) {
        return null;
    }

    @Override
    public void volitedHandle(UserInfoDto request) {
        //todo 校验逻辑
    }

    @Override
    public void addUserInfo(UserInfoDto userInfoDto) {

    }

    @Override
    public int UndateUserInfo(UserInfoDto userInfoDto) {
        return 0;
    }
}
