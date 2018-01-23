# 上传多文件

请求
- Service Key - frame.ctrl.uploads
- URI - /frame/ctrl/uploads

参数
```json
[
    {
        "fieldName": "域名称，须与监听器KEY相同",
        "fileName": "文件名",
        "contentType": "文件Content-Type",
        "base64": "BASE64编码后的数据"
    }
]
```

返回
```
[
    {
        "success": "true/false",
        "fieldName": "域名称，须与监听器KEY相同",
        "fileName": "文件名",
        "path": "上传文件保存路径，成功时返回",
        "thumbnail": "缩略图文件保存路径，成功且上传文件为符合缩略图设置的图片时返回",
        "message": "错误信息，失败时返回"
    }
]
```

> 自定义配置参考[文件上传](upload.md)说明。
