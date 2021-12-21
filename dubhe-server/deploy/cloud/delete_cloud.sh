#!/bin/bash
cd /Dubhe/dubhe-server/deploy/cloud
file=(`tree -fai . |grep .yaml`)

for i in ${file[*]};
  do
    echo "开始删除$i"
    echo "kubectl delete -f "$i" -n dubhe-prod"
    kubectl delete -f $i -n dubhe-prod
    echo "已删除$i"
done
