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
  * 特征
  * 根据URL地址装载图像；
  * 支持装载通讯录中的图像；
  * 支持异步装载；
  * 支持缓存；
* 开源项目下载地址 
  * [Download](https://github.com/loopj/android-smart-image-view)

## 项目中遇到的问题
* 当需要在消息页中回复时，弹出键盘会把底部菜单栏顶上去的问题，以及弹出的edittext的如何监听返回键。
  * 这个在我的个人博客中有提到，可以[点击查看](http://blog.csdn.net/wlaizff/article/details/42103197)
  
  
