// packages/hardhat/deploy/01_deploy_SHB_erc20.ts
import { DeployFunction } from "hardhat-deploy/types";
import { HardhatRuntimeEnvironment } from "hardhat/types";

// 部署SHBToken合约
const deploySHBToken: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployments, getNamedAccounts } = hre;
  const { deploy } = deployments;
  const { deployer } = await getNamedAccounts();
  // 部署合约
  await deploy("SHBToken", {
    from: deployer,
    args: [],
    log: true,
    autoMine: true,
  });
};
deploySHBToken.tags = ["ERC20SHB202330551321"];
export default deploySHBToken;