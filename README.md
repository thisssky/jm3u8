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
