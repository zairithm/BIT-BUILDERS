const hre = require("hardhat");

async function main() {
  const Rayzo = await hre.ethers.getContractFactory("RayzoAuditLayer");

  const rayzo = await Rayzo.deploy();

  await rayzo.waitForDeployment();

  const address = await rayzo.getAddress();

  console.log("RayzoAuditLayer deployed to:", address);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});