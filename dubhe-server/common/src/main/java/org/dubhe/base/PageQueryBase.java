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

package org.dubhe.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dubhe.constant.NumberConstant;

import javax.validation.constraints.Min;

/**
 * @description 分页基类
 * @date 2020-05-08
 */
@Data
@Accessors(chain = true)
public class PageQueryBase<T> {

    @ApiModelProperty(value = "分页-当前页数")
    @Min(value = 1, message = "current不能小于1")
    private Integer current;

    @ApiModelProperty(value = "分页-每页展示数")
    @Min(value = 1, message = "size不能小于1")
    private Integer size;


    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "排序方式,asc | desc")
    private String order;

    public Page<T> toPage() {
        Page<T> page = new Page();
        if (this.current != null) {
            page.setCurrent(this.current);
        }
        if (this.size != null && this.size < NumberConstant.MAX_PAGE_SIZE) {
            page.setSize(this.size);
        }
        return page;
    }

}
