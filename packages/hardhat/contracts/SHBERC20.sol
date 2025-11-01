
pragma solidity ^0.8.20;


import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";


contract SHBToken is ERC20, Ownable {

    constructor() ERC20("SongHanBinToken", "SHB") Ownable(msg.sender) {}


    function mint(address to, uint256 amount) public onlyOwner {
        _mint(to, amount);
    }


    function transfer(address to, uint256 value) public override returns (bool) {
        return super.transfer(to, value);
    }


    function approve(address spender, uint256 value) public override returns (bool) {
        return super.approve(spender, value);
    }


    function transferFrom(
        address from,
        address to,
        uint256 value
    ) public override returns (bool) {
        return super.transferFrom(from, to, value);
    }


    function balanceOf(address account) public view override returns (uint256) {
        return super.balanceOf(account);
    }
}                                                                                                                                                                                      