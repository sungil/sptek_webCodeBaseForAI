package com._sptek._webFrameworkExample.unit.responseError;

import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek.__webFramework.data.mybatis.MyBatisCommonDao;
import com.cesco.__projectsCommon.commonObject.code.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service

public class ResponseErrorService {

    private final MyBatisCommonDao myBatisCommonDao;

    public boolean isAvailableId_badExample(String userId) throws ServiceException {
        // int userIdCount = myBatisCommonDao.selectOne("userIdCount", userId); --> 이런 처리가 있다고 가정
        int userIdCount = 1;
        return userIdCount <= 0;
    }

    public boolean isAvailableId_goodExample(String userId) throws ServiceException {
        //int userIdCount = myBatisCommonDao.selectOne("userIdCount", userId); --> 이런 처리가 있다고 가정
        int userIdCount = 1;

        // 서비스 로직상 더이상 추가 처리가 불가능 할 경우 그 위치 에서 바로 ServiceException 를 통해 response 한다.
        if (userIdCount > 0) {
            throw new ServiceException(ServiceErrorCodeEnum.ALREADY_EXIST_RESOURCE_ERROR, userId + " 는 사용할 수 없는 아이디 입니다..");
        }
        return userIdCount <= 0;
    }
}
