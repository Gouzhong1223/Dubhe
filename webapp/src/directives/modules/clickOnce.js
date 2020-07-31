/** Copyright 2020 Zhejiang Lab. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* =============================================================
*/

export default {
  inserted(el, binding) {
    el.addEventListener('click', () => {
      if (!el.disabled) {
        let disabledTime = 1000;
        el.disabled = true;
        if (binding.value !== undefined) {
          if (binding.value.time !== undefined) {
            const time = Number(binding.value.time);
            if (!isNaN(time) && time > 0) {
              disabledTime = time;
            }
          }
        }
        setTimeout(() => {
          el.style.cursor = 'pointer';
          el.disabled = false;
        }, disabledTime);
      }
    });
  },
};
