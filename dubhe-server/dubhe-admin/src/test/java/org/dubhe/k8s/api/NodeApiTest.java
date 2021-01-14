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

package org.dubhe.k8s.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.dubhe.k8s.constant.K8sParamConstants;
import org.dubhe.k8s.domain.resource.BizNode;
import org.dubhe.k8s.enums.LackOfResourcesEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description NodeApiTest测试类
 * @date 2020-04-22
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class NodeApiTest {
    @Resource
    private NodeApi nodeApi;

    @Test
    public void get() {
        BizNode node = nodeApi.get("node1");
        System.out.println("BizNode = " + JSON.toJSONString(node));
    }

    @Test
    public void listAll() {
        List<BizNode> list = nodeApi.listAll();
        System.out.println("listAll = " + JSON.toJSONString(list));
    }

    @Test
    public void addLabel() {
        System.out.println("addLabel=" + nodeApi.addLabel("node1", "gpu", "gpu"));
    }

    @Test
    public void addLabels() {
        Map<String, String> labels = new HashMap<String, String>(16) {
            {
                put("node1", "label1");
            }
        };
        System.out.println("addLabel=" + nodeApi.addLabels("node1", labels));
    }

    @Test
    public void deleteLabel() {
        System.out.println("addLabel=" + nodeApi.deleteLabel("node1", "label1"));
    }

    @Test
    public void deleteLabels() {
        Set<String> labels = new HashSet<String>() {
            {
                add("label1");
                add("label2");
                add("label3");
            }
        };
        System.out.println("addLabel=" + nodeApi.deleteLabels("node1", labels));
    }

    @Test
    public void schedulable() {
        System.out.println("schedulable=" + nodeApi.schedulable("node1", false));
    }

    @Test
    public void isAllocatable(){
        LackOfResourcesEnum flag;
        flag = nodeApi.isAllocatable(10000,300000 ,30);
        System.out.println(flag.getMessage());
    }

    @Test
    public void isOutOfTotalAllocatableGpu(){
        System.out.println(nodeApi.isOutOfTotalAllocatableGpu(3));
    }
}
