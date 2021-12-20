#!/bin/bash
cd /Dubhe/dubhe-server/deploy/cloud
file=(`tree -fai . |grep .yaml`)

for i in ${file[*]};
  do
    echo "开始部署$i"
    echo "kubectl apply -f "$i" -n dubhe-prod"
    kubectl apply -f $i -n dubhe-prod
    echo "已部署$i"
done
