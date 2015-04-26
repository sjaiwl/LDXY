# LDXY
“灵动校园”项目 （基于android客户端的学生社交平台）<br>
“一起去”是基于android平台的学生社交软件，致力于提供好的校园活动，拉近不同学生心灵之间距离的一款产品。<br>

[Download APK]()

## 功能架构图
![image]()
## 功能实现
* 主要功能就是发布活动和参与活动，通过手机端将活动参数提交到后台，增加活动记录。
  * 主要使用Volley框架的get和post方法，发送请求。
  * 支持活动上传图片。

* 支持活动的动态参与和评论
  * 活动的参与者能够收到服务器的推送消息，主要是来自活动的参与和评论。
  * 支持评价回复功能。

* 用户的登陆和活动的分享
  * 支持三方登陆和分享。
  * 短信验证和实名注册。

## 关于SmartImageView开源控件的使用
* smartImageVIew，支持从URL和通讯录中获取图像，可以替代Android标准的ImageView
  
  
