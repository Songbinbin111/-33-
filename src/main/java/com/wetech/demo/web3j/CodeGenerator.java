package com.wetech.demo.web3j;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

public class CodeGenerator {
    public static void main(String[] args) throws Exception {
        String[] parameters = {
                "-b", "src/main/resources/contracts/build/ERC20Test.bin",
                "-a", "src/main/resources/contracts/build/ERC20Test.abi",
                "-o", "src/main/java",
                "-p", "com.wetech.demo.web3j.contracts"
        };

        SolidityFunctionWrapperGenerator.main(parameters);
        System.out.println("合约包装类生成成功！");
    }
}