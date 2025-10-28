package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/erc20")
public class ERC20Controller {

    private final ERC20Service erc20Service;

    public ERC20Controller(ERC20Service erc20Service) {
        this.erc20Service = erc20Service;
    }

    /** 增发代币 */
    @PostMapping("/mint")
    public String mint(@RequestParam String to, @RequestParam BigInteger amount) throws Exception {
        return erc20Service.mint(to, amount);
    }

    /** 普通转账 */
    @PostMapping("/transfer")
    public String transfer(@RequestParam String to, @RequestParam BigInteger amount) throws Exception {
        return erc20Service.transfer(to, amount);
    }

    /** 查询余额 */
    @GetMapping("/balanceOf")
    public BigInteger balanceOf(@RequestParam String address) throws Exception {
        return erc20Service.balanceOf(address);
    }

    /** 授权额度 */
    @PostMapping("/approve")
    public String approve(@RequestParam String spender, @RequestParam BigInteger amount) throws Exception {
        return erc20Service.approve(spender, amount);
    }

    /** ✅ 普通版 transferFrom（使用当前钱包发起） */
    @PostMapping("/transferFrom")
    public String transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigInteger amount) throws Exception {
        return erc20Service.transferFrom(from, to, amount);
    }

    /** ✅ 代理版 transferFrom（使用配置的 spender-private-key 发起） */
    @PostMapping("/transferFromBySpender")
    public String transferFromBySpender(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigInteger amount) throws Exception {
        return erc20Service.transferFromBySpender(from, to, amount);
    }
}
