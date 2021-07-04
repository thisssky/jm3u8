# **Java版M3U8下载器**



## what

### 1.下载m3u8格式视频，并转换成MP4

### 2.支持加密格式合并



## 环境

### 1.安装jdk

### 2.安装FFMPEG



## **使用**



1.文件夹下的extinf.xml文件不可删除，未下载完毕，将此文件拖入下载区可接着下载

 2.如果是加密视频文件夹下的key.key文件，cindex.m3u8文件不可删除，合并文件时需要使用

3.未下载完成不要删除extinf.xml,将该文件拖入下载区可继续下载未完成部分。如果是加密格式视频cindex.m3u8文件也不能删除

 4.将extinf.xml拖入列表区可将未下载完成的继续下载

 5.将下载链接按行写入m3u8.txt文件中，填写下载地址，将该文件拖入保存路径区即可批量添加下载任务

 6.使用firefox浏览器HLS插件，可自动下载。下载地址：https://addons.mozilla.org/zh-CN/firefox/addon/hls/

 7.在windows平台中需要将m3u8.jar文件，m3u8.json文件和m3u8.bat文件放在同一文件夹，修改m3u8.bat文件中的路径

 8.在windows平台中添加注册表，HKEY_CURRENT_USER\Software\Mozilla\NativeMessagingHosts\m3u8,值为m3u8.json文件路径，C:\Users\xxx\Desktop\xxx\m3u8.json

9.创建m3u8.bat文件，文件内容如下,地址根据自己需求修改

@echo off  
java -jar C:\Users\xxx\Desktop\xxx\m3u8.jar

10.创建m3u8.json文件，文件内容如下

{  
"name": "m3u8",  
 "description": "HLS native messaging",  
  "path": "m3u8.bat",   
  "type": "stdio",  
 "allowed_extensions": [ "hls@hls.org" ]  
}




配置
清单路径
在 Linux 和 Mac OS X 中，你需要将清单文件存在特定的位置。在 Windows 中，你需要创建一个注册表来指向清单文件。

所有类型的清单的详细规则都是相同的，除了倒数第二个的 type 字段表示了清单的类型。下面的例子展示了三种不同类型的清单。在例子中，<name> 代表清单中的 name 字段值。

Windows
如果想要全局可见，使用下面的路径创建注册表：

HKEY_LOCAL_MACHINE\SOFTWARE\Mozilla\NativeMessagingHosts\<name>

HKEY_LOCAL_MACHINE\SOFTWARE\Mozilla\ManagedStorage\<name>

HKEY_LOCAL_MACHINE\SOFTWARE\Mozilla\PKCS11Modules\<name>
注册表应该有单个默认值，值里存放“到清单文件的路径”。比如为原生应用通信清单建立的注册表差不多是这样：

为原生应用通信清单建立的注册表

对于原生应用清单，即使原生应用是32位的，也不能在 Wow6432Node 下创建注册表。浏览器将总会在 native 视图下寻找注册表的，而不是32位放在环境。确保注册表的创建在原生视图中，你可以键入KEY_WOW64_64KEY 或 KEY_WOW64_32KEY  到 RegCreateKeyEx。请参考：Accessing an Alternate Registry View

如果想要用户级别的可见，使用下面的路径创建注册表：

HKEY_CURRENT_USER\SOFTWARE\Mozilla\NativeMessagingHosts\<name>

HKEY_CURRENT_USER\SOFTWARE\Mozilla\ManagedStorage\<name>

HKEY_CURRENT_USER\SOFTWARE\Mozilla\PKCS11Modules\<name>
注册表应该有单个默认值，值里存放“到清单文件的路径”。

Mac OS X
如果想要全局可见，将清单文件存放在：

/Library/Application Support/Mozilla/NativeMessagingHosts/<name>.json

/Library/Application Support/Mozilla/ManagedStorage/<name>.json

/Library/Application Support/Mozilla/PKCS11Modules/<name>.json
如果想要用户级别的可见，将清单文件存放在：

~/Library/Application Support/Mozilla/NativeMessagingHosts/<name>.json

~/Library/Application Support/Mozilla/ManagedStorage/<name>.json

~/Library/Application Support/Mozilla/PKCS11Modules/<name>.json
Linux
如果想要全局可见，将清单文件存放在：

/usr/lib/mozilla/native-messaging-hosts/<name>.json

/usr/lib/mozilla/managed-storage/<name>.json

/usr/lib/mozilla/pkcs11-modules/<name>.json
或者：

/usr/lib64/mozilla/native-messaging-hosts/<name>.json

/usr/lib64/mozilla/managed-storage/<name>.json

/usr/lib64/mozilla/pkcs11-modules/<name>.json
如果想要用户级别的可见，将清单文件存放在：

~/.mozilla/native-messaging-hosts/<name>.json

~/.mozilla/managed-storage/<name>.json

~/.mozilla/pkcs11-modules/<name>.json
