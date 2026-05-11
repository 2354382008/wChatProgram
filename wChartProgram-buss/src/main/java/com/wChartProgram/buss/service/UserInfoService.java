package com.wChartProgram.buss.service;

import com.wChartProgram.model.dto.UserInfoDto;

public interface UserInfoService {

    void addUserInfo(UserInfoDto userInfoDto);

    int UndateUserInfo(UserInfoDto userInfoDto);

}
