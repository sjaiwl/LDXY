# LDXY
“灵动校园”项目 （基于android客户端的学生社交平台）<br>
“一起去”是基于android平台的学生社交软件，致力于提供好的校园活动，拉近不同学生心灵之间距离的一款产品。<br>

[Download APK](https://github.com/sjaiwl/image_folder/blob/master/LDXY.apk)

## 功能架构图
![image](https://github.com/sjaiwl/image_folder/blob/master/LDXY/information.png)
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
* 对于时间格式的转换
  * 由于项目的后台使用的是rails框架，其默认的时间格式是UTC格式，需要在手机端进行转换显示
  * 转换代码
  ```
  public static String getLocalTimeFromUTC(String UTCTime) {
		if (UTCTime == "") {
			return "时间获取失败";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		UTCTime = UTCTime.replace("Z", " UTC");
		Date dt = null;
		try {
			dt = sdf.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar g = new GregorianCalendar();
		int minutetime = 0;
		minutetime = Math.abs((int) (dt.getTime() - g.getTimeInMillis()))
				/ (1000 * 60);
		if (minutetime <= 0) {
			return "刚刚";
		}
		if (minutetime > 0 && minutetime < 60) {
			return minutetime + "分钟前";
		}
		if (minutetime >= 60 && minutetime < 60 * 24) {
			return (int) minutetime / 60 + "小时前";
		} else {
			return (int) minutetime / (60 * 24) + "天前";
		}
	}
  ```
* 怎么在手机端保存用户的信息？
  * 一种是采用SharedPreferences保存用户信息
  ```
  SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        userName = username.getText().toString();
        passWord = password.getText().toString();
        editor.putString("UserName", userName);
        editor.putString("PassWord", passWord);
        editor.commit();
  ```
  * 另一种是采用静态类的方法，保存user信息
  ```
  public class UserInfo implements Serializable {
    public static UserInfo user = null;
    public Integer doctor_id;
    public String doctor_name;
    ...
    ...
    ```
  * 项目主要采用静态类的方式，主要是通过gson解析用户的json数据，维护也更方便。
  ```
  Gson gson = new Gson();
  userInfo = gson.fromJson(response.toString(), UserInfo.class);
  ```
## 参考资料:
##### [android的开源项目 (里面有很多很好的控件，很棒的界面效果)](https://github.com/Trinea/android-open-project)
##### [常用的jar包](https://github.com/sjaiwl/LDXY/tree/master/libs)
##### [短信验证以及分享平台](http://sms.mob.com/Download)

