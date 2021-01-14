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

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @description 统一的公共响应体
 * @date 2020-10-09
 */
@Data
public class DataResponseBody<T> implements Serializable {

	/**
	 * 返回状态码
	 */
	private Integer code;
	/**
	 * 返回信息
	 */
	private String msg;
	/**
	 * 泛型数据
	 */
	private T data;

	public DataResponseBody() {
		this(HttpStatus.OK.value(), null);
	}

	public DataResponseBody(T data) {
		this(HttpStatus.OK.value(), null, data);
	}

	public DataResponseBody(Integer code, String msg) {
		this(code, msg, null);
	}

	public DataResponseBody(Integer code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

}