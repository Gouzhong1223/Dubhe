#!/bin/bash
cd /Dubhe/dubhe-server
host_ip=`hostname -I  |awk '{print $1}'`
change_ip=${change_ip:-${host_ip}}
file=(`tree -fai . |grep .yaml`)
cat /root/.kube/config > common-k8s/src/main/resources/kubeconfig

sed -i 's/validateJupyterUrl(url)/url/g' common-k8s/src/main/java/org/dubhe/k8s/api/impl/PodApiImpl.java

for i in ${file[*]};
  do
    echo "开始修改$i"
    sed -ri 's/server-addr: 127.0.0.1:8848/server-addr: '${change_ip}':8848/g'  ${i}
    echo "已修改$i"
done

cd /Dubhe/dubhe-server/yaml

file=(`tree -fai . |grep .yaml`)

for i in ${file[*]};
  do
    echo "开始修改$i"
    sed -ri 's/(\b[0-9]{1,3}\.){3}[0-9]{1,3}\b'/${change_ip}/g  ${i}
    sed -ri 's/localhost'/${change_ip}/g  ${i}
    sed -ri 's/kubeconfig: kubeconfig_test/kubeconfig: kubeconfig/g'  ${i}
    sed -ri 's/dubhe-cloud-test/dubhe-cloud-prod/g'  ${i}
    sed -ri 's/database:.*/database: 0/g'  ${i}
    sed -ri 's/serverPort: 9200/serverPort: 32321/g'  ${i}
    sed -ri 's/transportPort: 9300/transportPort: 32322/g'  ${i}
    sed -ri 's/index: dataset_text_test/index: dataset_text/g'  ${i}
    sed -ri 's/clusterName: docker-cluster/clusterName: kubernetes-logging/g'  ${i}
    sed -ri 's/hostlist:.*/hostlist: '${change_ip}':32321/g'  ${i}
    sed -ri 's/server-addr: 127.0.0.1:8848/server-addr: '${change_ip}':8848/g'  ${i}
    sed -ri 's/logback-spring-dev.xml/logback.xml/g'  ${i}
    sed -ri 's/username: test/username: root/g'  ${i}
    sed -ri 's/password: test/password: 123456/g'  ${i}
    sed -ri 's/bucketName:.*/bucketName: dubhe-prod/g'  ${i}
    sed -ri 's/harbor.dubhe.ai/harbor.dubhe.com/g'  ${i}
    sed -ri 's/tls-crt:.*/tls-crt: '$tls_crt'/g'  ${i}
    sed -ri 's/tls-key:.*/tls-key: '$tls_key'/g'  ${i}
    sed -ri 's/flag:.*/flag: false/g'  ${i}
    sed -ri 's/http-port:.*/http-port: "'${k8s_port}'"/g'  ${i}
    echo "已修改$i"
done

