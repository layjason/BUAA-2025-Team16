# 关于拦截器和token

如果你的接口需要调用拦截器验证token的话（如发布帖子时需要通过token获得userId），那么你需要

- 首先，在`config/InterceptorConfig.java`文件下，修改`addInterceptors`方法，将`.addPathPattern()`的括号中，仿照已有的值，向其中添加你的接口，表示这个接口被调用时会启用拦截器
- 拦截器会验证token并提取其中包含的userId，放到request的attribute中，因此，如果你的接口被拦截了，可以通过`String userId = (String) request.getAttribute("userId");`直接获取token中的userId信息，
- 你的接口可以通过`request.getHeader("token");`来获得token，当然一般不需要这么做，因为拦截器已经提取到了其中的有效信息
- 我们的从token中提取的userId是广义上的userId，包括管理员Id和用户Id，通过与10000000的比较，确定其是用户还是管理员。
- 在对这个接口测试的时候，你可能需要在header里添加token以及token的值，这个值可以通过调用login接口来获得，也就是包含当前登录用户信息的token值
- 如果你觉着先登录获取token的方法过于复杂，你可以将token设置为"woshiguanliyuan"或"woshiyonghu"，使用前者，拦截器识别后会将userId设置为"2"，使用后者，会设置为"10000003"；

目前生成token的签名为“123456”，因为我暂时不会动态签名。

# Note

- 在pom.xml中引入了支持邮件的依赖和支持JWT的依赖

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
  	<artifactId>spring-boot-starter-mail</artifactId>
  </dependency>
  <dependency>
  	<groupId>com.auth0</groupId>
  	<artifactId>java-jwt</artifactId>
  	<version>3.8.3</version>
  </denpendency>
  ```

- 在pom.xml中引入了支持上传文件的阿里云依赖

  ```xml
  <dependency>
			<groupId>com.aliyun.oss</groupId>
			<artifactId>aliyun-sdk-oss</artifactId>
			<version>3.10.2</version>
		</dependency>
  ```

- 在application.properties中配置邮件服务

  ```properties
  # 配置邮件服务器的地址
  spring.mail.host=smtp.qq.com
  # 配置邮件服务器的端口（465或587）
  spring.mail.port=465
  # 配置用户的账号
  spring.mail.username=1415596561@qq.com
  # 配置用户的密码（即上面我们申请到的授权码）
  spring.mail.password=lvfjzbwjqkcdiage
  # 配置默认编码
  spring.mail.default-encoding=UTF-8
  # SSL 连接配置
  spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
  # 开启 debug，这样方便开发者查看邮件发送日志
  #我给关了--from CH
  spring.mail.properties.mail.debug=false
  ```

- 在application.properties中配置上传文件服务

```properties
aliyun.endpoint=http://oss-cn-beijing.aliyuncs.com
aliyun.accessKeyId=LTAI5tHtdxjAYKrfoPBiMzMa
aliyun.accessKeySecret=RspPwVECB2RyZTBmlFArGnfQxcJNEv
#?????Object???????
aliyun.bucketName=learning-and-living
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.max-file-size=10MB
```

- 在application.yaml中配置上传文件服务

```yaml
  aliyun:
    oss:

      endpoint: oss-cn-beijing.aliyuncs.com

      accessKeyId: LTAI5tHtdxjAYKrfoPBiMzMa

      accessKeySecret: RspPwVECB2RyZTBmlFArGnfQxcJNEv

      bucketName: learning-and-living

      maxSize: 10
```


# UserController

- 完成了verifyAdminLogin()函数，用于验证管理员登录，成功返回RestBean\<UserDisplay>,失败时UserDisplay为null
  - POST	
  - 接口为/user/adminLogin
  
- 完成getSalt()，用于获取用户/管理员盐值，登陆时，前端应该分两步，首先将用户id或email发给后端，然后后端返回此用户/管理员的盐值，然后前端再将用户输入的密码加盐加密，再调用login接口或adminLogin，将加密后的密码同id或email发给后端
  - POST
  - 若此用户不存在，后端将返回一个随机的字符串作为盐值，避免攻击者判断某用户/管理员是否存在
  - /user/getSalt

- 完成了verifyUserLogin()函数，用于验证用户登录，同时更新登录日志，增加经验（尚未完成）成功返回RestBean\<UserDisplay>,失败时UserDisplay为null
  - POST
  - 接口为/user/login

- 完成了getUserInfo()，用于获取用户详细信息，使用token，用户只能得到自己的信息，管理员暂不能调用此方法
  - GET
  - 接口为/user/userInfo

- 完成了verifyUserRegister()，用于用户注册（邮箱+密码+盐值）,用户名为user+id，如user10000001
  - 盐值由前端随机生成，然后加密再传过来，加密的算法应该前后端一致，目前后端采用DigestUtils.*sha256Hex*(password);进行加密
  - POST
  - 接口为/user/register

- 完成了updateAccountInfo(),用于更新用户信息，使用token，管理员和用户均可修改，用户只能改自己的信息，管理员根据传进来的id改
  - PUT
  - 接口为/user/updateUserInfo

- 完成了getUserListByPage()，用于分页获取用户列表，返回的是Page对象，其中包含了用户列表等，详见entity.Page，使用token，仅有管理员可以调用此接口
  - POST
  - 接口为/user/userListByPage

- 完成了getPassword，用于找回密码，使用token，传进来的邮箱和token中的用户id对应的邮箱一致时才可找回，将密码发送到用户邮箱并更新账户信息，目前发送邮件的邮箱为1415596561@qq.com(CH的邮箱)
  - POST
  - 接口为/user/getPwd

- 完成了updatePassword,用于修改密码
  - 你首先需要调用getSalt接口，获取盐值并将新旧密码加盐加密
  - 你需要传4个json格式的参数，分别是userId,oldPassword,newPassword,salt
  - 从token中获取id，如果是管理员，则修改userId的密码为newPassword，如果是用户，则验证它的oldPassword，如果成功，则设置密码为newPassword
  - PUT
  - 接口为/user/updatePwd

- 完成了deleteUser，用于删除用户，管理员和用户均可删除，用户只能删除自己，管理员根据传进来的id删，我们在Log表设置了on delete set null，所以对应的登录日志不会删除，仅能将登录日志条目的userId设为null
  - DELETE
  - 接口为/user/deleteUser


# PostController



# ResourceController

- 目前已添加token验证

- 接口前缀/resource

目前我将上传的资源放在/resource/file中，封面放在/resource/image中

- uploadImage已完成，将返回图片路径，RestBean\<String>
  - POST
  - 测试方法：body选form-data，参数名填image，参数值类型选File,然后上传你的文件
  - 例如：<img src="C:\Users\CH\AppData\Roaming\Typora\typora-user-images\image-20230526195821307.png" alt="image-20230526195821307" style="zoom:50%;" />
  - 接口/uploadImage
- uploadFile已完成，将返回文件路径，RestBean\<String>
  - POST
  - 同上，参数名注意要改为file
  - 接口/uploadFile

- uploadResource已完成，注意subject只能有一个，categories是List\<Category>
  - POST
  - /uploadResource
- getResourceDetail已完成，返回RestBean\<ResourceDetail>
  - GET
  - /getResDetail
- getResourceByClassWithPage已完成，按subject和categories按页查询资源，返回RestBean\<Page\<ResourceDetail>>
  - GET
  - /listResByClassWithPage
- updateResource已完成，不仅需要传修改过的信息，未修改的信息也要传过来，资源封面和资源路径不可修改返回RestBean\<ResourceDetail>
  - PUT
  - /updateResource
- deleteResource已完成
  - DELEET
  - /deleteResource
- downloadResource已完成，成功增加下载量和下载记录
  - PUT
  - /downloadResource