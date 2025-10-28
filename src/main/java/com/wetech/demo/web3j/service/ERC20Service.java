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

    private final Web3j web3j;
    private final Credentials credentials;
    private final ERC20Test contract;

    public ERC20Service(Web3j web3j,
                        @Value("${web3j.private-key}") String privateKey,
                        @Value("${web3j.contract-address}") String contractAddress) {

        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("请在 application.yml 中配置 web3.private-key");
        }
        if (contractAddress == null || contractAddress.isEmpty()) {
            throw new IllegalArgumentException("请在 application.yml 中配置 web3.contract-address");
        }

        this.web3j = web3j;
        this.credentials = Credentials.create(privateKey);
        this.contract = ERC20Test.load(contractAddress, web3j, credentials, new DefaultGasProvider());
        System.out.println("当前钱包地址: " + credentials.getAddress());
    }

    /** 增发代币 */
    public String mint(String to, BigInteger amount) throws Exception {
        return contract.mint(to, amount).send().getTransactionHash();
    }

    /** 转账 */
    public String transfer(String to, BigInteger amount) throws Exception {
        return contract.transfer(to, amount).send().getTransactionHash();
    }

    /** 查询余额 */
    public BigInteger balanceOf(String address) throws Exception {
        return contract.balanceOf(address).send();
    }

    /** 授权额度 */
    public String approve(String spender, BigInteger amount) throws Exception {
        return contract.approve(spender, amount).send().getTransactionHash();
    }

    /** 代理转账 */
    public String transferFrom(String from, String to, BigInteger amount) throws Exception {
        return contract.transferFrom(from, to, amount).send().getTransactionHash();
    }

    @Value("${web3j.spender-private-key}")
    private String spenderPrivateKey;

    /** 代理转账（由配置好的 Spender 发起） */
    public String transferFromBySpender(String from, String to, BigInteger amount) throws Exception {
        var spenderCred = Credentials.create(spenderPrivateKey);
        var spenderContract = ERC20Test.load(contract.getContractAddress(), web3j, spenderCred, new DefaultGasProvider());
        return spenderContract.transferFrom(from, to, amount).send().getTransactionHash();
    }


}
