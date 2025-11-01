前端部分实验代码以及成果展示：
以下是实验成果截图展示（不知道为什么视频发布不上来）：
打开自己的前端地址 http://localhost:3000

<img width="414" height="202" alt="image" src="https://github.com/user-attachments/assets/a028a4d9-d37c-4a75-a0fb-8d84bd17372d" />

连接我的钱包：

<img width="415" height="211" alt="image" src="https://github.com/user-attachments/assets/2ff91496-206d-48e9-9de1-e01fdfd06475" />

成功界面:

<img width="415" height="201" alt="image" src="https://github.com/user-attachments/assets/2e7effba-cbd8-4639-9e2d-6299d8f8011a" />

1、调用合约所有实现接口的截图以及文字说明（mint、transfer、balanceOf、approve、transferFrom）
1)进入合约调试页面
在前端首页顶部导航栏，点击Debug Contracts；

<img width="415" height="209" alt="image" src="https://github.com/user-attachments/assets/d80c28d1-3607-4ad2-82ec-0c7f0e9b5b37" />

加载成功后会显示合约的所有接口（上图）。
2）1. 调用「mint」接口
操作步骤：
在页面中找到 「Write」 标签页，下拉找到 「mint」 方法；
填写参数：
to：我的钱包地址；
amount： 1000000000000000000（代表 1 个 SHB 代币）；
点击 「Write」 按钮，MetaMask 会弹出交易确认窗口；

<img width="414" height="145" alt="image" src="https://github.com/user-attachments/assets/ab717f22-490b-4e4d-a1f5-9bdb8d9e182f" />

点击 「确认」；
等待 10-30 秒（上链时间），前端提示「Transaction successful」即成功；

<img width="415" height="450" alt="image" src="https://github.com/user-attachments/assets/16e2958c-3cd8-431b-8c1a-a8f2ae32b2fe" />

跳转区块浏览器，查看交易详情。

<img width="416" height="211" alt="image" src="https://github.com/user-attachments/assets/151f61fe-9a27-4731-8b71-a0bf2b35da5b" />

3） 调用「balanceOf（查询余额）接口
操作步骤：
找到 「balanceOf」 方法，输入钱包地址；
点击 「Read」，显示 SHB 代币余额，确认余额正确。

<img width="415" height="194" alt="image" src="https://github.com/user-attachments/assets/30e18c52-9a5f-4dcf-bf5c-0dd1ebee1713" />

4）调用「transfer」
操作步骤：
找到 「transfer」 方法；
填写参数：
to：输入一个组员陈琼熙的 POTOS Testnet 地址；
amount：输入 500000000000000000（代表转账 0.5 个 SHB 代币）；
点击「Write」→ 确认 MetaMask 交易 ；

<img width="415" height="191" alt="image" src="https://github.com/user-attachments/assets/9388a1b6-8242-42e9-ae5b-6a7e80e549da" />

<img width="312" height="352" alt="image" src="https://github.com/user-attachments/assets/e37ebfc4-5009-483c-8660-0e995b6b7f79" />

打开区块浏览器，记录交易信息。

<img width="414" height="193" alt="image" src="https://github.com/user-attachments/assets/9f4c3193-200d-4b39-8ebc-c338193a9168" />

5）调用「approve」+「transferFrom」接口
先创建一个account2账户，并且持币方为account1账户，allowance判断是否授权：

<img width="416" height="224" alt="image" src="https://github.com/user-attachments/assets/ba5f6d4f-59bb-4452-9471-b7f90ce88584" />

返回值为0，需要调用approve授权额度：

<img width="415" height="187" alt="image" src="https://github.com/user-attachments/assets/b1ba4684-4eea-4749-85aa-c8b1ebd24868" />

成功后执行transferfrom

<img width="415" height="171" alt="image" src="https://github.com/user-attachments/assets/4f875c85-350e-4897-ab3f-1ac40d0ebcff" />

查看交易详情：

<img width="284" height="379" alt="image" src="https://github.com/user-attachments/assets/6b4a7d38-bca5-4b40-b453-24ad8d550686" />

2、验证接口调用成功截图以及文字说明
所有操作如下:

<img width="415" height="182" alt="image" src="https://github.com/user-attachments/assets/117f6456-a4f7-4ff9-a162-78587f9ae923" />

后端显示：：

<img width="416" height="186" alt="image" src="https://github.com/user-attachments/assets/5f66e738-bc65-4890-93dd-11e5498784fc" />
