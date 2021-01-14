/**
 * Copyright 2020 Tianshu AI Platform. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =============================================================
 */
package org.dubhe.async;

import cn.hutool.core.util.StrUtil;
import org.dubhe.base.ResponseCode;
import org.dubhe.config.TrainHarborConfig;
import org.dubhe.dao.PtImageMapper;
import org.dubhe.domain.entity.PtImage;
import org.dubhe.enums.ImageStateEnum;
import org.dubhe.enums.LogEnum;
import org.dubhe.exception.BusinessException;
import org.dubhe.harbor.api.HarborApi;
import org.dubhe.utils.IOUtil;
import org.dubhe.utils.LogUtil;
import org.dubhe.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @description 异步推送镜像到harbor
 * @date 2020-07-10
 */
@Component
public class HarborImagePushAsync {

    @Autowired
    private TrainHarborConfig trainHarborConfig;

    @Autowired
    private HarborApi harborApi;

    @Autowired
    private PtImageMapper ptImageMapper;

    /**
     * 组装shell脚本执行命令
     *
     * @param imagePath
     * @param imageNameandTag
     **/
    @Async
    public void execShell(String imagePath, String imageNameandTag, PtImage ptImage) {
        try {
            String imageResource = trainHarborConfig.getAddress() + StrUtil.SLASH + ptImage.getProjectName()
                    + StrUtil.SLASH + imageNameandTag;
            String cmdStr = "docker login --username=" + trainHarborConfig.getUsername() + " " + trainHarborConfig.getAddress() + " --password=" + trainHarborConfig.getPassword() + " ; docker " +
                    "load < " + imagePath + " |awk '{print $3}' |xargs -I str docker tag str " + imageResource + " ; docker push " + imageResource + "; docker rmi " + imageResource;
            String[] cmd = {"/bin/bash", "-c", cmdStr};
            LogUtil.info(LogEnum.BIZ_TRAIN, "镜像上传执行脚本参数:{}", cmd);

            Process process = Runtime.getRuntime().exec(cmd);
            if (checkImagePushIsOk(ptImage, process)) {
                updateImageStatus(ptImage, ImageStateEnum.SUCCESS.getCode());
            } else {
                updateImageStatus(ptImage, ImageStateEnum.FAIL.getCode());
            }
        } catch (Exception e) {
            LogUtil.error(LogEnum.BIZ_TRAIN, "上传镜像异常:{}", e);
            updateImageStatus(ptImage, ImageStateEnum.FAIL.getCode());
            throw new BusinessException("上传镜像异常!");

        }
    }

    /**
     * 更新镜像上传状态
     *
     * @param ptImage
     * @param status
     * @return java.lang.Integer
     **/
    public Integer updateImageStatus(PtImage ptImage, Integer status) {
        ptImage.setImageStatus(status);
        ptImageMapper.updateById(ptImage);
        return ResponseCode.SUCCESS;
    }


    /**
     * 校验镜像是否上传成功
     *
     * @param ptImage 镜像信息
     * @param process process对象
     * @return 是否上传成功
     */
    public boolean checkImagePushIsOk(PtImage ptImage, Process process) {
        //读取标准输出流
        BufferedReader brOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //读取标准错误流
        BufferedReader brErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        StringBuilder outMessage = new StringBuilder();
        StringBuilder errMessage = new StringBuilder();
        boolean isPushOk = true;
        try {
            while ((line = brOut.readLine()) != null) {
                outMessage.append(line);
            }
            if (StringUtils.isNotEmpty(outMessage)) {
                LogUtil.info(LogEnum.BIZ_TRAIN, "shell上传镜像输出信息:{}", outMessage.toString());
            }
            while ((line = brErr.readLine()) != null) {
                errMessage.append(line);
            }
            if (StringUtils.isNotEmpty(errMessage)) {
                LogUtil.error(LogEnum.BIZ_TRAIN, "shell上传镜像异常信息:{}", errMessage.toString());
            }
            Integer status = process.waitFor();
            LogUtil.info(LogEnum.BIZ_TRAIN, "上传镜像状态:{}", status);
            if (status == null) {
                if (!harborApi.isExistImage(ptImage.getImageUrl())) {
                    isPushOk = false;
                }
            } else if (status != 0) {
                isPushOk = false;
            }
        } catch (Exception e) {
            LogUtil.error(LogEnum.BIZ_TRAIN, "上传镜像异常:{}", e);
            return false;
        } finally {
            IOUtil.close(brErr, brOut);
        }
        return isPushOk;
    }
}
