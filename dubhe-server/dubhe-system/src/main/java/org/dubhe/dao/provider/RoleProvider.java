/**
 * Copyright 2020 Zhejiang Lab. All Rights Reserved.
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

package org.dubhe.dao.provider;

import java.util.Map;
/**
 * @description  角色构建类
 * @date 2020-04-15
 */
public class RoleProvider {
    public String findRolesByUserId(Long userId) {
        StringBuffer sql = new StringBuffer("select r.* from role r, users_roles ur ");
        sql.append(" where ur.user_id=#{userId} ");
        sql.append(" and ur.role_id=r.id");
        sql.append(" and r.deleted = 0");
        return sql.toString();
    }

    public String findByUserIdAndTeamId(Map<String, Object> para) {
        StringBuffer sql = new StringBuffer("select r.* from role r, teams_users_roles tur ");
        sql.append(" where tur.user_id=#{userId} ");
        sql.append(" and tur.team_id=#{teamId} ");
        sql.append(" and tur.role_id=r.id");
        sql.append(" and r.deleted = 0");
        return sql.toString();
    }
}
