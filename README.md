# 任务2.1：基于Web3J的区块链后台应用实现
本目录包含《区块链技术与应用》课程大作业（第33组）中**任务2.1（Web3J构建后台应用）** 的完整代码与操作说明，核心功能是实现ERC20代币合约的部署、集成及接口调用，技术栈为Spring Boot + Web3j。


## 一、项目概述
- **功能定位**：作为整个区块链应用的后端服务，提供ERC20代币的核心操作接口（增发、转账、余额查询、授权、代理转账），对接区块链测试网络（如BSC Testnet、POTOS Testnet）。
- **代码结构**：
  ```
  backend/
  ├── src/
  │   ├── main/
  │   │   ├── java/com/wetech/demo/web3j/
  │   │   │   ├── contracts/ERC20Test.java  # Web3j生成的ERC20合约Java封装类
  │   │   │   ├── service/ERC20Service.java  # 合约交互业务逻辑层
  │   │   │   └── controller/ERC20Controller.java  # HTTP接口层
  │   │   └── resources/
  │   │       ├── contracts/ERC20Test.sol  # ERC20代币合约源文件
  │   │       └── application.yml  # 配置文件（私钥、合约地址、Web3j节点等）
  │   └── test/  # 测试代码目录（可选）
  ├── build.gradle  # 项目构建配置
  └── README.md  # 本说明文档
  ```


## 二、环境准备
在运行本项目前，需先配置以下环境依赖，版本需与下表一致（避免兼容性问题）：

| 环境组件         | 版本/说明                  |
|------------------|---------------------------|
| 操作系统         | Windows 10                |
| JDK              | 21.0.8                    |
| Web3j            | 4.14.0（核心库）/ 1.7.0（CLI工具） |
| Solidity 编译器  | solcjs 0.8.30             |
| Node.js          | 最新稳定版（用于安装solcjs） |
| 构建工具         | Gradle（项目自带Wrapper，无需额外安装） |
| 区块链测试网络   | BSC Testnet / POTOS Testnet（需提前准备测试网ETH/Gas） |


## 三、核心实现步骤
### 3.1 1. 下载Spring Boot Web3j示例代码
首先克隆基础示例仓库，搭建Spring Boot与Web3j的集成框架：
```bash
# 1. 创建本地项目目录
mkdir BlockChain && cd BlockChain

# 2. 克隆示例代码
git clone https://github.com/kyonRay/spring-boot-web3j

# 3. 用IDEA打开项目，等待Gradle依赖同步完成（首次同步可能耗时较长）
```
同步成功标志：IDEA右侧`Gradle`面板显示`spring-boot-web3j`项目，且无依赖报错。


### 3.2 2. 集成ERC20Test合约（关键步骤）
由于直接使用`gradlew generateContractWrappers`存在**Gradle插件不兼容、Solidity插件停止维护**等问题，改用Web3j CLI工具手动生成合约Java封装类，步骤如下：

#### 2.1 安装Solidity编译器（solcjs）
```bash
# 全局安装solcjs（依赖Node.js）
npm install -g solc

# 验证安装成功（显示0.8.30版本）
solcjs --version
```

#### 2.2 编译ERC20Test.sol合约
1. 将`ERC20Test.sol`合约文件放入项目目录：`src/main/resources/contracts/`  
2. 执行编译命令，生成ABI和BIN文件：
   ```bash
   # 进入项目根目录（spring-boot-web3j）
   cd spring-boot-web3j

   # 编译合约，输出到build目录
   solcjs --bin --abi src/main/resources/contracts/ERC20Test.sol -o build --include-path node_modules --base-path .
   ```
   编译成功后，`build`目录下会生成：
   - `ERC20Test_sol_ERC20Test.abi`（合约接口描述文件）
   - `ERC20Test_sol_ERC20Test.bin`（合约字节码文件）

#### 2.3 用Web3j CLI生成Java合约封装类
1. 下载Web3j CLI工具并解压：
   ```bash
   # 创建tools目录，下载CLI压缩包
   mkdir -p tools && Invoke-WebRequest -Uri "https://github.com/web3j/web3j-cli/releases/download/v1.7.0/web3j-cli-shadow-1.7.0.zip" -OutFile "tools/web3j-cli.zip"

   # 解压到tools/web3j-cli
   Expand-Archive -Path "tools\web3j-cli.zip" -DestinationPath "tools\web3j-cli"
   ```
2. 生成Java类（指定输出目录和包名）：
   ```bash
   # 执行Web3j CLI命令，生成ERC20Test.java
   & "tools\web3j-cli\web3j-cli-shadow-1.7.0\bin\web3j.bat" generate solidity `
   --binFile "build\ERC20Test_sol_ERC20Test.bin" `
   --abiFile "build\ERC20Test_sol_ERC20Test.abi" `
   --outputDir "src\main\java" `
   --package "com.wetech.demo.web3j.contracts" `
   --contractName "ERC20Test"
   ```
   生成成功后，在`src/main/java/com/wetech/demo/web3j/contracts/`目录下会出现`ERC20Test.java`（合约的Java操作封装类）。


### 3.3 3. 核心业务代码实现
#### 3.1 服务层：ERC20Service.java（合约交互逻辑）
封装ERC20代币的核心操作（增发、转账、余额查询等），负责与区块链节点交互：
```java
package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.ERC20Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import java.math.BigInteger;

@Service
public class ERC20Service {
    // Web3j客户端（用于连接区块链节点）
    private final Web3j web3j;
    // 钱包凭证（私钥生成，用于签名交易）
    private final Credentials credentials;
    // ERC20合约实例（通过合约地址加载）
    private final ERC20Test contract;

    // 构造函数：初始化Web3j、Credentials、合约实例
    public ERC20Service(Web3j web3j,
                        @Value("${web3j.private-key}") String privateKey,
                        @Value("${web3j.contract-address}") String contractAddress) {
        // 校验配置（私钥和合约地址不可为空）
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("请在application.yml中配置web3j.private-key");
        }
        if (contractAddress == null || contractAddress.isEmpty()) {
            throw new IllegalArgumentException("请在application.yml中配置web3j.contract-address");
        }

        this.web3j = web3j;
        this.credentials = Credentials.create(privateKey); // 从私钥生成凭证
        this.contract = ERC20Test.load(contractAddress, web3j, credentials, new DefaultGasProvider()); // 加载合约

        // 打印当前钱包地址（用于验证配置是否正确）
        System.out.println("当前钱包地址: " + credentials.getAddress());
    }

    /**
     * 增发代币（仅合约拥有者可调用）
     * @param to 接收增发代币的地址
     * @param amount 增发数量（注意：需与合约decimals一致，如18位小数则传1000000000000000000代表1个代币）
     * @return 交易哈希（上链成功后返回）
     */
    public String mint(String to, BigInteger amount) throws Exception {
        return contract.mint(to, amount).send().getTransactionHash();
    }

    /**
     * 普通转账（从当前钱包向目标地址转账）
     * @param to 目标地址
     * @param amount 转账数量
     * @return 交易哈希
     */
    public String transfer(String to, BigInteger amount) throws Exception {
        return contract.transfer(to, amount).send().getTransactionHash();
    }

    /**
     * 查询指定地址的代币余额
     * @param address 待查询地址
     * @return 余额（BigInteger类型，需自行处理小数位）
     */
    public BigInteger balanceOf(String address) throws Exception {
        return contract.balanceOf(address).send();
    }

    /**
     * 授权第三方账户（spender）代为转账
     * @param spender 被授权地址
     * @param amount 授权额度
     * @return 交易哈希
     */
    public String approve(String spender, BigInteger amount) throws Exception {
        return contract.approve(spender, amount).send().getTransactionHash();
    }

    /**
     * 代理转账（被授权账户从原账户向目标账户转账）
     * @param from 原账户地址（需已授权给当前钱包）
     * @param to 目标账户地址
     * @param amount 转账数量（不可超过授权额度）
     * @return 交易哈希
     */
    public String transferFrom(String from, String to, BigInteger amount) throws Exception {
        return contract.transferFrom(from, to, amount).send().getTransactionHash();
    }
}
```

#### 3.2 控制层：ERC20Controller.java（HTTP接口暴露）
将服务层功能封装为REST接口，支持通过命令行或Postman调用：
```java
package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20Service;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/erc20") // 接口统一前缀
public class ERC20Controller {
    private final ERC20Service erc20Service;

    // 依赖注入ERC20Service
    public ERC20Controller(ERC20Service erc20Service) {
        this.erc20Service = erc20Service;
    }

    /**
     * 增发代币接口（POST）
     * 调用示例：http://localhost:8080/api/erc20/mint?to=0x...&amount=1000000000000000000
     */
    @PostMapping("/mint")
    public String mint(@RequestParam String to, @RequestParam BigInteger amount) throws Exception {
        return erc20Service.mint(to, amount);
    }

    /**
     * 普通转账接口（POST）
     * 调用示例：http://localhost:8080/api/erc20/transfer?to=0x...&amount=1000000000000000000
     */
    @PostMapping("/transfer")
    public String transfer(@RequestParam String to, @RequestParam BigInteger amount) throws Exception {
        return erc20Service.transfer(to, amount);
    }

    /**
     * 余额查询接口（GET）
     * 调用示例：http://localhost:8080/api/erc20/balanceOf?address=0x...
     */
    @GetMapping("/balanceOf")
    public BigInteger balanceOf(@RequestParam String address) throws Exception {
        return erc20Service.balanceOf(address);
    }

    /**
     * 授权接口（POST）
     * 调用示例：http://localhost:8080/api/erc20/approve?spender=0x...&amount=1000000000000000000
     */
    @PostMapping("/approve")
    public String approve(@RequestParam String spender, @RequestParam BigInteger amount) throws Exception {
        return erc20Service.approve(spender, amount);
    }

    /**
     * 代理转账接口（POST）
     * 调用示例：http://localhost:8080/api/erc20/transferFrom?from=0x...&to=0x...&amount=1000000000000000000
     */
    @PostMapping("/transferFrom")
    public String transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigInteger amount) throws Exception {
        return erc20Service.transferFrom(from, to, amount);
    }
}
```

#### 3.3 配置文件：application.yml
在`src/main/resources/`下创建`application.yml`，配置Web3j节点、私钥（测试网私钥，勿用主网！）：
```yaml
server:
  port: 8080 # 后端服务端口

web3j:
  private-key: "0x你的测试网钱包私钥" # 如0x34fc1195edfca5b5e6acc119ea251d4ce92381e7789ec9f024dc5c1bcefc5aff
  contract-address: "" # 合约部署后填写（初始为空，部署成功后从控制台获取）
  client-address: "https://rpc.ankr.com/bsc_testnet_chapel" # BSC测试网节点（或POTOS测试网节点）
```


### 3.4 4. 运行与合约部署
#### 4.1 启动Spring Boot项目
1. 在IDEA中找到`SpringBootApplication.java`（项目入口类），右键`Run`启动项目；  
2. 启动成功标志：控制台打印`当前钱包地址: 0x...`，且无报错。

#### 4.2 部署ERC20合约到测试网
通过`/api/storage/deploy`接口部署合约（需确保测试网钱包有足够Gas）：
```bash
# 使用curl命令调用部署接口（Windows PowerShell或Linux终端均可）
curl -X POST http://localhost:8080/api/storage/deploy
```
部署成功后，控制台会返回合约地址（如`0xb66271ff229C8b68C0f88b0686a31ebE14E851B7`），需将此地址填入`application.yml`的`web3j.contract-address`中，重启项目使配置生效。


### 3.5 5. 接口调用测试
所有接口均支持通过命令行（PowerShell）或Postman调用，以下为完整测试示例：

| 接口功能       | 调用命令（PowerShell）                                                                 | 返回结果说明                     |
|----------------|---------------------------------------------------------------------------------------|----------------------------------|
| 增发代币       | `Invoke-WebRequest -Uri "http://localhost:8080/api/erc20/mint?to=0x...&amount=5000000000000000000000" -Method POST` | 交易哈希（如0x4f616c2f7688f784fd935f4d487b98bdf91a89cf60a11e03274c851203d4520d） |
| 普通转账       | `Invoke-WebRequest -Uri "http://localhost:8080/api/erc20/transfer?to=0x...&amount=1000000000000000000000" -Method POST` | 交易哈希                         |
| 查询余额       | `Invoke-WebRequest -Uri "http://localhost:8080/api/erc20/balanceOf?address=0x..." -Method GET` | 余额数值（如5000000000000000000000） |
| 授权           | `Invoke-WebRequest -Uri "http://localhost:8080/api/erc20/approve?spender=0x...&amount=500000000000000000000" -Method POST` | 交易哈希                         |
| 代理转账       | `Invoke-WebRequest -Uri "http://localhost:8080/api/erc20/transferFrom?from=0x...&to=0x...&amount=200000000000000000000" -Method POST` | 交易哈希                         |

**验证交易**：可将返回的交易哈希复制到对应测试网的区块浏览器（如BSC Testnet浏览器：https://testnet.bscscan.com/），查看交易状态是否为“Success”。


## 四、常见问题排查
1. **Gradle依赖同步失败**  
   - 解决方案：删除项目根目录的`.gradle`文件夹，重启IDEA，重新同步；或检查网络是否能访问Maven中央仓库。

2. **Solc安装失败（npm报错）**  
   - 解决方案：升级Node.js到最新稳定版，执行`npm cache clean -f`清理缓存后重新安装。

3. **合约部署时报“Insufficient funds”**  
   - 原因：测试网钱包Gas不足；  
   - 解决方案：到对应测试网的 faucet（水龙头）领取测试币（如BSC Testnet faucet：https://testnet.bscscan.com/faucet/bsctestnet）。

4. **接口调用时报“Contract not found”**  
   - 原因：`application.yml`中的`contract-address`未配置或配置错误；  
   - 解决方案：重新部署合约，获取正确的合约地址填入配置文件，重启项目。


## 五、备注
1. **私钥安全**：测试网私钥可公开用于调试，但**主网私钥绝对不能提交到Git仓库**！建议在`gitignore`中添加`application.yml`，避免配置文件泄露。  
2. **小数位处理**：ERC20合约默认18位小数，接口中`amount`参数需传入“实际数量 × 10^18”（如1个代币传`1000000000000000000`）。  
3. **与前端协作**：前端DApp（任务2.2）可通过调用本后端的`/api/erc20/*`接口，实现ERC20代币的可视化操作（如转账、余额显示）。
