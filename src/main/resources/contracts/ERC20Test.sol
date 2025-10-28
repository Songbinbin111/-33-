// SPDX-License-Identifier: MIT
pragma solidity ^0.8.27;

//import "https://cdn.jsdelivr.net/gh/OpenZeppelin/openzeppelin-contracts@v5.0.1/contracts/token/ERC20/ERC20.sol";
//import "https://cdn.jsdelivr.net/gh/OpenZeppelin/openzeppelin-contracts@v5.0.1/contracts/access/Ownable.sol";
//import "./openzeppelin/ERC20.sol";
//import "./openzeppelin/Ownable.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract ERC20Test is ERC20, Ownable {
    constructor(string memory name_, string memory symbol_)
        ERC20(name_, symbol_)
        Ownable(msg.sender)
    {
        _mint(msg.sender, 1000 * 10 ** decimals());
    }

    function decimals() public pure override returns (uint8) {
        return 6;
    }

    function mint(address to, uint256 amount) public onlyOwner {
        _mint(to, amount);
    }

    function burn(uint256 amount) public {
        _burn(msg.sender, amount);
    }
}