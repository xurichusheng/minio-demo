https://www.cnblogs.com/daikainan/p/14413759.html

# 1. windows（单机）
## 1.1 下载
https://dl.minio.io/server/minio/release/windows-amd64/minio.exe

## 1.2 启动命令
minio.exe server G:\   （G:\ 表示目录）

## 1.3 控制台
http://localhost:9000

用户名、密码：

minioadmin  minioadmin

wjh jhwen123

## 1.4 设置永久链接

（1）下载 minio client
https://dl.min.io/client/mc/release/windows-amd64/mc.exe

（2）cmd窗口先后执行命令

mc config host add minio http://127.0.0.1:9000 minioadmin minioadmin S3v4

mc policy public minio/桶名

mc policy set download minio/桶名

（3）文件下载示例

http://localhost:9000/miniobuckets/1642487198309.jpg

## 1.5 权限分配

参考：https://blog.csdn.net/u012017645/article/details/116116468

（1）创建用户

cmd窗口执行命令：

mc admin user add minio/ 用户名 密码

如：mc admin user add minio/ wjh jhwen123

（2）设置权限规则

在1.2 启动命令的目录下，创建 json 文件，文件名称：桶名.json，如 miniobuckets.json

内容如下：

{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "s3:*"
            ],
            "Effect": "Allow",
            "Resource": [
                "arn:aws:s3:::miniobuckets/*"
            ],
            "Sid": ""
        }
    ]
}

说明：

Version：默认是 2012-10-17

Action：是运行的动作，这里给的权限是，获取桶列表，对对象有增删拿的操作。

arn:aws:s3::: 后面的桶名记得修改

miniobuckets/*：表示权限作用于miniobuckets这个桶下的所有数据。


（3）为桶设置规则

cmd窗口执行命令：

mc admin policy add minio 桶名rule 桶名.json

如：mc admin policy add minio miniobucketsrule miniobuckets.json

（4）为用户分配权限

mc admin policy set minio miniobucketsrule user=用户名

如：mc admin policy set minio miniobucketsrule user=wjh



登录控制台（http://localhost:9000）可查看到新增的用户和用户权限。



## 1.6 页面展示

http://localhost:8181/login.html

默认登录名/密码：minioadmin/minioadmin

# 2. Linux（集群）

操作系统：CentOS-7-x86_64-Everything-2009

详见 /src/main/resources/docs/CentOS  Minio集群安装.docx
